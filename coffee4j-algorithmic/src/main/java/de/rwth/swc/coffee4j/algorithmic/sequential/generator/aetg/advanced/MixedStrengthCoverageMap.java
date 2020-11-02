package de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg.advanced;

import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveStrengthGroup;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg.AetgSat;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.IpogAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of a coverage map that tracks how many t-combinations are already covered.
 * <p>
 * It also provides the means for handling constraints and some supporting functions for {@link AetgSat}. Combinations
 * are stored as arrays. A more efficient implementations should make use of a bitset like the coverage map of the
 * {@link IpogAlgorithm}.
 */
class MixedStrengthCoverageMap {

    private final Set<int[]> combinations;
    private final int length;

    MixedStrengthCoverageMap(TestModel testModel) {
        Preconditions.notNull(testModel, "testModel required");
        
        this.length = testModel.getNumberOfParameters();
        this.combinations = computeCombination(testModel);
    }
    
    private Set<int[]> computeCombination(TestModel testModel) {
        final Set<IntSet> parameterCombinations = computeParameterCombinations(testModel);
        
        return parameterCombinations.stream()
                .map(parameterCombination ->
                        Combinator.computeCombinations(testModel.getParameterSizes(), parameterCombination))
                .flatMap(Collection::stream)
                .filter(testModel.getConstraintChecker()::isValid)
                .collect(Collectors.toSet());
    }
    
    private Set<IntSet> computeParameterCombinations(TestModel testModel) {
        final Set<IntSet> mixedStrengthParameterCombinations = testModel.getMixedStrengthGroups().stream()
                .map(PrimitiveStrengthGroup::getAllSubGroups)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        
        final Set<IntSet> defaultStrengthParameterCombinations = Combinator.computeParameterCombinations(
                IntStream.range(0, testModel.getNumberOfParameters()).toArray(),
                testModel.getDefaultTestingStrength()).stream()
                        .filter(defaultCombination -> mixedStrengthParameterCombinations.stream()
                                .noneMatch(mixedCombination -> mixedCombination.containsAll(defaultCombination)))
                        .collect(Collectors.toSet());
        
        final Set<IntSet> allParameterCombinations = new HashSet<>(
                mixedStrengthParameterCombinations.size() + defaultStrengthParameterCombinations.size());
        allParameterCombinations.addAll(mixedStrengthParameterCombinations);
        allParameterCombinations.addAll(defaultStrengthParameterCombinations);
        
        return allParameterCombinations;
    }

    boolean hasUncoveredCombinations() {
        return !combinations.isEmpty();
    }

    void updateSubCombinationCoverage(int[] testCase) {
        combinations.removeIf(c -> contains(testCase, c));
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
                Optional<Int2IntMap.Entry> best = valueCount.int2IntEntrySet().stream()
                        .max(Comparator.comparing(Int2IntMap.Entry::getIntValue));
                if (best.isPresent() && best.get().getIntValue() > highestCount) {
                    bestParameter = parameter;
                    bestValue = best.get().getIntKey();
                    highestCount = best.get().getIntValue();
                }
            }
        }
        return new ParameterValuePair(bestParameter, bestValue);
    }
    
}
