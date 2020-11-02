package de.rwth.swc.coffee4j.algorithmic.sequential.generator;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil.contains;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlgorithmTestUtil {

    public static void verifyAllSeedsPresent(Collection<int[]> seeds, Collection<int[]> testSuite) {
        for(int[] seed : seeds) {
            assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, seed)),
                    () -> "test suite does not contain seed " + Arrays.toString(seed));
        }
    }

    public static void verifyAllRelevantSeedsPresent(List<int[]> seeds,
                                                     List<int[]> testSuite,
                                                     ConstraintChecker constraintChecker) {
        final List<int[]> relevantSeeds = seeds.stream()
                .filter(constraintChecker::isValid)
                .collect(Collectors.toList());

        assertTrue(testSuite.size() >= relevantSeeds.size());

        for(int[] seed : relevantSeeds) {
            assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, seed)),
                    () -> "test suite does not contain seed " + Arrays.toString(seed));
        }
    }

    public static void verifyAllRelevantCombinationsPresent(List<int[]> testSuite,
                                                            int[] parameterSizes,
                                                            int strength,
                                                            ConstraintChecker constraintChecker) {
        final List<IntSet> parameterCombinations = Combinator.computeParameterCombinations(IntStream.range(0, parameterSizes.length).toArray(), strength);

        for (IntSet parameterCombination : parameterCombinations) {
            final List<int[]> relevantCombinations = computeCartesianProduct(parameterCombination, parameterSizes)
                    .stream()
                    .filter(constraintChecker::isValid)
                    .collect(Collectors.toList());

            for (int[] combination : relevantCombinations) {
                assertTrue(containsCombination(testSuite, combination),
                        () -> "" + Arrays.toString(combination) + " missing");
            }
        }
    }

    public static void verifyAllCombinationsPresent(List<int[]> testSuite, int[] parameterSizes, int strength) {
        final List<IntSet> parameterCombinations = Combinator.computeParameterCombinations(IntStream.range(0, parameterSizes.length).toArray(), strength);

        for (IntSet parameterCombination : parameterCombinations) {
            final List<int[]> combinations = computeCartesianProduct(parameterCombination, parameterSizes);

            for (int[] combination : combinations) {
                assertTrue(containsCombination(testSuite, combination),
                        () -> "" + Arrays.toString(combination) + " missing");
            }
        }
    }

    private static List<int[]> computeCartesianProduct(IntSet parameterCombination, int[] parameterSizes) {
        final Int2IntMap parameterSizeMap = new Int2IntOpenHashMap(parameterSizes.length);

        for (int parameter : parameterCombination) {
            parameterSizeMap.put(parameter, parameterSizes[parameter]);
        }

        return Combinator.computeCartesianProduct(parameterSizeMap, parameterSizes.length);
    }

    private static boolean containsCombination(List<int[]> testSuite, int[] combination) {
        for (int[] testInput : testSuite) {
            if (contains(testInput, combination)) {
                return true;
            }
        }

        return false;
    }
}
