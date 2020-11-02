package de.rwth.swc.coffee4j.algorithmic.interleaving.feedback;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;

import java.util.Optional;

/**
 * Interface that each feedback-checking strategy used by interleaving CT must implement.
 */
public interface FeedbackCheckingStrategy {
    /**
     * @param candidate possibly exception/failure-inducing combination to check
     * @param failingTestInput test input that contains possibly exception/failure-inducing combination
     *
     * @return first test input used for checking possibly exception/failure-inducing combination
     * (if present, otherwise empty Optional)
     */
    Optional<int[]> startFeedbackChecking(int[] candidate, int[] failingTestInput);

    /**
     * @param testInput previously executed test input
     * @param testResult result of previously executed test input
     *
     * @return next test input that is most dissimilar from previously generated tests in checking phase. Empty optional,
     * if maximal number of feedback checks is reached or previously executed test input passed which means that
     * combination is not failure-inducing.
     */
    Optional<int[]> generateNextTestInputForChecking(int[] testInput, TestResult testResult);
}
