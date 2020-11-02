package de.rwth.swc.coffee4j.algorithmic.util;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Test class for {@link Combinator}.
 */
class CombinatorTest {
    
    @Test
    void allParameterValuesReturnedAsCombinationsIfOnlyOneParameter() {
        Int2IntMap parameters = new Int2IntOpenHashMap(new int[]{0}, new int[]{3});
        List<int[]> combinations = Combinator.computeCartesianProduct(parameters, 1);
        
        assertEquals(3, combinations.size());
        assertArrayEquals(new int[]{0}, combinations.get(0));
        assertArrayEquals(new int[]{1}, combinations.get(1));
        assertArrayEquals(new int[]{2}, combinations.get(2));
    }
    
    @Test
    void combinationsAreFilledUpToRequiredSize() {
        Int2IntMap parameters = new Int2IntOpenHashMap(new int[]{0}, new int[]{3});
        List<int[]> combinations = Combinator.computeCartesianProduct(parameters, 4);
        
        assertArrayEquals(new int[]{0, CombinationUtil.NO_VALUE, CombinationUtil.NO_VALUE, CombinationUtil.NO_VALUE}, combinations.get(0));
        assertArrayEquals(new int[]{1, CombinationUtil.NO_VALUE, CombinationUtil.NO_VALUE, CombinationUtil.NO_VALUE}, combinations.get(1));
        assertArrayEquals(new int[]{2, CombinationUtil.NO_VALUE, CombinationUtil.NO_VALUE, CombinationUtil.NO_VALUE}, combinations.get(2));
    }
    
    @Test
    void computeCartesianProductOfTwoParameters() {
        Int2IntMap parameters = new Int2IntOpenHashMap(new int[]{0, 1}, new int[]{4, 4});
        List<int[]> combinations = Combinator.computeCartesianProduct(parameters, 4);
        
        List<int[]> expectedCombinations = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                expectedCombinations.add(new int[]{j, i, CombinationUtil.NO_VALUE, CombinationUtil.NO_VALUE});
            }
        }
        
        for (int i = 0; i < expectedCombinations.size(); i++) {
            assertArrayEquals(expectedCombinations.get(i), combinations.get(i));
        }
    }
    
    @Test
    void cartesianProductOfMultipleParametersHasRightSize() {
        Int2IntMap parameters = new Int2IntOpenHashMap(new int[]{0, 1, 2, 3, 4, 5}, new int[]{5, 8, 2, 4, 24, 100});
        List<int[]> combinations = Combinator.computeCartesianProduct(parameters, 6);
        
        int expectedNumberOfCombinations = 5 * 8 * 2 * 4 * 24 * 100; //768000
        assertEquals(expectedNumberOfCombinations, combinations.size());
    }
    
    @Test
    void calculateParameterCombinationsWithOneParameter() {
        int[] parameters = new int[]{0};
        List<IntSet> parameterCombinations = Combinator.computeParameterCombinations(parameters, 1);
        
        assertEquals(1, parameterCombinations.size());
        assertEquals(new IntOpenHashSet(Collections.singletonList(0)), parameterCombinations.get(0));
    }
    
    @Test
    void parameterCombinationShouldBeSetItselfIfStrengthEqualsToNumberOfParameters() {
        List<IntSet> parameterCombinations = Combinator.computeParameterCombinations(IntStream.range(0, 100).toArray(), 100);
        
        assertEquals(1, parameterCombinations.size());
        assertEquals(new IntArraySet(IntStream.range(0, 100).toArray()), parameterCombinations.get(0));
    }

    @Test
    void computeCorrectParameterCombinationsForT0() {
        List<IntSet> parameterCombinations = Combinator.computeParameterCombinations(IntStream.range(0, 4).toArray(), 0);

        assertEquals(0, parameterCombinations.size());
    }

    @Test
    void computeCorrectParameterCombinationsForT1() {
        List<IntSet> parameterCombinations = Combinator.computeParameterCombinations(IntStream.range(0, 4).toArray(), 1);

        assertEquals(4, parameterCombinations.size());
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(List.of(0))));
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(List.of(1))));
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(List.of(2))));
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(List.of(3))));
    }

    @Test
    void computeCorrectParameterCombinationsForT2() {
        List<IntSet> parameterCombinations = Combinator.computeParameterCombinations(IntStream.range(0, 4).toArray(), 2);
        
        assertEquals(6, parameterCombinations.size());
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(List.of(0, 1))));
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(List.of(0, 2))));
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(List.of(0, 3))));
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(List.of(1, 2))));
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(List.of(1, 3))));
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(List.of(2, 3))));
    }
    
    @Test
    void computeNonConsecutiveRightParameterCombinations() {
        int[] parameters = new int[]{0, 2, 3};
        List<IntSet> parameterCombinations = Combinator.computeParameterCombinations(parameters, 2);
        
        assertEquals(3, parameterCombinations.size());
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(Arrays.asList(0, 2))));
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(Arrays.asList(0, 3))));
        assertTrue(parameterCombinations.contains(new IntOpenHashSet(Arrays.asList(2, 3))));
    }
    
    @Test
    void computeNoParameterCombinationsIfSizeTooLarge() {
        int[] parameters = new int[]{0, 1};
        List<IntSet> parameterCombinations = Combinator.computeParameterCombinations(parameters, 3);
        
        assertTrue(parameterCombinations.isEmpty());
    }
    
    @Test
    void shouldWorkWithNonConsecutiveParameters() {
        Int2IntMap parameters = new Int2IntOpenHashMap();
        parameters.put(0, 2);
        parameters.put(3, 2);
        
        List<int[]> combinations = Combinator.computeCartesianProduct(parameters, 4);
        
        assertArrayEquals(new int[]{0, CombinationUtil.NO_VALUE, CombinationUtil.NO_VALUE, 0}, combinations.get(0));
    }
    
    @Test
    void sizeMustBeLargerThanMaxParameterIndex() {
        Int2IntMap parameters = new Int2IntOpenHashMap();
        parameters.put(0, 2);
        parameters.put(5, 2);
        
        assertThrows(IllegalArgumentException.class, () -> Combinator.computeCartesianProduct(parameters, 5));
    }

    @Test
    void computeCorrectSingleNegativeParameterCombinationsForA0B0() {
        int[] negativeParameters = {0};

        assertThrows(IllegalArgumentException.class,
                () -> Combinator.computeNegativeParameterCombinations(IntStream.range(0, 4).toArray(), negativeParameters, 0, 0));
    }

    @Test
    void computeCorrectSingleNegativeParameterCombinationsForA1B0() {
        int[] negativeParameters = {0};
        
        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 4).toArray(), negativeParameters, 1, 0);
        
        assertEquals(1, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Collections.singletonList(0))));
    }
    
    @Test
    void computeCorrectSingleNegativeParameterCombinationsForA1B1() {
        int[] negativeParameters = {0};
        
        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 4).toArray(), negativeParameters, 1,1);
        
        assertEquals(3, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 1))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 2))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 3))));
    }
    
    @Test
    void computeCorrectSingleNegativeParameterCombinationsForA1B2() {
        int[] negativeParameters = {0};
        
        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 4).toArray(), negativeParameters, 1,2);
        
        assertEquals(3, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 1, 2))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 1, 3))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 2, 3))));
    }
    
    @Test
    void computeCorrectSingleNegativeParameterCombinationsForA1B3() {
        int[] negativeParameters = {0};
        
        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 4).toArray(), negativeParameters, 1,3);
        
        assertEquals(1, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 1, 2, 3))));
    }
    
    @Test
    void computeCorrectSingleNegativeParameterCombinationsForA1B4() {
        int[] negativeParameters = {0};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 4).toArray(), negativeParameters, 1, 4);

        assertEquals(1, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 1, 2, 3))));
    }

    @Test
    void computeCorrectPairOfNegativeParameterCombinationsForA1B0() {
        int[] negativeParameters = {0, 2};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 5).toArray(), negativeParameters, 1, 0);

        assertEquals(2, result.size());
        assertTrue(result.contains(new IntOpenHashSet(List.of(0))));
        assertTrue(result.contains(new IntOpenHashSet(List.of(2))));
    }

    @Test
    void computeCorrectPairOfNegativeParameterCombinationsForA2B0() {
        int[] negativeParameters = {0, 2};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 5).toArray(), negativeParameters, 2, 0);

        assertEquals(1, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 2))));
    }

    @Test
    void computeCorrectPairOfNegativeParameterCombinationsForA1B1() {
        int[] negativeParameters = {0, 2};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 5).toArray(), negativeParameters, 1,1);

        assertEquals(6, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 1))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 3))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 4))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(2, 1))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(2, 3))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(2, 4))));
    }
    @Test
    void computeCorrectPairOfNegativeParameterCombinationsForA2B1() {
        int[] negativeParameters = {0, 2};
        
        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 5).toArray(), negativeParameters, 2,1);
        
        assertEquals(3, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 2, 1))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 2, 3))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 2, 4))));
    }

    @Test
    void computeCorrectPairOfNegativeParameterCombinationsForA1B2() {
        int[] negativeParameters = {0, 2};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 5).toArray(), negativeParameters, 1, 2);

        assertEquals(6, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 1, 3))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 1, 4))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 3, 4))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(2, 1, 3))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(2, 1, 4))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(2, 3, 4))));
    }

    @Test
    void computeCorrectPairOfNegativeParameterCombinationsForA2B2() {
        int[] negativeParameters = {0, 2};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 5).toArray(), negativeParameters, 2, 2);

        assertEquals(3, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 2, 1, 3))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 2, 1, 4))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 2, 3, 4))));
    }

    @Test
    void computeCorrectPairOfNegativeParameterCombinationsForA1B3() {
        int[] negativeParameters = {0, 2};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 5).toArray(), negativeParameters, 1,3);

        assertEquals(2, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 1, 3, 4))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(2, 1, 3, 4))));
    }

    @Test
    void computeCorrectPairOfNegativeParameterCombinationsForA2B3() {
        int[] negativeParameters = {0, 2};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 5).toArray(), negativeParameters, 2, 3);

        assertEquals(1, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 2, 1, 3, 4))));
    }

    @Test
    void computeCorrectPairOfNegativeParameterCombinationsForA1B4() {
        int[] negativeParameters = {0, 2};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 5).toArray(), negativeParameters, 1, 4);

        assertEquals(2, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 1, 3, 4))));
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(2, 1, 3, 4))));
    }

    @Test
    void computeCorrectPairOfNegativeParameterCombinationsForA2B4() {
        int[] negativeParameters = {0, 2};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 5).toArray(), negativeParameters, 2, 4);

        assertEquals(1, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 2, 1, 3, 4))));
    }

    @Test
    void computeCorrectExhaustiveNegativeParameterCombinationsForA1B0() {
        int[] negativeParameters = {0, 1};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 2).toArray(), negativeParameters, 1, 0);

        assertEquals(2, result.size());
        assertTrue(result.contains(new IntOpenHashSet(List.of(0))));
        assertTrue(result.contains(new IntOpenHashSet(List.of(1))));
    }

    @Test
    void computeCorrectExhaustiveNegativeParameterCombinationsForA2B0() {
        int[] negativeParameters = {0, 1};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 2).toArray(), negativeParameters, 2, 0);

        assertEquals(1, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 1))));
    }

    @Test
    void computeCorrectExhaustiveNegativeParameterCombinationsForA1B1() {
        int[] negativeParameters = {0, 1};

        List<IntSet> result = Combinator.computeNegativeParameterCombinations(IntStream.range(0, 2).toArray(), negativeParameters, 2, 1);

        assertEquals(1, result.size());
        assertTrue(result.contains(new IntOpenHashSet(Arrays.asList(0, 1))));
    }

    @Test
    void preconditionsOfComputeSubCombinationsOfSize() {
        assertThrows(NullPointerException.class, () -> Combinator.computeSubCombinations(null, 1));
        assertThrows(IllegalArgumentException.class, () -> Combinator.computeSubCombinations(CombinationUtil.emptyCombination(2), -1));
    }
    
    @ParameterizedTest
    @MethodSource("sizedSubCombinationTestInputs")
    void combinationHasRightSubCombinationsWithGivenSize(int[] combination, int size, List<int[]> expectedSubCombinations) {
        final List<int[]> computedSubCombinations = Combinator.computeSubCombinations(combination, size);
        final Set<IntArrayWrapper> wrappedComputedSubCombinations = new HashSet<>(IntArrayWrapper.wrapToList(computedSubCombinations));
        final Set<IntArrayWrapper> wrappedExpectedSubCombinations = new HashSet<>(IntArrayWrapper.wrapToList(expectedSubCombinations));
        
        assertEquals(wrappedExpectedSubCombinations, wrappedComputedSubCombinations);
    }
    
    private static Stream<Arguments> sizedSubCombinationTestInputs() {
        return Stream.of(
                Arguments.of(CombinationUtil.emptyCombination(0), 1, Collections.emptyList()),
                Arguments.of(CombinationUtil.emptyCombination(0), 100, Collections.emptyList()),
                Arguments.of(new int[]{1, 2, 3, 4}, 8, Collections.emptyList()),
                Arguments.of(CombinationUtil.emptyCombination(1), 1, Collections.emptyList()),
                Arguments.of(CombinationUtil.emptyCombination(6), 1, Collections.emptyList()),
                Arguments.of(new int[0], 0, Collections.singletonList(CombinationUtil.emptyCombination(0))),
                Arguments.of(new int[]{1}, 0, Collections.singletonList(CombinationUtil.emptyCombination(1))),
                Arguments.of(new int[]{0, 1, 2, 3, 4, 5, 6, 7}, 0, Collections.singletonList(CombinationUtil.emptyCombination(8))),
                Arguments.of(new int[]{1}, 1, Collections.singletonList(new int[]{1})),
                Arguments.of(new int[]{1, 2, 3, 4, 5}, 1, Arrays.asList(new int[]{1, -1, -1, -1, -1}, new int[]{-1, 2, -1, -1, -1}, new int[]{-1, -1, 3, -1, -1}, new int[]{-1, -1, -1, 4, -1}, new int[]{-1, -1, -1, -1, 5})),
                Arguments.of(new int[]{-1, 2, -1, 3, -1}, 1, Arrays.asList(new int[]{-1, 2, -1, -1, -1}, new int[]{-1, -1, -1, 3, -1})),
                Arguments.of(new int[]{1, 2}, 2, List.of(new int[]{1, 2})),
                Arguments.of(new int[]{1, 2, 3, 4}, 2, Arrays.asList(new int[]{1, 2, -1, -1}, new int[]{1, -1, 3, -1}, new int[]{1, -1, -1, 4}, new int[]{-1, 2, 3, -1}, new int[]{-1, 2, -1, 4}, new int[]{-1, -1, 3, 4})),
                Arguments.of(new int[]{-1, 2, -1, 4, -1, 6, -1, 8}, 2, Arrays.asList(new int[]{-1, 2, -1, 4, -1, -1, -1, -1}, new int[]{-1, 2, -1, -1, -1, 6, -1, -1}, new int[]{-1, 2, -1, -1, -1, -1, -1, 8}, new int[]{-1, -1, -1, 4, -1, 6, -1, -1}, new int[]{-1, -1, -1, 4, -1, -1, -1, 8}, new int[]{-1, -1, -1, -1, -1, 6, -1, 8})));
    }
    
    @Test
    void preconditionsOfComputeSubCombinations() {
        assertThrows(NullPointerException.class, () -> Combinator.computeSubCombinations(null));
    }
    
    @ParameterizedTest
    @MethodSource("subCombinationsTestInputs")
    void combinationHasRightSubCombinations(int[] combination, List<int[]> expectedSubCombinations) {
        final List<int[]> computedSubCombinations = Combinator.computeSubCombinations(combination);
        final Set<IntArrayWrapper> wrappedComputedSubCombinations = new HashSet<>(IntArrayWrapper.wrapToList(computedSubCombinations));
        final Set<IntArrayWrapper> wrappedExpectedSubCombinations = new HashSet<>(IntArrayWrapper.wrapToList(expectedSubCombinations));
        
        assertEquals(wrappedExpectedSubCombinations, wrappedComputedSubCombinations);
    }
    
    private static Stream<Arguments> subCombinationsTestInputs() {
        return Stream.of(Arguments.of(CombinationUtil.emptyCombination(0), Collections.emptyList()), Arguments.of(CombinationUtil.emptyCombination(1), Collections.emptyList()), Arguments.of(CombinationUtil.emptyCombination(100), Collections.emptyList()), Arguments.of(new int[]{1}, Collections.singletonList(new int[]{1})), Arguments.of(new int[]{-1, 1}, Collections.singletonList(new int[]{-1, 1})), Arguments.of(new int[]{1, -1}, Collections.singletonList(new int[]{1, -1})), Arguments.of(new int[]{1, 2}, Arrays.asList(new int[]{1, -1}, new int[]{-1, 2}, new int[]{1, 2})), Arguments.of(new int[]{-1, 1, -1, 2}, Arrays.asList(new int[]{-1, 1, -1, -1}, new int[]{-1, -1, -1, 2}, new int[]{-1, 1, -1, 2})), Arguments.of(new int[]{1, 2, 3}, Arrays.asList(new int[]{1, -1, -1}, new int[]{-1, 2, -1}, new int[]{-1, -1, 3}, new int[]{1, 2, -1}, new int[]{1, -1, 3}, new int[]{-1, 2, 3}, new int[]{1, 2, 3})));
    }
    
    @Test
    void preconditionsWhenComputingCombinationsOfParameterSubSet() {
        assertThrows(NullPointerException.class, () -> Combinator.computeCombinations(null, IntSets.EMPTY_SET));
        assertThrows(NullPointerException.class, () -> Combinator.computeCombinations(new int[0], null));
        assertThrows(IllegalArgumentException.class, () ->
                Combinator.computeCombinations(new int[0], IntSets.singleton(1)));
        assertThrows(IllegalArgumentException.class, () ->
                Combinator.computeCombinations(new int[] {2, 3, 4}, IntSets.EMPTY_SET));
        assertThrows(IllegalArgumentException.class, () ->
                Combinator.computeCombinations(new int[] {2, 3, 4}, new IntOpenHashSet(new int[] {0, 1, 5})));
    }
    
    @ParameterizedTest
    @MethodSource("computeCombinationsOfParameterSubSet")
    void computeCombinationsOfParameterSubSet(int[] parameterSizes, int[] parameters, Set<int[]> expectedCombinations) {
        final Set<int[]> actualCombinations = Combinator.computeCombinations(
                parameterSizes, new IntOpenHashSet(parameters));
        final Set<IntArrayWrapper> wrappedExpectedCombinations = IntArrayWrapper.wrapToSet(expectedCombinations);
        final Set<IntArrayWrapper> wrappedActualCombinations = IntArrayWrapper.wrapToSet(actualCombinations);
        
        assertEquals(wrappedExpectedCombinations, wrappedActualCombinations);
    }
    
    private static Stream<Arguments> computeCombinationsOfParameterSubSet() {
        return Stream.of(
                arguments(new int[] {2}, new int[] {0}, Set.of(new int[] {0}, new int[] {1})),
                arguments(new int[] {3, 2, 4}, new int[] {1}, Set.of(new int[] {-1, 0, -1}, new int[] {-1, 1, -1})),
                arguments(new int[] {2, 2}, new int[] {0, 1}, Set.of(
                        new int[] {0, 0}, new int[] {0, 1}, new int[] {1, 0}, new int[] {1, 1})),
                arguments(new int[] {2, 3, 2, 3, 2, 3}, new int[] {2, 4, 5}, Set.of(
                        new int[] {-1, -1, 0, -1, 0, 0}, new int[] {-1, -1, 0, -1, 0, 1},
                        new int[] {-1, -1, 0, -1, 0, 2}, new int[] {-1, -1, 0, -1, 1, 0},
                        new int[] {-1, -1, 0, -1, 1, 1}, new int[] {-1, -1, 0, -1, 1, 2},
                        new int[] {-1, -1, 1, -1, 0, 0}, new int[] {-1, -1, 1, -1, 0, 1},
                        new int[] {-1, -1, 1, -1, 0, 2}, new int[] {-1, -1, 1, -1, 1, 0},
                        new int[] {-1, -1, 1, -1, 1, 1}, new int[] {-1, -1, 1, -1, 1, 2})));
    }
    
    @ParameterizedTest
    @MethodSource("computeCombinations")
    void computeCombinations(int[] parameters, int size, List<int[]> expectedCombinations) {
        final Set<int[]> computedCombinations = Combinator.computeCombinations(parameters, size);
        final Set<IntArrayWrapper> wrappedComputedCombinations = new HashSet<>(IntArrayWrapper.wrapToSet(computedCombinations));
        final Set<IntArrayWrapper> wrappedExpectedCombinations = new HashSet<>(IntArrayWrapper.wrapToList(expectedCombinations));
        
        assertEquals(wrappedExpectedCombinations, wrappedComputedCombinations);
    }
    
    private static Stream<Arguments> computeCombinations() {
        return Stream.of(
                arguments(new int[0], 0, List.of()),
                arguments(new int[]{2, 2, 2}, 0, List.of()),
                arguments(new int[]{2}, 1, List.of(new int[]{0}, new int[]{1})),
                arguments(new int[]{2, 2}, 3, List.of()),
                arguments(new int[]{2, 2}, 1, List.of(
                        new int[]{0, CombinationUtil.NO_VALUE}, new int[]{1, CombinationUtil.NO_VALUE},
                        new int[]{CombinationUtil.NO_VALUE, 0}, new int[]{CombinationUtil.NO_VALUE, 1})));
    }
}
