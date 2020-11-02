package de.rwth.swc.coffee4j.algorithmic.classification;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Strategy that is used to classify a found exception-inducing combination according to the exceptional results it
 * causes.
 */
public interface ClassificationStrategy {
    /**
     * initializes the strategy.
     * @param errorConstraintExceptionCausingTestInputs all test inputs that result in an
     * {@link de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException}
     * @param exceptionInducingCombinationsToClassify all exception-inducing combinations that are found during
     *                                                generation
     * @param possiblyFailureInducingCombinations all possibly failure-inducing combinations that are found during
     *                                            generation
     * @return all classified exception-inducing combinations. If a combination cannot be classified, it is classified
     * as {@link de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException}.
     */
    Optional<int[]> startClassification(
                Map<int[], Throwable> errorConstraintExceptionCausingTestInputs,
                List<int[]> exceptionInducingCombinationsToClassify,
                Set<int[]> possiblyFailureInducingCombinations
            );

    /**
     * @param testInput last executed test input
     * @param result result of last executed test input
     * @return next test input to execute that is needed for classification
     */
    Optional<int[]> generateNextTestInputForClassification(int[] testInput, TestResult result);

    /**
     * @return returns all classified exception-inducing combinations.
     * {@link de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException} indicates that no classification is possible for the
     * related combination
     */
    Map<int[], Class<? extends Throwable>> getClassifiedExceptionInducingCombinations();
}
