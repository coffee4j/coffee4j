package de.rwth.swc.coffee4j.engine.process.interleaving;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.Constraint;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.manager.PhaseManager;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionContext;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionPhase;
import de.rwth.swc.coffee4j.engine.process.phase.model.ModelModificationContext;
import de.rwth.swc.coffee4j.engine.process.phase.model.ModelModificationPhase;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.tuplebased.TupleBasedConstraint;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.DefaultGeneratingInterleavingManager;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingCombinatorialTestManager;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.checking.CheckingPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.classification.InterleavingClassificationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationContext;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.identification.IdentificationPhase;
import de.rwth.swc.coffee4j.engine.report.DelegatingInterleavingExecutionReporter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation managing the fully-automated hybrid approach by extending {@link AbstractInterleavingPhaseManager}.
 * After identifying exception-inducing combinations, this manager updates the IPM, re-initializes all phases and
 * performs
 * combinatorial testing subsequently.
 */
public class DefaultDynamicInterleavingPhaseManager implements PhaseManager {
    
    private final ResultCache cache = new HashMapResultCache();
    
    private DynamicInterleavingPhaseManagerConfiguration configuration;
    
    private ExecutionContext executionContext;
    private InterleavingGenerationContext generationContext;

    private ExtensionExecutor extensionExecutor;
    private ExecutionPhase executionPhase;
    private InterleavingGenerationPhase generationPhase;
    private IdentificationPhase identificationPhase;
    private CheckingPhase checkingPhase;
    private InterleavingClassificationPhase classificationPhase;

    private final InputParameterModel initialInputParameterModel;

    /**
     * @param configuration {@link DynamicInterleavingPhaseManagerConfiguration} containing components needed to run this process
     *                                                                 like factories for the different phases.
     */
    public DefaultDynamicInterleavingPhaseManager(DynamicInterleavingPhaseManagerConfiguration configuration) {
        this.configuration = Preconditions.notNull(configuration);
        this.initialInputParameterModel = Preconditions.notNull(configuration.getTestMethodConfiguration().getInputParameterModel());
    }

    @Override
    public void run() {
        executeModelModificationPhase();
        createContexts();
        createPhases();
        
        // Error-Constraint Generation Phase
        runOneInterleavingTestingIteration();

        executeClassificationPhase();

        // update generation context and create new phases for testing-phase
        updateConfiguration();

        // Interleaving Combinatorial Testing Phase
        runOneInterleavingTestingIteration();
    }
    
    private void executeModelModificationPhase() {
        this.extensionExecutor = configuration.getExtensionExecutorFactory().create(configuration.getExtensions());
        
        final ModelModificationContext context = new ModelModificationContext(extensionExecutor,
                new DelegatingInterleavingExecutionReporter(configuration.getExecutionConfiguration().getExecutionReporters()));
        final ModelModificationPhase phase = configuration.getModelModificationPhaseFactory().create(context);
        final InputParameterModel model = configuration.getTestMethodConfiguration().getInputParameterModel();
        final InputParameterModel modifiedModel = phase.execute(model);
        
        configuration = configuration.toBuilder()
                .testMethodConfiguration(configuration.getTestMethodConfiguration().toBuilder()
                        .inputParameterModel(modifiedModel)
                        .build())
                .build();
    }
    
    private void createContexts() {
        this.executionContext = ExecutionContext.createExecutionContext(
                extensionExecutor,
                configuration.getTestMethodConfiguration(),
                configuration.getExecutionConfiguration().getExecutionReporters());
        
        this.generationContext = new InterleavingGenerationContext(
                configuration.getErrorConstraintGenerationExecutionConfiguration(),
                initialInputParameterModel,
                extensionExecutor);
    }
    
    private void createPhases() {
        executionPhase = configuration.getExecutionPhaseFactory()
                .create(executionContext);
        generationPhase = configuration.getGenerationPhaseFactory()
                .create(generationContext);
        identificationPhase = configuration.getIdentificationPhaseFactory()
                .create(generationContext);
        checkingPhase = configuration.getCheckingPhaseFactory()
                .create(generationContext);
        classificationPhase = configuration.getClassificationPhaseFactory()
                .create(generationContext);
    }

    private void runOneInterleavingTestingIteration() {
        Map<Combination, TestResult> executionResult;
        Combination nextTestInput = generationPhase.execute(new HashMap<>());

        while (nextTestInput != null) {
            executionResult = runTestInput(nextTestInput);
            TestResult result = executionResult.get(nextTestInput);

            // test input failed -> start inspection
            if (!result.isSuccessful()) {
                // identify possible fics
                extensionExecutor.executeBeforeFaultCharacterization(executionResult);
                nextTestInput = identificationPhase.initialize(nextTestInput, result);

                List<Combination> generatedTestInputsDuringIdentification = Collections.emptyList();

                while (!checkingPhase.failureInducingCombinationsFound()) {
                    generatedTestInputsDuringIdentification = new ArrayList<>();

                    while (nextTestInput != null) {
                        generatedTestInputsDuringIdentification.add(nextTestInput);
                        executionResult = runTestInput(nextTestInput);
                        nextTestInput = identificationPhase.execute(executionResult);
                    }

                    nextTestInput = checkingPhase.initialize();

                    while (nextTestInput != null) {
                        while (nextTestInput != null) {
                            executionResult = runTestInput(nextTestInput);

                            nextTestInput = checkingPhase.execute(executionResult);
                        }

                        // restart checking phase until all possibly failure-inducing combinations are checked
                        // if one is not correctly identified, restart identification phase (incorrectly identified
                        // combinations will not be returned again as there is a passing test input)
                        nextTestInput = checkingPhase.initialize();
                    }

                    if (!checkingPhase.failureInducingCombinationsFound()) {
                        nextTestInput = identificationPhase.reinitialize();
                    }
                }

                extensionExecutor.executeAfterFaultCharacterization(generatedTestInputsDuringIdentification);
            }
            nextTestInput = generationPhase.execute(executionResult);
        }
    }
    
    /**
     * passes test input to execution phase if result is not present in cache.
     *
     * @param nextTestInput test input to obtain result for.
     * @return executed test input with corresponding execution result.
     */
    private Map<Combination, TestResult> runTestInput(Combination nextTestInput) {
        Map<Combination, TestResult> executionResult;

        if (!cache.containsResultFor(nextTestInput)) {
            executionResult = executionPhase.execute(Collections.singletonList(nextTestInput));
            cache.addResultIfAbsentFor(nextTestInput, executionResult.get(nextTestInput));
        } else {
            executionResult = new HashMap<>();
            executionResult.put(nextTestInput, cache.getResultFor(nextTestInput));
        }

        return executionResult;
    }
    
    private void executeClassificationPhase() {
        Map<Combination, TestResult> executionResult;
        Combination nextTestInput = classificationPhase.initialize(((HashMapResultCache) cache)
                .getResults()
                .entrySet()
                .stream()
                .filter(testInput -> testInput.getValue().isExceptionalSuccessful())
                .filter(testInput -> testInput.getValue().getResultValue().orElse(null) instanceof ErrorConstraintException)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        
        while (nextTestInput != null) {
            executionResult = runTestInput(nextTestInput);
            nextTestInput = classificationPhase.execute(executionResult);
        }
    }
    
    private void updateConfiguration() {
        final ModelConverter converter = generationContext.getModelConverter();
        final Set<int[]> foundExceptionInducingCombinations
                = ((DefaultGeneratingInterleavingManager) generationContext.getTestManager())
                        .getMinimalExceptionInducingCombinations()
                        .keySet();
        final Set<Constraint> errorConstraints = new HashSet<>();
        final List<String> parameterNames = initialInputParameterModel.getParameters().stream()
                .map(Parameter::getName)
                .collect(Collectors.toList());
        
        int id = 0;
        
        for (int[] combination : foundExceptionInducingCombinations) {
            errorConstraints.add(new TupleBasedConstraint("exceptionInducingCombination-" + id++, parameterNames, converter.convertCombination(combination)));
        }
        
        final InputParameterModel newInputParameterModel = initialInputParameterModel.toBuilder()
                .errorConstraints(errorConstraints)
                .build();
        final TestMethodConfiguration newTestMethodConfiguration = configuration.getTestMethodConfiguration()
                .toBuilder()
                .inputParameterModel(newInputParameterModel)
                .build();
        configuration = configuration.toBuilder()
                .testMethodConfiguration(newTestMethodConfiguration)
                .build();
        
        createContexts();
        
        InterleavingCombinatorialTestManager newTestManager = generationContext.getTestManager();
        
        // already passing tuples do not need to be considered anymore
        for (Map.Entry<Combination, TestResult> testInput : ((HashMapResultCache) cache).getResults().entrySet()) {
            if ((testInput.getValue().isSuccessful())) {
                newTestManager.updateCoverage(converter.convertCombination(testInput.getKey()));
            }
        }
        
        createPhases();
    }
    
}
