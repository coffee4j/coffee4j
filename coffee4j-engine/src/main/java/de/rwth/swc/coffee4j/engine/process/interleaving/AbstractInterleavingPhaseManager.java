package de.rwth.swc.coffee4j.engine.process.interleaving;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.manager.PhaseManager;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionContext;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionPhase;
import de.rwth.swc.coffee4j.engine.process.phase.model.ModelModificationContext;
import de.rwth.swc.coffee4j.engine.process.phase.model.ModelModificationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.checking.CheckingPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationContext;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.identification.IdentificationPhase;
import de.rwth.swc.coffee4j.engine.report.DelegatingInterleavingExecutionReporter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract class implementing the {@link PhaseManager} Interface for Interleaving CT. It provides general functionality
 * and fields used by all sub-classes.
 *
 * <p>
 *     Manages a {@link ResultCache} to execute every test input exactly once. If a test input has been executed before,
 *     the {@link PhaseManager} loads the result from the cache instead of executing the {@link ExecutionPhase}.
 * </p>
 */
public abstract class AbstractInterleavingPhaseManager implements PhaseManager {
    
    protected final ResultCache cache = new HashMapResultCache();
    
    protected InterleavingPhaseManagerConfiguration configuration;
    
    protected ExecutionContext executionContext;
    protected InterleavingGenerationContext generationContext;
    
    protected ExtensionExecutor extensionExecutor;
    protected ExecutionPhase executionPhase;
    protected InterleavingGenerationPhase generationPhase;
    protected IdentificationPhase identificationPhase;
    protected CheckingPhase checkingPhase;

    public AbstractInterleavingPhaseManager(InterleavingPhaseManagerConfiguration configuration) {
        this.configuration = Preconditions.notNull(configuration);
    }

    @Override
    public void run() {
        executeModelModificationPhase();
        createContexts();
        createPhases();
        
        Map<Combination, TestResult> executionResult;
        Combination nextTestInput = generationPhase.execute(new HashMap<>());

        while (nextTestInput != null) {
            executionResult = runTestInput(nextTestInput);
            TestResult result = executionResult.get(nextTestInput);

            // test input passed -> no identification necessary
            if (result.isSuccessful()) {
                nextTestInput = generationPhase.execute(executionResult);
            // test input failed -> start identification phase
            } else {
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
                nextTestInput = generationPhase.execute(executionResult);
            }
        }

        // provide all test inputs in cache that triggered an ErrorConstraintException
        executeClassificationPhase(((HashMapResultCache) cache)
                .getResults()
                .entrySet()
                .stream()
                .filter(testInput -> testInput.getValue().isExceptionalSuccessful())
                .filter(testInput -> testInput.getValue().getResultValue().orElse(null) instanceof ErrorConstraintException)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
    
    private void executeModelModificationPhase() {
        extensionExecutor = configuration.getExtensionExecutorFactory().create(configuration.getExtensions());
        
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
        executionContext = ExecutionContext.createExecutionContext(
                extensionExecutor,
                configuration.getTestMethodConfiguration(),
                configuration.getExecutionConfiguration().getExecutionReporters());
        
        generationContext = new InterleavingGenerationContext(
                configuration.getExecutionConfiguration(),
                configuration.getTestMethodConfiguration().getInputParameterModel(),
                extensionExecutor);
    }
    
    protected void createPhases() {
        executionPhase = configuration.getExecutionPhaseFactory().create(executionContext);
        executionPhase.setExecutionMode(configuration.getExecutionConfiguration().getExecutionMode());
        
        generationPhase = configuration.getGenerationPhaseFactory()
                .create(generationContext);
        
        identificationPhase = configuration.getIdentificationPhaseFactory()
                .create(generationContext);
        
        checkingPhase = configuration.getCheckingPhaseFactory()
                .create(generationContext);
    }

    /**
     * Template method implemented by error-constraint generating managers.
     * @param errorConstraintExceptionCausingTestInputs map containing all executed {@link Combination}s and the
     *                                                  corresponding {@link TestResult}s causing an exceptional-pass.
     */
    protected abstract void executeClassificationPhase(Map<Combination, TestResult> errorConstraintExceptionCausingTestInputs);

    /**
     * passes test input to execution phase if result is not present in cache.
     *
     * @param nextTestInput test input to obtain result for.
     * @return executed test input with corresponding execution result.
     */
    protected Map<Combination, TestResult> runTestInput(Combination nextTestInput) {
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
    
}
