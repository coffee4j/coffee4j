package de.rwth.swc.coffee4j.algorithmic.sequential.manager;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesChecker;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.GeneratingFaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.report.EmptySequentialGenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.sequential.report.GenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.mixtgte.Mixtgte;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.emptyset.EmptySetGenerator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Default implementation of the interface {@link GeneratingSequentialCombinatorialTestManager}.
 */
public class ConstraintGeneratingSequentialCombinatorialTestManager implements GeneratingSequentialCombinatorialTestManager {
    
    private final TestInputGroup testInputGroup;
    private final GenerationReporter generationReporter;
    private final GeneratingFaultCharacterizationAlgorithm faultCharacterizationAlgorithm;
    private final ClassificationStrategy classificationStrategy;

    private Set<IntList> missingTestInputs;
    private Map<int[], TestResult> testResults;

    /**
     * @param configuration {@link SequentialCombinatorialTestConfiguration} used for the initialization of FCAs etc.
     * @param testModel model to process.
     */
    public ConstraintGeneratingSequentialCombinatorialTestManager(SequentialCombinatorialTestConfiguration configuration,
                                                                  CompleteTestModel testModel) {
        Preconditions.notNull(configuration);
        Preconditions.notNull(testModel);

        generationReporter = configuration.getGenerationReporter().orElseGet(EmptySequentialGenerationReporter::new);

        final FaultCharacterizationAlgorithmFactory faultCharacterizationAlgorithmFactory
                = configuration.getFaultCharacterizationAlgorithmFactory().orElse(null);

        if (faultCharacterizationAlgorithmFactory == null) {
            throw new Coffee4JException("No Fault Characterization Factory available!");
        }

        // Assumption: for this task at most one TestInputGroupGenerator needed
        Optional<TestInputGroupGenerator> optionalGenerator = configuration.getGenerators().stream().findFirst();
        final TestInputGroupGenerator testInputGroupGenerator = optionalGenerator.orElse(new EmptySetGenerator());

        Collection<Supplier<TestInputGroup>> testInputGroupSupplier = testInputGroupGenerator.generate(
                testModel,
                generationReporter
        );

        Optional<Supplier<TestInputGroup>> optGroupSupplier = testInputGroupSupplier.stream().findFirst();
        assert(testInputGroupSupplier.size() == 1);

        testInputGroup = optGroupSupplier.get().get();

        generationReporter.testInputGroupGenerated(testInputGroup, testInputGroupGenerator);

        FaultCharacterizationConfiguration faultCharacterizationConfiguration = testInputGroup
                .getFaultCharacterizationConfiguration()
                .orElse(new FaultCharacterizationConfiguration(testModel, generationReporter));

        try {
            faultCharacterizationAlgorithm = (GeneratingFaultCharacterizationAlgorithm) faultCharacterizationAlgorithmFactory.create(
                    faultCharacterizationConfiguration);
        } catch (Exception e) {
            throw new Coffee4JException(e, "Generating Fault Characterization Algorithm could not be created!");
        }

        if (!configuration.getClassificationStrategyFactory().isPresent()) {
            throw new Coffee4JException("No Classification-Strategy provided!");
        }

        Optional<ClassificationStrategyFactory> optFactory = configuration.getClassificationStrategyFactory();

        this.classificationStrategy = Preconditions.notNull(optFactory.get())
                .create(ClassificationConfiguration
                        .configuration()
                        .constraintChecker(MinimalForbiddenTuplesChecker.minimalForbiddenTuplesChecker().createConstraintChecker(testModel))
                        .testModel(testModel)
                        .build());
    }

    @Override
    public List<int[]> generateInitialTests() {
        List<int[]> initialTestSuite = new ArrayList<>();

        if (!(faultCharacterizationAlgorithm instanceof Mixtgte)) {
            initialTestSuite.addAll(testInputGroup.getTestInputs());
        }

        if (initialTestSuite.isEmpty()) {
            initialTestSuite = faultCharacterizationAlgorithm.computeNextTestInputs(new HashMap<>());
        }

        missingTestInputs = initialTestSuite.stream().map(IntArrayList::new).collect(Collectors.toSet());
        testResults = new HashMap<>();

        generationReporter.faultCharacterizationStarted(testInputGroup, faultCharacterizationAlgorithm);

        return initialTestSuite;
    }

    @Override
    public List<int[]> generateAdditionalTestInputsWithResult(int[] testInput, TestResult testResult) {
        IntList input = new IntArrayList(testInput);

        if (missingTestInputs.contains(input)) {
            missingTestInputs.remove(input);
            testResults.put(testInput, testResult);

            if (missingTestInputs.isEmpty()) {
                List<int[]> nextTestInputs = faultCharacterizationAlgorithm.computeNextTestInputs(new HashMap<>(testResults));
                testResults.clear();

                if (!nextTestInputs.isEmpty()) {
                    generationReporter.faultCharacterizationTestInputsGenerated(testInputGroup, nextTestInputs);
                    missingTestInputs.addAll(nextTestInputs.stream().map(IntArrayList::new).collect(Collectors.toList()));
                }

                return nextTestInputs;
            }
        }

        return Collections.emptyList();
    }

    @Override
    public Optional<int[]> initializeClassification(Map<int[], TestResult> errorConstraintExceptionCausingTestInputs) {
        Optional<int[]> nextTestInput = classificationStrategy.startClassification(
                errorConstraintExceptionCausingTestInputs
                        .entrySet()
                        .stream()
                        .map(input -> Map.entry(input.getKey(), input.getValue().getResultValue().get().getCause()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                new ArrayList<>(faultCharacterizationAlgorithm.computeExceptionInducingCombinations()),
                new HashSet<>(faultCharacterizationAlgorithm.computeFailureInducingCombinations())
        );

        return checkTestInputForClassification(nextTestInput);
    }

    @Override
    public Optional<int[]> generateNextTestInputForClassification(int[] testInput, TestResult testResult) {
        Optional<int[]> nextTestInput = classificationStrategy.generateNextTestInputForClassification(testInput, testResult);
        return checkTestInputForClassification(nextTestInput);
    }

    private Optional<int[]> checkTestInputForClassification(Optional<int[]> nextTestInput) {
        if (!nextTestInput.isPresent()) {
            List<int[]> failureInducingCombinations = faultCharacterizationAlgorithm.computeFailureInducingCombinations();
            Map<int[], Class<? extends Throwable>> exceptionInducingCombinations = classificationStrategy.getClassifiedExceptionInducingCombinations();

            generationReporter.faultCharacterizationFinished(testInputGroup, exceptionInducingCombinations, new HashSet<>(failureInducingCombinations));
            generationReporter.testInputGroupFinished(testInputGroup);
        }

        return nextTestInput;
    }

    /**
     * @return set of minimal exception-inducing combinations.
     */
    public Set<IntList> getMinimalExceptionInducingTuples() {
        return classificationStrategy.getClassifiedExceptionInducingCombinations().keySet().stream()
                .map(IntArrayList::new)
                .collect(Collectors.toSet());
    }
}
