package de.rwth.swc.coffee4j.engine.generator.ipog;

import de.rwth.swc.coffee4j.engine.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.engine.util.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static de.rwth.swc.coffee4j.engine.util.CombinationUtil.NO_VALUE;
import static de.rwth.swc.coffee4j.engine.util.CombinationUtil.containsAllParameters;

/**
 * This acts as the coverage map described in section 4.1 of the paper
 * "An Efficient Design and Implementation of the In-Parameter-Order Algorithm".
 * Basically, it stores the tuples of each possible ParameterCombination as a
 * bitmap with a bijective function to map to and from an index in said bitmap.
 * <p>
 * This uses the index system described in {@link IpogAlgorithm}.
 */
class CoverageMap {
    
    private static final String PARAMETER_COMBINATIONS_NOT_NULL = "Parameter combinations must not be null";
    private static final String PARAMETER_NOT_VALID = "The parameter index must not be negative";
    private static final String FIXED_PARAMETER_NOT_CONTAINED = "The fixed parameter has to be contained in the " + "parameter map";
    private static final String COMBINATION_NOT_NULL = "Combination cannot be null";
    private static final String PARAMETERS_NOT_NULL = "Parameters cannot be null";
    
    private final int fixedParameter;
    private final int fixedParameterSize;
    
    private final Map<IntSet, ParameterCombinationCoverageMap> combinationCoverageMap = new HashMap<>();
    
    private final ConstraintChecker constraintChecker;
    
    /**
     * Initializes a new coverage map with the given parameter combinations
     * and the fixed parameter. This means that internally the fixed parameter
     * is added to each parameter combination.
     *
     * @param parameterCombinations the parameter combinations for which the
     *                              tuple coverage shall be tracked.
     *                              Must not be {@code null}
     * @param fixedParameter        the parameter added to all parameters.
     *                              Must not be negative
     * @param parameters            the sizes of all parameter. Must contains the sizes
     *                              of the parameters in all combinations and the fixed
     *                              parameter. Must not be {@code null}
     * @throws NullPointerException     if parameterCombinations or parameters
     *                                  is {@code null}
     * @throws IllegalArgumentException if one of the other constraints
     *                                  described for each method parameter
     *                                  is not met
     */
    CoverageMap(Collection<IntSet> parameterCombinations, int fixedParameter, Int2IntMap parameters, ConstraintChecker constraintChecker) {
        Preconditions.notNull(parameterCombinations, PARAMETER_COMBINATIONS_NOT_NULL);
        Preconditions.notNull(parameters, PARAMETERS_NOT_NULL);
        Preconditions.check(fixedParameter >= 0, PARAMETER_NOT_VALID);
        Preconditions.check(parameters.containsKey(fixedParameter), FIXED_PARAMETER_NOT_CONTAINED);
        Preconditions.notNull(constraintChecker);
        
        this.constraintChecker = constraintChecker;
        this.fixedParameter = fixedParameter;
        fixedParameterSize = parameters.get(fixedParameter);
        constructCombinationCoverageMap(parameterCombinations, fixedParameter, parameters);
    }
    
    private void constructCombinationCoverageMap(Collection<IntSet> parameterCombinations, int fixedParameter, Int2IntMap parameters) {
        if (parameterCombinations.isEmpty()) {
            parameterCombinations = Collections.singleton(new IntOpenHashSet(0));
        }
        
        for (IntSet parameterCombination : parameterCombinations) {
            combinationCoverageMap.put(parameterCombination, new ParameterCombinationCoverageMap(parameterCombination, fixedParameter, parameters, constraintChecker));
        }
    }
    
    /**
     * @return whether any combination is not covered
     */
    boolean hasUncoveredCombinations() {
        for (ParameterCombinationCoverageMap combinationCoverage : combinationCoverageMap.values()) {
            if (combinationCoverage.hasUncoveredCombinations()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Marks all sub-combinations which can be mapped to one of the given
     * parameter combinations given in the constructor as covered.
     *
     * @param combination the combination to mark as covered.
     *                    Must not be {@code null}
     * @throws NullPointerException if combination is {@code null}
     */
    void markAsCovered(int[] combination) {
        Preconditions.notNull(combination, COMBINATION_NOT_NULL);
        
        if (combination[fixedParameter] != NO_VALUE) {
            Set<ParameterCombinationCoverageMap> relevantCombinationCoverages = getRelevantCombinationCoverages(combination);
            for (ParameterCombinationCoverageMap combinationCoverage : relevantCombinationCoverages) {
                combinationCoverage.markAsCovered(combination);
            }
        }
    }
    
    private Set<ParameterCombinationCoverageMap> getRelevantCombinationCoverages(int[] combination) {
        Set<ParameterCombinationCoverageMap> relevantCombinationCoverages = new HashSet<>();
        for (Map.Entry<IntSet, ParameterCombinationCoverageMap> entry : combinationCoverageMap.entrySet()) {
            if (containsAllParameters(combination, entry.getKey())) {
                relevantCombinationCoverages.add(entry.getValue());
            }
        }
        
        return relevantCombinationCoverages;
    }
    
    /**
     * Computes the number of combinations which would be covered if the fixed
     * parameter given in the constructor would be set to a specific value in
     * the given combination.
     *
     * @param combination the base combination in which the gains of the values
     *                    for the fixed parameter shall be computed.
     *                    Must not be {@code null}
     * @return the number of combinations which would additionally be covered
     * if the fixed parameter was set to a certain value. The index
     * in the array corresponds to the value index in the parameter
     * <p>
     * the index is -1 if it refers to an invalid combination
     * Please note: only t-wise invalid combinations are identified!
     * The test input must be checked as well for k>t-wise invalid combinations
     * @throws NullPointerException if combination is {@code null}
     */
    int[] computeGainsOfFixedParameter(int[] combination) {
        Preconditions.notNull(combination, COMBINATION_NOT_NULL);
        
        int[] gains = new int[fixedParameterSize];
        Set<ParameterCombinationCoverageMap> relevantCombinations = getRelevantCombinationCoverages(combination);
        for (ParameterCombinationCoverageMap combinationCoverage : relevantCombinations) {
            combinationCoverage.addGainsOfFixedParameter(combination, gains);
        }
        
        return gains;
    }
    
    /**
     * Finds the next uncovered combination and returns it.
     *
     * @return the next uncovered combination in all parameter combination
     * coverage maps or an empty {@link Optional} if no combination is
     * uncovered
     */
    Optional<int[]> getUncoveredCombination() {
        for (ParameterCombinationCoverageMap combinationCoverage : combinationCoverageMap.values()) {
            if (combinationCoverage.hasUncoveredCombinations()) {
                return Optional.of(combinationCoverage.getUncoveredCombination());
            }
        }
        
        return Optional.empty();
    }
    
    private static final class ParameterCombinationCoverageMap {
        
        private final int numberOfCombinations;
        private final int numberOfParameters;
        
        private final int[] parameterCombination;
        private final int[] parameterSizes;
        private final int[] parameterMultipliers;
        
        private final BitSet coverageMap;
        
        private int numberOfCoveredCombinations;
        
        private final ConstraintChecker constraintChecker;
        
        private ParameterCombinationCoverageMap(IntSet parameterCombination, int fixedParameter, Int2IntMap parameters, ConstraintChecker constraintChecker) {
            this.parameterCombination = new int[parameterCombination.size() + 1];
            parameterCombination.toArray(this.parameterCombination);
            this.parameterCombination[parameterCombination.size()] = fixedParameter;
            parameterSizes = parameterSizesAsArray(parameters);
            parameterMultipliers = parameterMultipliersAsArray();
            
            numberOfCombinations = numberOfCombinations();
            numberOfCoveredCombinations = 0;
            numberOfParameters = parameters.size();
            
            coverageMap = new BitSet(numberOfCombinations);
            
            this.constraintChecker = constraintChecker;
        }
        
        private int[] parameterSizesAsArray(Int2IntMap parameters) {
            int[] parameterSizesAsArray = new int[parameterCombination.length];
            for (int i = 0; i < parameterCombination.length; i++) {
                parameterSizesAsArray[i] = parameters.get(parameterCombination[i]);
            }
            return parameterSizesAsArray;
        }
        
        private int[] parameterMultipliersAsArray() {
            int[] parameterMultipliersAsArray = new int[parameterSizes.length];
            int currentMultiplier = 1;
            for (int i = 0; i < parameterSizes.length; i++) {
                parameterMultipliersAsArray[i] = currentMultiplier;
                currentMultiplier *= parameterSizes[i];
            }
            return parameterMultipliersAsArray;
        }
        
        private int numberOfCombinations() {
            int count = 1;
            for (int parameterSize : parameterSizes) {
                count *= parameterSize;
            }
            return count;
        }
        
        private boolean hasUncoveredCombinations() {
            return numberOfCoveredCombinations < numberOfCombinations;
        }
        
        private void markAsCovered(int[] combination) {
            int index = getIndexUntil(combination, parameterCombination.length);
            if (!coverageMap.get(index)) {
                numberOfCoveredCombinations++;
            }
            coverageMap.set(index);
        }
        
        private void markIndexAsCovered(int index) {
            coverageMap.set(index);
        }
        
        private int getIndexUntil(int[] combination, int parameterCount) {
            int index = 0;
            for (int i = 0; i < parameterCount; i++) {
                int parameter = parameterCombination[i];
                index += combination[parameter] * parameterMultipliers[i];
            }
            return index;
        }
        
        private int[] getUncoveredCombination() {
            return getCombination(coverageMap.nextClearBit(0));
        }
        
        private int[] getCombination(int index) {
            int[] combination = new int[numberOfParameters];
            Arrays.fill(combination, NO_VALUE);
            for (int i = parameterCombination.length - 1; i >= 0; i--) {
                int parameter = parameterCombination[i];
                int parameterIndexPart = (index - (index % parameterMultipliers[i]));
                int value = parameterIndexPart / parameterMultipliers[i];
                combination[parameter] = value;
                index -= parameterIndexPart;
            }
            return combination;
        }
        
        private void addGainsOfFixedParameter(int[] combination, int[] gains) {
            if (!hasUncoveredCombinations()) {
                return;
            }
            
            int fixedParameterIndex = parameterCombination.length - 1;
            int baseIndex = getIndexUntil(combination, fixedParameterIndex);
            
            int[] subset = createSubsetOfCombination(combination, parameterCombination);
            
            for (int value = 0; value < gains.length; value++) {
                int index = baseIndex + value * parameterMultipliers[fixedParameterIndex];
                
                if (gains[value] != -1 && !coverageMap.get(index)) {
                    subset[fixedParameterIndex] = value;
                    
                    if (constraintChecker.isDualValid(parameterCombination, subset)) {
                        gains[value]++;
                    } else {
                        markIndexAsCovered(index);
                        gains[value] = -1;
                    }
                }
            }
        }
        
        private int[] createSubsetOfCombination(int[] combination, int[] parameters) {
            int[] subset = new int[parameters.length];
            
            for (int i = 0; i < subset.length; i++) {
                subset[i] = combination[parameters[i]];
            }
            
            return subset;
        }
    }
}
