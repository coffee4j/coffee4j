package de.rwth.swc.coffee4j.algorithmic.classification;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import it.unimi.dsi.fastutil.ints.*;

import java.util.*;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * {@link ClassificationStrategy} that tries to find an isolating test input for each found exception-inducing combination,
 * i.e. a test input containing no other found exception-inducing combination.
 */
public class IsolatingClassificationStrategy implements ClassificationStrategy {
    private static final long MAXIMUM_NUMBER_OF_ITERATIONS = 100000;
    private final ConstraintChecker checker;
    private final CompleteTestModel testModel;
    private final IntList parameters;
    private long numberOfPossibleTestInputs = 1;
    private Set<IntList> involvedFailureInducingCombinations;

    private IntList currentlyProcessedCombination;
    private List<IntList> exceptionInducingCombinationsToClassify;
    final Map<IntList, Class<? extends Throwable>> classifiedExceptionInducingCombinations = new HashMap<>();
    private Set<IntList> forbiddenTuples;

    /**
     * Constructor using a {@link ClassificationConfiguration}
     * @param configuration provided configuration.
     */
    public IsolatingClassificationStrategy(ClassificationConfiguration configuration) {
        this.checker = configuration.getConstraintChecker();
        this.testModel = configuration.getTestModel();
        int numberOfParameters = testModel.getNumberOfParameters();
        IntList parameterSizes = new IntArrayList(testModel.getParameterSizes());
        parameterSizes.forEach((IntConsumer) values -> numberOfPossibleTestInputs *= values);

        // select the minimum value of MAXIMUM_NUMBER_OF_ITERATIONS and the number of possible test inputs for the processed
        // test model to limit the attempts performed for a test input generation
        numberOfPossibleTestInputs = Long.min(numberOfPossibleTestInputs, MAXIMUM_NUMBER_OF_ITERATIONS);

        // overflow possible caused by large models
        if (numberOfPossibleTestInputs < 0) {
            numberOfPossibleTestInputs = MAXIMUM_NUMBER_OF_ITERATIONS;
        }

        parameters = new IntArrayList(numberOfParameters);
        IntStream.range(0, numberOfParameters).forEach(parameters::add);
    }

    /**
     * @return factory for creating an {@link IsolatingClassificationStrategy}.
     */
    public static ClassificationStrategyFactory isolatingClassificationStrategy() { return IsolatingClassificationStrategy::new; }

    @Override
    public Optional<int[]> startClassification(Map<int[], Throwable> errorConstraintExceptionCausingTestInputs, List<int[]> exceptionInducingCombinationsToClassify, Set<int[]> possiblyFailureInducingCombinations) {
        this.exceptionInducingCombinationsToClassify = exceptionInducingCombinationsToClassify.stream().map(IntArrayList::new).collect(Collectors.toList());
        involvedFailureInducingCombinations = possiblyFailureInducingCombinations.stream().map(IntArrayList::new).collect(Collectors.toSet());

        IntList emptyCombination = new IntArrayList(CombinationUtil.emptyCombination(testModel.getNumberOfParameters()));
        // infeasible to find an isolating test input for the empty combination -> remove this combination if present
        this.exceptionInducingCombinationsToClassify.remove(emptyCombination);
        involvedFailureInducingCombinations.remove(emptyCombination);

        this.forbiddenTuples = new HashSet<>(this.exceptionInducingCombinationsToClassify);
        this.forbiddenTuples.addAll(involvedFailureInducingCombinations);

        Optional<int[]> nextTestInput = Optional.empty();

        if (!this.exceptionInducingCombinationsToClassify.isEmpty()) {
            while (!nextTestInput.isPresent() && !this.exceptionInducingCombinationsToClassify.isEmpty()) {
                currentlyProcessedCombination = this.exceptionInducingCombinationsToClassify.remove(0);
                nextTestInput = generateIsolatingTestInput(currentlyProcessedCombination);
            }
        }

        return nextTestInput;
    }

    @Override
    public Optional<int[]> generateNextTestInputForClassification(int[] testInput, TestResult result) {
        if (result.isExceptionalSuccessful()) {
            Optional<Throwable> optCause = result.getResultValue();

            if (optCause.isPresent()) {
                if (optCause.get().getCause() == null) {
                    classifiedExceptionInducingCombinations.put(currentlyProcessedCombination, ErrorConstraintException.class);
                } else {
                    classifiedExceptionInducingCombinations.put(currentlyProcessedCombination, optCause.get().getCause().getClass());
                }
            }
        }

        Optional<int[]> nextTestInput = Optional.empty();

        if (!this.exceptionInducingCombinationsToClassify.isEmpty()) {
            while (!nextTestInput.isPresent() && !this.exceptionInducingCombinationsToClassify.isEmpty()) {
                currentlyProcessedCombination = this.exceptionInducingCombinationsToClassify.remove(0);
                nextTestInput = generateIsolatingTestInput(currentlyProcessedCombination);
            }
        }

        return nextTestInput;
    }

    @Override
    public Map<int[], Class<? extends Throwable>> getClassifiedExceptionInducingCombinations() {
        return classifiedExceptionInducingCombinations.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toIntArray(), Map.Entry::getValue));
    }

    private Optional<int[]> generateIsolatingTestInput(IntList exceptionInducingCombination) {
        Optional<int[]> optNextTestInput;
        int[] nextTestInput = null;

        forbiddenTuples = new HashSet<>(exceptionInducingCombinationsToClassify);
        forbiddenTuples.addAll(classifiedExceptionInducingCombinations.keySet());
        forbiddenTuples.addAll(involvedFailureInducingCombinations);
        forbiddenTuples.remove(exceptionInducingCombination);

        long iteration = 1;

        while (nextTestInput == null && iteration < numberOfPossibleTestInputs) {
            nextTestInput = Arrays.copyOf(exceptionInducingCombination.toIntArray(), exceptionInducingCombination.size());

            iteration++;

            Collections.shuffle(parameters);

            for (int parameter : parameters) {
                if (nextTestInput[parameter] == -1) {
                    Optional<ParameterValuePair> optimalValue = calculateOptimalValue(parameter,
                            testModel.getParameterSize(parameter),
                            nextTestInput,
                            forbiddenTuples,
                            checker
                            );

                    // select valid value that is most dissimilar for current partial test input and previously
                    // generated inputs
                    if (optimalValue.isPresent()) {
                        nextTestInput[parameter] = optimalValue.get().getValue();
                    } else {
                        forbiddenTuples.add(new IntArrayList(nextTestInput));
                        nextTestInput = null;
                        break;
                    }
                }
            }
        }

        if (nextTestInput != null) {
            optNextTestInput = Optional.of(nextTestInput);
            forbiddenTuples.add(exceptionInducingCombination);
        } else {
            optNextTestInput = Optional.empty();
        }

        return optNextTestInput;
    }

    private Optional<ParameterValuePair> calculateOptimalValue(int parameter, int sizeOfParameter, int[] nextTestInput, Set<IntList> forbiddenTuples, ConstraintChecker checker) {
        int[] candidateTestInput = Arrays.copyOf(nextTestInput, nextTestInput.length);

        IntArrayList values = new IntArrayList();
        IntStream.range(0, sizeOfParameter).forEach(values::add);

        Collections.shuffle(values);

        for (int value : values) {
            if (checker.isExtensionValid(candidateTestInput, parameter, value)) {
                // valid value found
                candidateTestInput[parameter] = value;
                boolean invalid = false;

                for (IntList forbiddenTuple : forbiddenTuples) {
                    if (CombinationUtil.contains(candidateTestInput, forbiddenTuple.toIntArray())) {
                        invalid = true;
                        break;
                    }
                }

                if (!invalid) {
                    return Optional.of(new ParameterValuePair(parameter, value));
                }
            }
        }

        // no valid value could be found
        return Optional.empty();
    }
}
