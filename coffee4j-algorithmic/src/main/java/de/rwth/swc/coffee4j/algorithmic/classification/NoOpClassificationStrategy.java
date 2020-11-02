package de.rwth.swc.coffee4j.algorithmic.classification;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;

import java.util.*;

/**
 * All exception-inducing combinations are classified as throwing ErrorConstraintException indicating that no classification
 * was performed.
 */
public class NoOpClassificationStrategy implements ClassificationStrategy {

    final Map<int[], Class<? extends Throwable>> classifiedExceptionInducingCombinations;

    /**
     * Constructor using a {@link ClassificationConfiguration}
     * @param configuration provided configuration.
     */
    public NoOpClassificationStrategy(ClassificationConfiguration configuration) {
        classifiedExceptionInducingCombinations = new HashMap<>();
    }

    /**
     * @return returns a factory for this class.
     */
    public static ClassificationStrategyFactory noOpClassificationStrategy() { return NoOpClassificationStrategy::new; }

    @Override
    public Optional<int[]> startClassification(Map<int[], Throwable> errorConstraintExceptionCausingTestInputs, List<int[]> exceptionInducingCombinationsToClassify, Set<int[]> possiblyFailureInducingCombinations) {
        exceptionInducingCombinationsToClassify.forEach(combination -> classifiedExceptionInducingCombinations.put(combination, ErrorConstraintException.class));
        return Optional.empty();
    }

    @Override
    public Optional<int[]> generateNextTestInputForClassification(int[] testInput, TestResult result) {
        return Optional.empty();
    }

    @Override
    public Map<int[], Class<? extends Throwable>> getClassifiedExceptionInducingCombinations() {
        return classifiedExceptionInducingCombinations;
    }
}
