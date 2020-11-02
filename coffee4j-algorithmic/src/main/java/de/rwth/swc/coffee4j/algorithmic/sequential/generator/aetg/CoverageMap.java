package de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg;

import de.rwth.swc.coffee4j.algorithmic.constraint.DynamicHardConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.IpogAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of a coverage map that tracks how many t-combinations are already covered.
 * <p>
 * It also provides the means for handling constraints and some supporting functions for {@link AetgSat}. Combinations
 * are stored as arrays. A more efficient implementations should make use of a bitset like the coverage map of the
 * {@link IpogAlgorithm}.
 */
class CoverageMap {

    private final Set<int[]> combinations;
    private final int length;
    private final DynamicHardConstraintChecker checker;

    CoverageMap(int[] parameterSizes, int strength, DynamicHardConstraintChecker checker) {
        this(Combinator.computeCombinations(parameterSizes, strength), parameterSizes.length, checker);
    }

    CoverageMap(Set<int[]> combinations, int length, DynamicHardConstraintChecker checker) {
        Preconditions.notNull(combinations);
        Preconditions.check(!combinations.isEmpty());
        Preconditions.check(combinations.stream().allMatch(s -> s.length == length));

        if (checker.getInvolvedParameters().isEmpty()) {
            this.combinations = new HashSet<>(combinations);
        } else {
            this.combinations = combinations.stream().filter(checker::isValid).collect(Collectors.toSet());
        }
        this.length = length;
        this.checker = checker;
    }

    void addForbiddenCombination(int[] combination) {
        this.checker.addConstraint(combination);
        this.combinations.removeIf(c -> !checker.isValid(c));
    }

    boolean hasUncoveredCombinations() {
        return !combinations.isEmpty();
    }

    void updateSubCombinationCoverage(int[] testCase) {
        combinations.removeIf(c -> CombinationUtil.contains(testCase, c));
    }
    
    long getNumberOfUncoveredCombinations(int[] testCase) {
        return combinations.stream().filter(c -> contains(testCase, c)).count();
    }
    
    private boolean contains(int[] testCase, int[] combination) {
        for (int parameter = 0; parameter < testCase.length; parameter++) {
            if (testCase[parameter] != CombinationUtil.NO_VALUE && combination[parameter] != CombinationUtil.NO_VALUE && testCase[parameter] != combination[parameter]) {
                return false;
            }
        }
        return true;
    }

    ParameterValuePair getMostCommonValue(Set<ParameterValuePair> forbiddenPairs, IntSet forbiddenParameters) {

        int bestValue = -1;
        int bestParameter = -1;

        int highestCount = 0;

        for (int parameter = 0; parameter < length; parameter++) {
            if (!forbiddenParameters.contains(parameter)) {
                Int2IntMap valueCount = new Int2IntArrayMap();
                for (int[] combination : combinations) {
                    if (combination[parameter] != CombinationUtil.NO_VALUE && !forbiddenPairs.contains(new ParameterValuePair(parameter, combination[parameter]))) {
                        valueCount.put(combination[parameter], valueCount.getOrDefault(combination[parameter], 0) + 1);
                    }
                }
                Optional<Int2IntMap.Entry> best = valueCount.int2IntEntrySet().stream().max(Comparator.comparing(Int2IntMap.Entry::getIntValue));
                if (best.isPresent() && best.get().getIntValue() > highestCount) {
                    bestParameter = parameter;
                    bestValue = best.get().getIntKey();
                    highestCount = best.get().getIntValue();
                }
            }
        }
        return new ParameterValuePair(bestParameter, bestValue);
    }

    int getNumberOfUncoveredCombinations() {
        return combinations.size();
    }
}
