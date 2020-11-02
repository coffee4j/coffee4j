package de.rwth.swc.coffee4j.algorithmic.util;

import it.unimi.dsi.fastutil.ints.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil.NO_VALUE;
import static de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil.emptyCombination;

/**
 * Utility methods used for combinatorial tasks in the context of combinatorial
 * test generation.
 * <p>
 * Uses the indexing schema introduced in
 * {@link CombinationUtil}.
 */
public final class Combinator {
    
    private static final String PARAMETERS_NOT_NULL = "Parameters cannot be null";
    private static final String AT_LEAST_ONE_PARAMETER = "At least one parameter has to be given";
    private static final String TOO_MANY_PARAMETERS = "The combination size cannot be smaller than the number" + "of parameters";
    private static final String TOO_HIGH_PARAMETERS = "The combination size cannot be smaller than the highest" + "parameter number";
    private static final String SIZE_NOT_NEGATIVE = "The size of combinations cannot be negative";
    
    private Combinator() {
    }
    
    /**
     * Computes the full cartesian product of the given parameters.
     * Each entry form the cartesian product is stored in an array of size
     * combinationSize so that the combinations can be used for larger test
     * inputs in later iterations of the IPOG algorithm.
     *
     * @param parameters      the parameters for whose values the cartesian product
     *                        shall be computed. Must no be {@code null} or empty
     * @param combinationSize the size of the combinations returned. Empty
     *                        places are filled with
     *                        {@link CombinationUtil#NO_VALUE}.
     *                        Must not be smaller than the number of parameters
     * @return all tuples of the cartesian product
     * @throws NullPointerException     if parameters are {@code null}
     * @throws IllegalArgumentException if there are no parameters or if the
     *                                  combinationsSize is too small
     */
    public static List<int[]> computeCartesianProduct(Int2IntMap parameters, int combinationSize) {
        Preconditions.notNull(parameters, PARAMETERS_NOT_NULL);
        Preconditions.check(!parameters.isEmpty(), AT_LEAST_ONE_PARAMETER);
        Preconditions.check(combinationSize >= parameters.size(), TOO_MANY_PARAMETERS);
        Preconditions.check(combinationSize > parameters.keySet().stream()
                .mapToInt(parameter -> parameter).max().orElse(0), TOO_HIGH_PARAMETERS);
        
        List<int[]> combinations = new ArrayList<>();
        int[] currentIndex = new int[parameters.size()];
        
        int[] keys = parameters.keySet().toIntArray();
        Arrays.sort(keys);
        
        do {
            int[] currentCombination = new int[combinationSize];
            Arrays.fill(currentCombination, CombinationUtil.NO_VALUE);
            
            for (int i = 0; i < keys.length; i++) {
                int index = keys[i];
                int value = currentIndex[i];
                
                currentCombination[index] = value;
            }
            
            combinations.add(currentCombination);
        } while (tryIncreaseByOne(currentIndex, keys, parameters));
        
        return combinations;
    }
    
    private static boolean tryIncreaseByOne(int[] currentIndex, int[] keys, Int2IntMap parameters) {
        for (int i = 0; i < currentIndex.length; i++) {
            currentIndex[i]++;
            if (currentIndex[i] < parameters.get(keys[i])) {
                return true;
            } else {
                currentIndex[i] = 0;
            }
        }
        
        return false;
    }
    
    /**
     * Computes all subsets of parameter indices with the given size.
     * For example if the parameters 1, 2, 3, and 4 are given and the
     * specified size is 2, the parameters subsets (1, 2), (1, 3),
     * (1, 4), (2, 3), (2, 4), (3, 4) are returned.
     *
     * @param parameters the set of parameters for which all subsets shall be generated.
     *     Must not be {@code null}
     * @param size the size of the returned subsets. Must not be negative or
     *     greater than the number of parameters
     * @return all subsets of parameters of the given size. If the size is zero, an
     *     empty list is returned
     * @throws NullPointerException if parameters are {@code null}
     * @throws IllegalArgumentException if the size is negative or too large
     */
    public static List<IntSet> computeParameterCombinations(int[] parameters, int size) {
        Preconditions.notNull(parameters, PARAMETERS_NOT_NULL);
        Preconditions.check(size >= 0, SIZE_NOT_NEGATIVE);
        
        return computeParameterCombinationsRecursively(parameters, size);
    }
    
    private static List<IntSet> computeParameterCombinationsRecursively(int[] parameters, int k) {
        if (k == 0 || parameters.length == 0 || parameters.length < k) {
            return Collections.emptyList();
        } else if (k == 1) {
            List<IntSet> combinations = new ArrayList<>(parameters.length);
            
            for (int parameter : parameters) {
                IntSet set = new IntOpenHashSet(1);
                set.add(parameter);
                
                combinations.add(set);
            }
            
            return combinations;
        } else if (parameters.length == k) {
            List<IntSet> combinations = new ArrayList<>(1);
            combinations.add(new IntOpenHashSet(parameters));
            
            return combinations;
        } else {
            int[] tail = Arrays.copyOfRange(parameters, 1, parameters.length);
            List<IntSet> tailSubsets = computeParameterCombinationsRecursively(tail, k - 1);
            
            for (IntSet set : tailSubsets) {
                set.add(parameters[0]);
            }
            
            List<IntSet> subsets = computeParameterCombinationsRecursively(tail, k);
            
            List<IntSet> combinations = new ArrayList<>(tailSubsets.size() + subsets.size());
            combinations.addAll(tailSubsets);
            combinations.addAll(subsets);
            
            return combinations;
        }
    }

    /**
     * Computes (a,b)-wise subsets of parameter indices
     * <p>
     * For example with a unary error-constraint,
     * f( [0, 1, 2, 3], [ 0 ], 1, 0) == { (0) }
     * f( [0, 1, 2, 3], [ 0 ], 1, 1) == { (0, 1), (0, 2), (0, 3) }
     * f( [0, 1, 2, 3], [ 0 ], 1, 2) == { (0, 1, 2), (0, 1, 3), (0, 2, 3) }
     * f( [0, 1, 2, 3], [ 0 ], 1, 3) == { (0, 1, 2, 3) }
     * f( [0, 1, 2, 3], [ 0 ], 1, 4) == { (0, 1, 2, 3) }
     * f( [0, 1, 2, 3], [ 0 ], 1, 5) == { (0, 1, 2, 3) }
     *<p>
     * Another example with a binary error-constraint,
     * f( [0, 1, 2, 3], [ 2, 3 ], 1, 0) == { (2), (3) }
     * f( [0, 1, 2, 3], [ 2, 3 ], 2, 0) == { (2, 3) }
     * f( [0, 1, 2, 3], [ 2, 3 ], 1, 1) == { (2, 0), (2, 1), (3, 0), (3, 1) }
     * f( [0, 1, 2, 3], [ 2, 3 ], 2, 1) == { (2, 3, 0), (2, 3, 1) }
     * f( [0, 1, 2, 3], [ 2, 3 ], 1, 2) == { (2, 0, 1), (3, 0, 1) }
     * f( [0, 1, 2, 3], [ 2, 3 ], 2, 2) == { (2, 3, 0, 1) }
     *
     * @param parameters the set of parameters for which all subsets shall be
     *                   generated. Must not be {@code null}
     * @param negativeParameters the set of parameters that is involved in the
     *                           error-constraint. Must not be {@code null}
     * @param strengthA the interaction strength for negativeParameters
     * @param strengthB the interaction stregth for the subset of not-negativeParameters
     *
     * @return all subsets of parameters of size strengthA + strengthB
     * @throws NullPointerException if parameters are {@code null}
     * @throws IllegalArgumentException if parameter sizes are illegal
     */
    public static List<IntSet> computeNegativeParameterCombinations(int[] parameters, int[] negativeParameters, int strengthA, int strengthB) {
        Preconditions.notNull(parameters);
        Preconditions.notNull(negativeParameters);
        Preconditions.check(ArrayUtil.containsAll(parameters, negativeParameters));
        Preconditions.check(strengthA > 0 && strengthA <= negativeParameters.length);
        Preconditions.check(strengthB >= 0);

        final List<IntSet> errorTupleInteractions = computeParameterCombinations(negativeParameters, strengthA);

        if(strengthB == 0) {
            return errorTupleInteractions;
        } else {
            final int[] otherParameters = ArrayUtil.exclude(parameters, negativeParameters);
            final List<IntSet> remainingInteractions = computeParameterCombinations(otherParameters, Math.min(strengthB, otherParameters.length));

            return unionElementWise(errorTupleInteractions, remainingInteractions);
        }
    }

    private static List<IntSet> unionElementWise(List<IntSet> first, List<IntSet> second) {
        Preconditions.notNull(first);
        Preconditions.notNull(second);

        if(first.isEmpty()) {
            return second;
        } else if(second.isEmpty()) {
            return first;
        }

        final List<IntSet> allUnions = new ArrayList<>(first.size() * second.size());

        for (final IntSet firstElement : first) {
            for (final IntSet secondElement : second) {
                final IntSet union = new IntArraySet(firstElement);
                union.addAll(secondElement);

                allUnions.add(union);
            }
        }

        return allUnions;
    }

    /**
     * Computes all possible combinations as partial test cases which only contain the given parameters.
     *
     * <p>For example, given an input of [2, 3, 4, 5] and {0, 2}, all combinations between the first and third
     * parameters are computed:
     * [0, -1, 0, -1], [0, -1, 1, -1], [0, -1, 2, -1], [0, -1, 3, -1],
     * [1, -1, 0, -1], [1, -1, 1, -1], [1, -1, 2, -1], [1, -1, 3, -1]
     *
     * @param parameterSizes the sizes of all parameters. This actually only needs to contain the sizes of the
     *     parameters which are referenced in the set of given parameters. Must not be {@code null} or empty
     * @param parameters the parameters of which to build the cartesian product of combinations. Must not be
     *     {@code null} or empty and must only contain valid indices of {@code parameterSizes}
     * @return the cartesian product of the referenced parameter values with the other values left empty
     */
    public static Set<int[]> computeCombinations(int[] parameterSizes, IntSet parameters) {
        Preconditions.notNull(parameterSizes, "parameter sizes required");
        Preconditions.notNull(parameters, "parameters required");
        Preconditions.check(parameterSizes.length > 0, "At least one parameter required");
        Preconditions.check(!parameters.isEmpty(), "At least one parameter required");
        Preconditions.check(parameters.stream()
                .allMatch(parameter -> parameter >= 0 && parameter < parameterSizes.length),
                "invalid parameter in " + parameters + " for sizes " + Arrays.toString(parameterSizes));
        
        return computeCombinationsRecursively(parameterSizes, parameters.toIntArray(),
                CombinationUtil.emptyCombination(parameterSizes.length), 0);
    }
    
    private static Set<int[]> computeCombinationsRecursively(int[] parameterSizes, int[] parameterIndices,
            int[] currentCombination, int currentIndex) {
        
        final boolean isLastIteration = currentIndex == parameterIndices.length - 1;
        final int parameter = parameterIndices[currentIndex];
        final int parameterSize = parameterSizes[parameter];
        final Set<int[]> combinations = new HashSet<>();
        
        for (int value = 0; value < parameterSize; value++) {
            final int[] currentCombinationWithValue = Arrays.copyOf(currentCombination, currentCombination.length);
            currentCombinationWithValue[parameter] = value;
            
            if (isLastIteration) {
                combinations.add(currentCombinationWithValue);
            } else {
                combinations.addAll(computeCombinationsRecursively(parameterSizes, parameterIndices,
                        currentCombinationWithValue, currentIndex + 1));
            }
        }
        
        return combinations;
    }
    
    /**
     * Computes all size-value-combinations there are with the given parameters.
     * For example, for the given parameters [2, 2, 2] (three parameters with 2 values each) and size 2, the
     * following combinations are returned:
     * [0, 0, -1]
     * [0, 1, -1]
     * [1, 0, -1]
     * [1, 1, -1]
     * [0, -1, 0]
     * [0, -1, 1]
     * [1, -1, 0]
     * [1, -1, 1]
     * [-1, 0, 0]
     * [-1, 0, 1]
     * [-1, 1, 0]
     * [-1, 1, 1]
     *
     * @param parameters all parameters. They are defined as their number of values. So [2, 3] means the first parameter
     *                   has two values, and the second one has three. Must not be {@code null}
     * @param size       the size of sub-combinations of values in the parameters that are calculated
     * @return all sub-combinations of the values with the given size as demonstrated above. The order of combinations
     * is not defined and may change in subsequent implementations. In any combinations the values for parameters
     * are ordered the same way as the parameters supplied to the method
     */
    public static Set<int[]> computeCombinations(int[] parameters, int size) {
        Preconditions.notNull(parameters);
        Preconditions.check(size >= 0);
        
        final List<IntSet> parameterCombinations = computeParameterCombinationsRecursively(IntStream.range(0, parameters.length).toArray(), size);
        final Set<int[]> combinations = new HashSet<>();
        for (IntSet parameterCombination : parameterCombinations) {
            final Int2IntMap parameterSizes = new Int2IntOpenHashMap(parameterCombination.size());
            for (int parameter : parameterCombination) {
                parameterSizes.put(parameter, parameters[parameter]);
            }
            
            combinations.addAll(computeCartesianProduct(parameterSizes, parameters.length));
        }
        
        return combinations;
    }
    
    /**
     * Computes all sub-combinations with the given size that the combination has. The combinations is allowed to have
     * values not set. For example, [-1, 2, 3, 1, -1, 3] called with 2 would return
     * [-1, 2, 3, -1, -1, -1]
     * [-1, 2, -1, 1, -1, -1]
     * [-1, 2, -1, -1, -1, 3]
     * [-1, -1, 3, 1, -1, -1]
     * [-1, -1, 3, -1, -1, 3]
     * [-1, -1, -1, 1, -1, 3]
     *
     * @param combination a combination. Must not be {@code null}
     * @param size        the size of sub-combinations. Must be positive
     * @return all sub-combinations with the given size of the combinations. No order is guaranteed. The parameters
     * are in the same order as with the given combination
     */
    public static List<int[]> computeSubCombinations(int[] combination, int size) {
        Preconditions.notNull(combination);
        Preconditions.check(size >= 0);
        
        return computeSubCombinationsRecursively(combination, emptyCombination(combination.length), 0, size);
    }
    
    private static List<int[]> computeSubCombinationsRecursively(int[] combination, int[] currentSubCombination, int index, int size) {
        final int currentSize = CombinationUtil.numberOfSetParameters(currentSubCombination);
        if (currentSize == size) {
            return Collections.singletonList(Arrays.copyOf(currentSubCombination, currentSubCombination.length));
        }
        if (index == combination.length || size > combination.length - index + 1 + currentSize) {
            return Collections.emptyList();
        }
        
        final List<int[]> result = new ArrayList<>(computeSubCombinationsRecursively(combination, currentSubCombination, index + 1, size));
        
        if (combination[index] != NO_VALUE) {
            currentSubCombination[index] = combination[index];
            result.addAll(computeSubCombinationsRecursively(combination, currentSubCombination, index + 1, size));
            currentSubCombination[index] = NO_VALUE;
        }
        
        return result;
    }
    
    /**
     * Computes all sub-combinations of this combination. This returns the same result as calling
     * {@link #computeCombinations(int[], int)} with sizes from 1 to combinations.length.
     *
     * @param combination a combination. Must not be {@code null} but can have unset values
     * @return all sub-combinations (including the combination itself)
     */
    public static List<int[]> computeSubCombinations(int[] combination) {
        Preconditions.notNull(combination);
        
        return computeSubCombinationsRecursively(combination, emptyCombination(combination.length), 0);
    }
    
    private static List<int[]> computeSubCombinationsRecursively(int[] combination, int[] currentSubCombination, int index) {
        if (index == combination.length) {
            if (CombinationUtil.numberOfSetParameters(currentSubCombination) > 0) {
                return Collections.singletonList(Arrays.copyOf(currentSubCombination, currentSubCombination.length));
            } else {
                return Collections.emptyList();
            }
        }
        
        final List<int[]> result = new ArrayList<>(computeSubCombinationsRecursively(combination, currentSubCombination, index + 1));
        
        if (combination[index] != NO_VALUE) {
            currentSubCombination[index] = combination[index];
            result.addAll(computeSubCombinationsRecursively(combination, currentSubCombination, index + 1));
            currentSubCombination[index] = NO_VALUE;
        }
        
        return result;
    }
}
