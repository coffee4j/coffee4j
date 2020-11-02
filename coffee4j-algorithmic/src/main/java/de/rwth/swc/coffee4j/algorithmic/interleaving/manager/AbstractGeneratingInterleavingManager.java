package de.rwth.swc.coffee4j.algorithmic.interleaving.manager;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.constraint.ForbiddenTuplesChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.Phase;
import de.rwth.swc.coffee4j.algorithmic.interleaving.util.TupleBuilderUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract class implementing {@link GeneratingInterleavingCombinatorialTestManager}.Extends the {@link AbstractInterleavingManager}
 * and adds the capability to store exception-inducing combinations.
 */
public abstract class AbstractGeneratingInterleavingManager extends AbstractInterleavingManager implements GeneratingInterleavingCombinatorialTestManager {
    private final int numberOfParameters;
    private final int[] parameterSizes;

    private final ClassificationStrategy classificationStrategy;

    protected Map<int[], Class<? extends Throwable>> minimalExceptionInducingCombinations = new HashMap<>();
    protected Set<int[]> minimalExceptionInducingCombinationsToCheck;

    /**
     * @param configuration {@link InterleavingCombinatorialTestConfiguration} used for initialization of strategies etc.
     * @param testModel model to process.
     */
    AbstractGeneratingInterleavingManager(InterleavingCombinatorialTestConfiguration configuration, CompleteTestModel testModel) {
        super(configuration, testModel);
        numberOfParameters = testModel.getNumberOfParameters();
        parameterSizes = Preconditions.notNull(testModel.getParameterSizes());

        classificationStrategy = Preconditions.notNull(configuration.getClassificationStrategyFactory())
                .create(ClassificationConfiguration
                        .configuration()
                        .constraintChecker(configuration.getConstraintCheckerFactory().createConstraintChecker(testModel))
                        .testModel(testModel)
                        .build());

        assert numberOfParameters > 0;
    }

    @Override
    public Optional<int[]> initializeClassification(Map<int[], TestResult> errorConstraintExceptionCausingTestInputs) {
        currentPhase = Phase.CLASSIFICATION;
        Optional<int[]> nextTestInput;

        Map<int[], Throwable> inputs = new HashMap<>();
        for (Map.Entry<int[], TestResult> input : errorConstraintExceptionCausingTestInputs.entrySet()) {
            input.getValue().getResultValue().ifPresent(throwable -> inputs.put(input.getKey(), throwable.getCause()));
        }

        nextTestInput = classificationStrategy.startClassification(inputs, postProcessExceptionInducingCombinations(), failureInducingCombinations);

        return checkTestInputForClassification(nextTestInput);
    }

    @Override
    public Optional<int[]> generateNextTestInput(int[] testInput, TestResult result) {
        switch (currentPhase) {
            case GENERATION:
                return generateNextTestInput();
            case IDENTIFICATION:
                return generateNextTestInputForIdentification(testInput, result);
            case VERIFICATION:
                return generateNextTestInputForFeedbackChecking(testInput, result);
            case CLASSIFICATION:
                return generateNextTestInputForClassification(testInput, result);
            default:
                throw new Coffee4JException("Unknown Phase!");
        }
    }

    @Override
    protected Optional<int[]> generateNextTestInput() {
        Optional<int[]> nextTestInput = Optional.empty();

        if (!coverageMap.allCombinationsCovered()) {
            nextTestInput = testInputGenerationStrategy.generateNextTestInput();
        }

        if (!nextTestInput.isPresent()) {
            currentPhase = Phase.CLASSIFICATION;
        }

        return nextTestInput;
    }

    @Override
    public Map<int[], Class<? extends Throwable>> getMinimalExceptionInducingCombinations() {
        return minimalExceptionInducingCombinations;
    }

    private Optional<int[]> generateNextTestInputForClassification(int[] testInput, TestResult result) {
        Optional<int[]> nextTestInput = classificationStrategy.generateNextTestInputForClassification(testInput, result);
        return checkTestInputForClassification(nextTestInput);
    }

    private Optional<int[]> checkTestInputForClassification(Optional<int[]> nextTestInput) {
        if (!nextTestInput.isPresent()) {
            minimalExceptionInducingCombinations = classificationStrategy.getClassifiedExceptionInducingCombinations();
            terminateInterleavingGroup();
        }

        return nextTestInput;
    }

    protected List<int[]> postProcessExceptionInducingCombinations() {
        Preconditions.check(checker instanceof ForbiddenTuplesChecker);
        Set<IntList> initialForbiddenTuples = ((ForbiddenTuplesChecker) checker).getInitialForbiddenTuples();
        Set<IntList> exceptionInducingCombinations = minimalExceptionInducingCombinations.keySet().stream().map(IntArrayList::new).collect(Collectors.toSet());

        Set<IntList> derivedTuples = new HashSet<>();
        Set<IntList> usedForDeriving = new HashSet<>();

        for (IntList generatedTuple : exceptionInducingCombinations) {
            for (int param = 0; param < numberOfParameters; param++) {
                IntSet usedValues = new IntArraySet();

                if (generatedTuple.getInt(param) != -1) {
                    List<Collection<IntList>> forbiddenTuplesForParameter = new ArrayList<>(parameterSizes[param]);

                    for (int i = 0; i < parameterSizes[param]; i++) {
                        forbiddenTuplesForParameter.add(new HashSet<>());
                    }

                    usedValues.add(generatedTuple.getInt(param));
                    forbiddenTuplesForParameter.get(generatedTuple.getInt(param)).add(generatedTuple);

                    for (IntList initialTuple : initialForbiddenTuples) {
                        if (initialTuple.getInt(param) != -1) {
                            forbiddenTuplesForParameter.get(initialTuple.getInt(param)).add(initialTuple);
                            usedValues.add(initialTuple.getInt(param));
                        }
                    }

                    if (usedValues.size() == parameterSizes[param]) {
                        derivedTuples.addAll(deriveNewTuplesUsingParameter(param, forbiddenTuplesForParameter));
                        usedForDeriving.add(generatedTuple);
                    }
                }
            }
        }

        derivedTuples.removeAll(usedForDeriving);
        exceptionInducingCombinations.removeAll(derivedTuples);

        return exceptionInducingCombinations
                .stream()
                .map(IntCollection::toIntArray)
                .collect(Collectors.toList());

    }

    private Collection<IntList> deriveNewTuplesUsingParameter(int param, List<Collection<IntList>> forbiddenTuplesForParameter) {
        Set<Collection<IntList>> forbiddenSet = new HashSet<>();

        for (Collection<IntList> set : forbiddenTuplesForParameter) {
            Set<IntList> newSet = new HashSet<>();
            for (IntList tuple : set) {
                IntList newTuple = new IntArrayList(tuple);
                newTuple.set(param, -1);
                newSet.add(newTuple);
            }

            forbiddenSet.add(newSet);
        }

        return TupleBuilderUtil.buildCartesianProduct(forbiddenSet, numberOfParameters);
    }
}
