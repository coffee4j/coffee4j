package de.rwth.swc.coffee4j.algorithmic.interleaving;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Coverage Map storing all uncovered t-tuples.
 */
public class CoverageMap {
    private final Set<int[]> uncoveredCombinations;
    private final ConstraintChecker checker;
    private final int numberOfParameters;
    private final Set<IntList> passingTestInputs;
    private final IntSet parameters = new IntArraySet();

    /**
     * @param parameterSizes contains number of possible values for each parameter
     * @param strength testing strength t
     * @param checker Constraint checker used to remove invalid t-tuples
     */
    public CoverageMap(int[] parameterSizes, int strength, ConstraintChecker checker) {
        Preconditions.notNull(parameterSizes);
        Preconditions.check(strength > 0 && strength <= parameterSizes.length);

        this.checker = Preconditions.notNull(checker);

        this.uncoveredCombinations = Combinator.computeCombinations(parameterSizes, strength)
                .stream()
                .filter(checker::isValid)
                .collect(Collectors.toSet());

        numberOfParameters = parameterSizes.length;

        for (int i = 0; i < numberOfParameters; i++) {
            parameters.add(i);
        }
        
        this.passingTestInputs = new HashSet<>();
    }

    /**
     * @param coverageMap {@link CoverageMap} to copy.
     * @param checker Constraint checker used to remove invalid t-tuples.
     */
    public CoverageMap(CoverageMap coverageMap, ConstraintChecker checker) {
        this.uncoveredCombinations = new HashSet<>(coverageMap.uncoveredCombinations);
        this.numberOfParameters = coverageMap.numberOfParameters;
        this.passingTestInputs = new HashSet<>(coverageMap.passingTestInputs);
        this.checker = checker;
    }

    public Set<IntList> getPassingTestInputs() {
        return passingTestInputs;
    }

    /**
     * @return true iff all valid t-tuples are covered
     */
    public boolean allCombinationsCovered() {
        return uncoveredCombinations.isEmpty();
    }

    /**
     * remove all combinations contained by given test input
     * @param testInput successful test input in normal control-flow
     */
    public void updateCoverage(int[] testInput) {
        passingTestInputs.add(new IntArrayList(testInput));
        uncoveredCombinations.removeIf(combination -> CombinationUtil.contains(testInput, combination));
    }

    /**
     * update coverage: remove all tuples that became invalid after new forbidden combination was added
     * to {@link #checker}
     */
    public void updateCoverage() {
        uncoveredCombinations.removeIf(combination -> !checker.isValid(combination));
    }

    /**
     * @param testInput combination to compute covered t-tuples for
     * @return number of t-tuples covered by given combination
     */
    public long getNumberOfCoveredCombinationsByTestInput(int[] testInput) {
        // numberOfSetValues(testInput) >= testing strength
        long numberOfCombinationsCoveredByTestInput = uncoveredCombinations
                .stream()
                .filter(combination -> CombinationUtil.contains(testInput, combination))
                .count();

        // numberOfSetValues(testInput) < testing strength
        long numberOfCombinationsContainingPartialTestInput = uncoveredCombinations
                .stream()
                .filter(combination -> (numberOfSetValues(testInput) < numberOfSetValues(combination))
                    && CombinationUtil.contains(combination, testInput))
                .count();

        return numberOfCombinationsContainingPartialTestInput + numberOfCombinationsCoveredByTestInput;
    }

    private int numberOfSetValues(int[] testInput) {
        return (int) Arrays.stream(testInput).filter(value -> value != -1).count();
    }

    /**
     * @param forbiddenPairs parameter-value paris to be excluded from search of optimal pair
     * @return parameter-value pair covering most uncovered t-tuples
     */
    public ParameterValuePair getParameterValuePairCoveringMostCombinations(Set<ParameterValuePair> forbiddenPairs) {
        int optimalParameter = -1;
        int optimalValue = -1;
        long maximumNumberOfCoveredCombinations = 0;

        IntList randomOrderParameters = new IntArrayList(parameters);
        Collections.shuffle(randomOrderParameters);

        for (int parameter : randomOrderParameters) {
            Int2IntMap parameterValueCoverageCount = new Int2IntArrayMap();

            for (int[] combination : uncoveredCombinations) {
                if (combination[parameter] != -1 && !forbiddenPairs.contains(new ParameterValuePair(parameter, combination[parameter]))) {
                    parameterValueCoverageCount.put(combination[parameter], parameterValueCoverageCount.getOrDefault(combination[parameter], 0) + 1);
                }
            }

            Optional<Int2IntMap.Entry> optimalValueForParameter = parameterValueCoverageCount.int2IntEntrySet().stream().max(Comparator.comparing(Int2IntMap.Entry::getIntValue));

            if (optimalValueForParameter.isPresent() && optimalValueForParameter.get().getIntValue() > maximumNumberOfCoveredCombinations) {
                optimalParameter = parameter;
                optimalValue = optimalValueForParameter.get().getIntKey();
                maximumNumberOfCoveredCombinations = optimalValueForParameter.get().getIntValue();
            }
        }

        return new ParameterValuePair(optimalParameter, optimalValue);
    }
}
