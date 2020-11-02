package de.rwth.swc.coffee4j.algorithmic.classification;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link ClassificationStrategy} that classifies the found exception-inducing combinations using the maximum number of
 * test inputs that trigger the same exception and contain the corresponding combination.
 */
public class MaxCountClassificationStrategy implements ClassificationStrategy {
    
    private final Map<int[], Class<? extends Throwable>> classifiedExceptionInducingCombinations = new HashMap<>();
    private Map<int[], Throwable> errorConstraintExceptionCausingTestInputs;

    /**
     * Constructor using a {@link ClassificationConfiguration}
     * @param configuration provided configuration.
     */
    public MaxCountClassificationStrategy(ClassificationConfiguration configuration) {
        // empty constructor
    }

    /**
     * @return returns factory for this class.
     */
    public static ClassificationStrategyFactory maxCountClassificationStrategy() { return MaxCountClassificationStrategy::new; }

    @Override
    public Optional<int[]> startClassification(Map<int[], Throwable> errorConstraintExceptionCausingTestInputs, List<int[]> exceptionInducingCombinationsToClassify, Set<int[]> possiblyFailureInducingCombinations) {
        this.errorConstraintExceptionCausingTestInputs = errorConstraintExceptionCausingTestInputs;
        exceptionInducingCombinationsToClassify.forEach(this::classifyCombination);

        return Optional.empty();
    }

    private void classifyCombination(int[] combination) {
        // collect all Exceptions raised by failing test inputs containing currently checked combination
        List<Throwable> exceptionsRaisedByCombination =
                errorConstraintExceptionCausingTestInputs
                    .entrySet()
                    .stream()
                    .filter(testInput -> CombinationUtil.contains(testInput.getKey(), combination))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());

        // select class of Exception triggered most frequently by currently checked combination
        Class<? extends Throwable> type = exceptionsRaisedByCombination
                .stream()
                .collect(Collectors.groupingBy(Throwable::getClass))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().size()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        
        if (type == null) {
            type = exceptionsRaisedByCombination.get(0).getClass();
        }

        classifiedExceptionInducingCombinations.put(combination, type);
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
