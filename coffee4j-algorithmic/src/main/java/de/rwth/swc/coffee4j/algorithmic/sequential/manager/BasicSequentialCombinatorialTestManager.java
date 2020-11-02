package de.rwth.swc.coffee4j.algorithmic.sequential.manager;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;
import de.rwth.swc.coffee4j.algorithmic.sequential.report.EmptySequentialGenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.sequential.report.GenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper.wrap;
import static de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper.wrapToSet;

/**
 * A very basic manager for combinatorial tests. It is basic in the sense that it does not support any form of test
 * result caching and/or parallel generation of test input groups.
 */
public class BasicSequentialCombinatorialTestManager implements SequentialCombinatorialTestManager {
    
    private final SequentialCombinatorialTestConfiguration configuration;
    
    private final CompleteTestModel model;
    
    private final List<SingleGroupGenerationManager> managers = new ArrayList<>();
    
    public BasicSequentialCombinatorialTestManager(SequentialCombinatorialTestConfiguration configuration,
                                                   CompleteTestModel model) {
        this.configuration = Preconditions.notNull(configuration);
        this.model = Preconditions.notNull(model);
    }

    /**
     * Generates all test input groups given by the supplied {@link TestInputGroupGenerator}s. All test inputs are then
     * returned. During the generation, the method
     * {@link GenerationReporter#testInputGroupGenerated(TestInputGroup, TestInputGroupGenerator)} is called for each
     * generated {@link TestInputGroup}.
     *
     * @return all generated test inputs from all groups. They are returned in the exact order in which the
     * {@link TestInputGroupGenerator}s returned them inside {@link TestInputGroup}s.
     */
    @Override
    public List<int[]> generateInitialTests() {
        return configuration.getGenerators().stream()
                .map(this::generateManagers)
                .flatMap(Collection::stream)
                .map(this::registerManager)
                .map(SingleGroupGenerationManager::generateInitialTests)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
    
    private Set<SingleGroupGenerationManager> generateManagers(TestInputGroupGenerator generator) {
        final GenerationReporter generationReporter = configuration.getGenerationReporter()
                .orElse(new EmptySequentialGenerationReporter());
        
        return generator.generate(model, generationReporter).stream()
                .map(testInputGroupSupplier -> new SingleGroupGenerationManager(
                        testInputGroupSupplier,
                        generator,
                        configuration.getPrioritizer(),
                        configuration.getFaultCharacterizationAlgorithmFactory().orElse(null),
                        generationReporter,
                        configuration.getExecutionMode()))
                .collect(Collectors.toSet());
    }
    
    private SingleGroupGenerationManager registerManager(SingleGroupGenerationManager manager) {
        managers.add(manager);
        return manager;
    }
    
    /**
     * Returns all additional test inputs needed for all {@link TestInputGroup}s managed by this manager.
     * For each managed {@link TestInputGroup} the following flow is used:
     * 1. Check if the test input is contained in either the initial set of generated test inputs (first iteration) or
     * in the requested test inputs for fault characterization (in all other iterations)
     * 1.1 If that is not the input, return an empty list of additionally needed test inputs
     * 1.2 if that is the input, check whether fault characterization is enabled (factory is given, group has configuration,
     * a test input in the initial set failed)
     * 1.2.1 if FL is not enabled, the group is considered finished and will never return test inputs again
     * 1.2.2 else, new test inputs are generated by the fault characterization algorithm provided by the factory and returned
     * <p>
     * When necessary, the method called the necessary methods on a given reporter:
     * -{@link GenerationReporter#faultCharacterizationStarted(TestInputGroup, FaultCharacterizationAlgorithm)}
     * -{@link GenerationReporter#faultCharacterizationTestInputsGenerated(TestInputGroup, List)}
     * -{@link GenerationReporter#faultCharacterizationFinished(TestInputGroup, Map, Set)}
     * -{@link GenerationReporter#testInputGroupFinished(TestInputGroup)}
     *
     * @param testInput  the test inputs for which's result additional test inputs shall be generated
     * @param testResult whether the test input was successful and if not how the failure was caused
     * @return a combined list of test inputs generated by the fault characterization of each test input group
     */
    @Override
    public List<int[]> generateAdditionalTestInputsWithResult(int[] testInput, TestResult testResult) {
        final IntArrayWrapper wrappedTestInputs = wrap(testInput);
        
        return managers.stream().map(manager -> manager.generateAdditionalTestInputsWithResult(wrappedTestInputs, testResult)).flatMap(Collection::stream).collect(Collectors.toList());
    }
    
    private static final class SingleGroupGenerationManager {
        
        private final Supplier<TestInputGroup> testInputGroupSupplier;
        private final TestInputGroupGenerator testInputGroupGenerator;
        private final TestInputPrioritizer prioritizer;
        private final FaultCharacterizationAlgorithmFactory faultCharacterizationAlgorithmFactory;
        private final GenerationReporter reporter;
        private final boolean isFailFastExecutionMode;
        
        private TestInputGroup testInputGroup;
        private FaultCharacterizationAlgorithm faultCharacterizationAlgorithm;
        private Set<IntArrayWrapper> missingTestInputs;
        private Map<int[], TestResult> testResults;
        private boolean isInitialGeneration = true;
        
        private SingleGroupGenerationManager(Supplier<TestInputGroup> testInputGroupSupplier,
                TestInputGroupGenerator testInputGroupGenerator,
                TestInputPrioritizer prioritizer,
                FaultCharacterizationAlgorithmFactory faultCharacterizationAlgorithmFactory,
                GenerationReporter reporter,
                ExecutionMode executionMode) {
            
            this.testInputGroupSupplier = testInputGroupSupplier;
            this.testInputGroupGenerator = testInputGroupGenerator;
            this.prioritizer = prioritizer;
            this.faultCharacterizationAlgorithmFactory = faultCharacterizationAlgorithmFactory;
            this.reporter = reporter;
            this.isFailFastExecutionMode = executionMode == ExecutionMode.FAIL_FAST;
        }
        
        List<int[]> generateInitialTests() {
            testInputGroup = testInputGroupSupplier.get();
            reporter.testInputGroupGenerated(testInputGroup, testInputGroupGenerator);
            final List<int[]> testInputs = testInputGroup.getTestInputs();
            final List<int[]> prioritizedInputs = prioritizeInputsIfPossible(testInputs);
            initializeNextMissingTestInputs(prioritizedInputs);
            return prioritizedInputs;
        }
        
        private List<int[]> prioritizeInputsIfPossible(List<int[]> testInputs) {
            return testInputGroup.getFaultCharacterizationConfiguration()
                    .map(FaultCharacterizationConfiguration::getModel)
                    .map(testModel -> prioritizer.prioritize(testInputs, testModel))
                    .orElse(testInputs);
        }
        
        private void initializeNextMissingTestInputs(List<int[]> testInputs) {
            missingTestInputs = wrapToSet(testInputs);
            testResults = new HashMap<>();
        }
        
        List<int[]> generateAdditionalTestInputsWithResult(IntArrayWrapper combination, TestResult testResult) {
            if (missingTestInputs.contains(combination)) {
                missingTestInputs.remove(combination);
                testResults.put(combination.getArray(), testResult);
                
                if (missingTestInputs.isEmpty()
                        || (testResult.isUnsuccessful() && isFailFastExecutionMode && isInitialGeneration)) {
                    isInitialGeneration = false;
                    
                    if (shouldUseFaultCharacterization()) {
                        return nextFaultCharacterizationIteration();
                    } else {
                        reporter.testInputGroupFinished(testInputGroup);
                    }
                }
            }
            
            return Collections.emptyList();
        }
        
        private boolean shouldUseFaultCharacterization() {
            return faultCharacterizationAlgorithm != null || (faultCharacterizationAlgorithmFactory != null
                    && testInputGroup.getFaultCharacterizationConfiguration().isPresent()
                    && testResultsContainAnyFailure());
        }
        
        private boolean testResultsContainAnyFailure() {
            return testResults.values().stream().anyMatch(TestResult::isUnsuccessful);
        }
        
        private List<int[]> nextFaultCharacterizationIteration() {
            initializeCharacterizationAlgorithmIfNotInitialized();
            final List<int[]> nextTestInputs = faultCharacterizationAlgorithm.computeNextTestInputs(new HashMap<>(testResults));
            testResults.clear();
            
            if (nextTestInputs.isEmpty()) {
                final List<int[]> failureInducingCombinations = faultCharacterizationAlgorithm.computeFailureInducingCombinations();
                reporter.faultCharacterizationFinished(testInputGroup, new HashMap<>(), new HashSet<>(failureInducingCombinations));
                reporter.testInputGroupFinished(testInputGroup);
            } else {
                reporter.faultCharacterizationTestInputsGenerated(testInputGroup, nextTestInputs);
                missingTestInputs.addAll(wrapToSet(nextTestInputs));
            }
            
            return nextTestInputs;
        }
        
        private void initializeCharacterizationAlgorithmIfNotInitialized() {
            if (faultCharacterizationAlgorithm == null) {
                missingTestInputs.clear();
                final FaultCharacterizationConfiguration configuration = testInputGroup.getFaultCharacterizationConfiguration()
                        .orElseThrow(() -> new IllegalArgumentException("Algorithm cannot be initialized without a configuration"));

                try {
                    faultCharacterizationAlgorithm = faultCharacterizationAlgorithmFactory.create(configuration);
                } catch (Exception e) {
                    throw new Coffee4JException(e, "Fault Characterization Algorithm could not be created!");
                }

                reporter.faultCharacterizationStarted(testInputGroup, faultCharacterizationAlgorithm);
            }
        }
        
    }
    
}
