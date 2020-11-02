package de.rwth.swc.coffee4j.algorithmic.interleaving.util;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.CoverageMap;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import it.unimi.dsi.fastutil.ints.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class for generating test inputs used by {@link de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategy}
 * or {@link de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategy}
 */
public final class OptimalValue {
    /**
     * @param parameter parameter to find an optimal value for
     * @param numberOfValues number of possible values the given parameter can take
     * @param forbiddenValues values that must not be assigned to the parameter
     * @param testInput partial test input generated so far and which needs to be extended
     * @param coverageMap containing all uncovered combinations
     * @param checker Constraint Checker to ensure that assigned value is valid in given test input
     *
     * @return valid value for given parameter covering most uncovered t-tuples
     */
    public Optional<ParameterValuePair> forParameter(int parameter, int numberOfValues, IntSet forbiddenValues, int[] testInput, CoverageMap coverageMap, ConstraintChecker checker) {
        int[] candidateTestInput = Arrays.copyOf(testInput, testInput.length);

        int optimalValue = -1;
        long maximumNumberOfCoveredCombinations = -1;

        IntList possibleValues = new IntArrayList(numberOfValues);
        for (int i = 0; i < numberOfValues; i++) {
            if (!forbiddenValues.contains(i)) {
                possibleValues.add(i);
            }
        }

        Collections.shuffle(possibleValues);

        for (int value : possibleValues) {
            if (checker.isExtensionValid(candidateTestInput, parameter, value)) {
                // valid value found
                candidateTestInput[parameter] = value;

                long numberOfCoveredCombinations = coverageMap.getNumberOfCoveredCombinationsByTestInput(candidateTestInput);

                // if given value increases number of covered t-tuples, set this value as (current) optimal value
                if (numberOfCoveredCombinations > maximumNumberOfCoveredCombinations) {
                    optimalValue = value;
                    maximumNumberOfCoveredCombinations = numberOfCoveredCombinations;
                }
            }
        }

        // no valid value could be found
        if (optimalValue == -1) {
            return Optional.empty();
        }

        return Optional.of(new ParameterValuePair(parameter, optimalValue));
    }

    /**
     * @param parameter parameter to find a valid value for
     * @param numberOfValues number of possible values the given parameter can take
     * @param testInput partial test input generated so far and which needs to be extended
     * @param executedTestInputs all test inputs generated and executed so far. Next test inputs must be as different as
     *                           possible
     * @param checker Constraint checker to ensure that assigned value is valid in given test input

     * @return valid value for given parameter that is most dissimilar to already executed test inputs
     */
    public static Optional<ParameterValuePair> mostDissimilarForParameter(int parameter, int numberOfValues, int[] testInput, List<int[]> executedTestInputs, ConstraintChecker checker) {
        int[] candidateTestInput = Arrays.copyOf(testInput, testInput.length);

        int optimalValue = -1;
        int minimalNumberOfSimilarTestCases = Integer.MAX_VALUE;

        IntArrayList possibleValues = new IntArrayList();

        IntStream.range(0, numberOfValues).forEach(possibleValues::add);
        Collections.shuffle(possibleValues);

        for (int value : possibleValues) {
            if (checker.isExtensionValid(candidateTestInput, parameter, value)) {
                // valid value found
                candidateTestInput[parameter] = value;

                // lambda expression needs final variable
                final int valueToCheck = value;

                int numberOfSimilarTestCases = (int) executedTestInputs.stream().filter(input -> input[parameter] == valueToCheck).count();

                // if given value decreases number of similar test inputs, set this value as (current) optimal value
                if (numberOfSimilarTestCases < minimalNumberOfSimilarTestCases) {
                    optimalValue = value;
                    minimalNumberOfSimilarTestCases = numberOfSimilarTestCases;
                }
            }
        }

        // no valid value could be found
        if (optimalValue == -1) {
            return Optional.empty();
        }

        return Optional.of(new ParameterValuePair(parameter, optimalValue));
    }

    /**
     * @param parameter parameter to find a value for
     * @param numberOfValues number of possible values the given parameter can take
     * @param testInput partial test input generated so far and which needs to be extended
     * @param executedTestInputs all test inputs generated and executed so far. Next test inputs must not represent sub-
     *                           combinations of them.
     * @param checker Constraint checker to ensure that assigned value is valid in given test input.
     *
     * @return valid value extending the given partial test input for the given parameter.
     */
    public static Optional<ParameterValuePair> valueForParameter(int parameter, int numberOfValues, int[] testInput, Set<IntList> executedTestInputs, ConstraintChecker checker) {
        int[] candidateTestInput = Arrays.copyOf(testInput, testInput.length);

        IntSet values = new IntArraySet();

        IntStream.range(0, numberOfValues).forEach(values::add);
        values.removeAll(executedTestInputs.stream().map(IntCollection::toIntArray).filter(input -> CombinationUtil.contains(input, testInput)).map(input -> input[parameter]).collect(Collectors.toList()));

        IntArrayList possibleValues = new IntArrayList(values);
        Collections.shuffle(possibleValues);

        for (int value : possibleValues) {
            if (checker.isExtensionValid(candidateTestInput, parameter, value)) {
                // valid value found
                candidateTestInput[parameter] = value;

                return Optional.of(new ParameterValuePair(parameter, value));
            }
        }

        // no valid value could be found
        return Optional.empty();

    }
}
