package de.rwth.swc.coffee4j.algorithmic.interleaving.feedback;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.CoverageMap;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import de.rwth.swc.coffee4j.algorithmic.interleaving.util.OptimalValue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Default Feedback Checking Strategy introduced in "An interleaving approach to combinatorial testing and failure-inducing
 * interaction identification". A pre-defined number of checks are generated that are most-dissimilar to each other
 * to increase the probability that a combination is classified correctly as failure-inducing.
 */
public class DefaultFeedbackCheckingStrategy implements FeedbackCheckingStrategy {
    
    private final CoverageMap coverageMap;
    private final ConstraintChecker checker;
    private final CompleteTestModel testModel;

    private int[] candidate;
    private final int numberOfParameters;

    private final int numberOfFeedbackChecks;

    // test input containing identified possibly failure-/exception-inducing combination
    private int[] processedTestInput;

    // List of all parameters
    private final IntList parameters;
    private List<int[]> dissimilarTestInputSet;

    private DefaultFeedbackCheckingStrategy(FeedbackCheckingConfiguration configuration) {
        this.coverageMap = configuration.getCoverageMap();
        this.checker = configuration.getConstraintChecker();
        this.testModel = configuration.getTestModel();
        this.numberOfFeedbackChecks = configuration.getNumberOfFeedbackChecks();

        numberOfParameters = testModel.getNumberOfParameters();

        parameters = new IntArrayList(numberOfParameters);
        IntStream.range(0, numberOfParameters).forEach(parameters::add);
    }

    /**
     * @return returns a factory for creating a {@link DefaultFeedbackCheckingStrategy}.
     */
    public static FeedbackCheckingStrategyFactory defaultCheckingStrategy() {
        return DefaultFeedbackCheckingStrategy::new;
    }

    @Override
    public Optional<int[]> startFeedbackChecking(int[] candidate, int[] processedTestInput) {
        this.candidate = candidate;
        this.processedTestInput = processedTestInput;
        dissimilarTestInputSet = generateDissimilarTestInputSet();

        return selectDissimilarTestInput();
    }

    @Override
    public String toString() {
        return "DefaultFeedbackCheckingStrategy";
    }

    private List<int[]> generateDissimilarTestInputSet() {
        List<int[]> alreadyGeneratedTestInputs = new ArrayList<>();
        alreadyGeneratedTestInputs.add(processedTestInput);
        List<int[]> candidateTestInputs;

        // generate NUMBER_OF_FEEDBACK_CHECKS most dissimilar test inputs used for checking possibly
        // failure-/exception-inducing combination
        for (int numberOfTestInputs = 0; numberOfTestInputs < numberOfFeedbackChecks; numberOfTestInputs++) {
            candidateTestInputs = new ArrayList<>();

            // generate a set of candidate test inputs
            for (int iteration = 0; iteration < 50; iteration++) {
                Optional<int[]> possibleTestInput = generateDissimilarTestInput(alreadyGeneratedTestInputs);
                possibleTestInput.ifPresent(candidateTestInputs::add);
            }

            // select most dissimilar test input compared to previously generated test inputs (if present)
            if (candidateTestInputs.size() == 1) {
                alreadyGeneratedTestInputs.add(candidateTestInputs.get(0));
            } else if (!candidateTestInputs.isEmpty()) {
                alreadyGeneratedTestInputs.add(Collections.max(candidateTestInputs, Comparator.comparing(coverageMap::getNumberOfCoveredCombinationsByTestInput)));
            }
        }

        return alreadyGeneratedTestInputs;
    }

    @Override
    public Optional<int[]> generateNextTestInputForChecking(int[] testInput, TestResult testResult) {
        // combination is not failure-/exception-inducing
        if (testResult.isSuccessful()) {
            return Optional.empty();
        } else {
            return selectDissimilarTestInput();
        }
    }

    private Optional<int[]> selectDissimilarTestInput() {
        if (!dissimilarTestInputSet.isEmpty()) {
            return Optional.of(dissimilarTestInputSet.remove(0));
        }

        // maximum number of feedback checks is reached
        return Optional.empty();
    }

    // based on: bypassing the combinatorial explosion: using similarity ...
    private Optional<int[]> generateDissimilarTestInput(List<int[]> dissimilarFrom) {
        int[] nextTestInput = Arrays.copyOf(candidate, numberOfParameters);
        Collections.shuffle(parameters);

        // iterate over parameters randomly
        for (int parameter : parameters) {
            if (nextTestInput[parameter] == -1) {
                Optional<ParameterValuePair> optimalValue = OptimalValue.mostDissimilarForParameter(
                        parameter,
                        testModel.getParameterSize(parameter),
                        nextTestInput,
                        dissimilarFrom,
                        checker);

                // select valid value that is most dissimilar for current partial test input and previously
                // generated inputs
                if (optimalValue.isPresent()) {
                    nextTestInput[parameter] = optimalValue.get().getValue();
                } else {
                    return Optional.empty();
                }
            }
        }

        return Optional.of(nextTestInput);
    }
}
