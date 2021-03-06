package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog;

import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveSeed;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.util.ArrayUtil;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil.NO_VALUE;
import static de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil.containsAllParameters;

/**
 * An implementation of the popular IPOG algorithm. For given configuration it generates a test suite so that for each
 * t-value-combination there is a test input containing it. This means IPOG create a t-way-testing suite.
 * Some improvements from "An Efficient Design and Implementation of the In-Parameter-Order Algorithm" were used.
 *
 * <p>The algorithm was extended to offer support for constraints, dynamic parameter orders, and variable strength
 * testing. To introduce parameter orders, the strategy pattern is used with {@link ParameterOrder}, and the same is
 * done for variable strength testing via {@link ParameterCombinationFactory}.
 */
public class IpogAlgorithm {
    
    private final TestModel model;
    private final ParameterOrder order = new MixedStrengthParameterOrder();
    private final ParameterCombinationFactory combinationFactory = new MixedStrengthParameterCombinationFactory();
    
    /**
     * Creates a new algorithm for the given configuration. After this, the {@link IpogAlgorithm#generate()} method can be used
     * to generate the test suite satisfying the configuration.
     *
     * @param testModel test model that should be generated by IPOG. Must not be {@code null}
     * @throws NullPointerException if configuration is {@code null}
     */
    public IpogAlgorithm(TestModel testModel) {
        model = Preconditions.notNull(testModel);
    }
    
    public List<int[]> generate() {
        final int strength = model.getDefaultTestingStrength();

        final Int2IntMap parameters = convertToFactors();
        final int[] initialParameters = order.getInitialParameters(model);

        final List<int[]> testSuite = buildInitialTestSuite(parameters, initialParameters);
        addSeedsToInitialTestSuite(testSuite, model.getSeeds());

        final int[] remainingParameters = order.getRemainingParameters(model);

        if(strength > 0 || model.getMixedStrengthGroups().size() > 1) {
            extendInitialTestSuite(parameters, initialParameters, testSuite, remainingParameters);
        }

        fillEmptyValues(testSuite, parameters);
        
        return testSuite;
    }
    
    private Int2IntMap convertToFactors() {
        Int2IntMap parameters = new Int2IntOpenHashMap(model.getNumberOfParameters());
        for (int i = 0; i < model.getNumberOfParameters(); i++) {
            parameters.put(i, model.getParameterSize(i));
        }
        return parameters;
    }
    
    private List<int[]> buildInitialTestSuite(Int2IntMap allParameters, int[] initialParameters) {
        if (initialParameters.length == 0) {
            return List.of();
        } else {
            final List<int[]> testSuite = Combinator.computeCartesianProduct(
                    subMap(allParameters, initialParameters), allParameters.size());
           
            return testSuite.stream()
                    .filter(model.getConstraintChecker()::isValid)
                    .collect(Collectors.toList());
        }
    }
    
    private Int2IntMap subMap(Int2IntMap original, int[] keys) {
        Int2IntMap subMap = new Int2IntOpenHashMap();
        
        for (int i = 0; i < original.size(); i++) {
            
            if (ArrayUtil.contains(keys, i)) {
                subMap.put(i, original.get(i));
            }
        }
        
        return subMap;
    }
    
    private void addSeedsToInitialTestSuite(List<int[]> testSuite, List<PrimitiveSeed> seeds) {
        for (PrimitiveSeed seed : seeds) {
            final int[] combination = seed.getCombination();
            
            if (!tryToAdd(testSuite, combination) && model.getConstraintChecker().isValid(combination)) {
                testSuite.add(combination);
            }
        }
    }
    
    private boolean tryToAdd(List<int[]> testSuite, int[] toBeAdded) {
        for (int[] testCase : testSuite) {
            if (CombinationUtil.canBeAdded(testCase, toBeAdded, model.getConstraintChecker())) {
                CombinationUtil.add(testCase, toBeAdded);
                return true;
            }
        }
        
        return false;
    }

    private void extendInitialTestSuite(Int2IntMap parameters, int[] initialParameters, List<int[]> testSuite,
            int[] remainingParameters) {
        
        final IntList coveredParameters = new IntArrayList(initialParameters);

        for (int nextParameter : remainingParameters) {
            final List<IntSet> parameterCombinations = combinationFactory.create(
                    coveredParameters.toIntArray(), nextParameter, model);
            
            if (!parameterCombinations.isEmpty()) {
                final CoverageMap coverageMap =
                        horizontalExtension(nextParameter, testSuite, parameters, parameterCombinations);
    
                if (coverageMap.mayHaveUncoveredCombinations()) {
                    verticalExtension(nextParameter, parameters, testSuite, coverageMap);
                }
            }

            coveredParameters.add(nextParameter);
        }
    }
    
    private CoverageMap horizontalExtension(int nextParameter, List<int[]> testSuite, Int2IntMap allParameters,
            List<IntSet> parameterCombinations) {
        
        final CoverageMap coverageMap = new EfficientCoverageMap(parameterCombinations, nextParameter, allParameters,
                model.getConstraintChecker());
        
        if (!model.getSeeds().isEmpty()) {
            for (int[] testInput : testSuite) {
                coverageMap.markAsCovered(testInput);
            }
        }
        
        for (int[] testInput : testSuite) {
            addValueWithHighestCoverageGain(coverageMap, testInput, nextParameter);
            coverageMap.markAsCovered(testInput);

            if (!coverageMap.mayHaveUncoveredCombinations()) {
                break;
            }
        }
        
        return coverageMap;
    }
    
    private void addValueWithHighestCoverageGain(CoverageMap coverageMap,
                                                 int[] partialTestInput,
                                                 int parameterIndex) {
        if(skipAlreadyParameterValues(partialTestInput, parameterIndex)) {
            return;
        }

        int[] gains = coverageMap.computeGainsOfFixedParameter(partialTestInput);
        
        for (int i = 0; i < gains.length; i++) {
            int valueWithHighestGain = getValueWithHighestGain(gains);
            
            partialTestInput[parameterIndex] = valueWithHighestGain;
            
            if (model.getConstraintChecker().isValid(partialTestInput)) {
                return;
            } else {
                partialTestInput[parameterIndex] = -1;
                gains[valueWithHighestGain] = -1;
            }
        }

        throw new IllegalStateException("ERROR: test input "
                + Arrays.toString(partialTestInput)
                + " cannot be updated for parameter " + parameterIndex);
    }

    private boolean skipAlreadyParameterValues(int[] partialTestInput, int parameterIndex) {
        return partialTestInput[parameterIndex] != NO_VALUE;
    }

    private int getValueWithHighestGain(int[] gains) {
        int valueWithHighestGain = 0;
        for (int value = 0; value < gains.length; value++) {
            if (gains[value] > gains[valueWithHighestGain]) {
                valueWithHighestGain = value;
            }
        }
        return valueWithHighestGain;
    }
    
    private void verticalExtension(int index,
                                   Int2IntMap parameters,
                                   List<int[]> testSuite,
                                   CoverageMap coverageMap) {
        final CombinationPartitioner combinationPartitioner = new CombinationPartitioner(
                getIncompleteCombinations(index, testSuite), index, parameters.get(index));

        while (coverageMap.mayHaveUncoveredCombinations()) {
            final Optional<int[]> uncoveredCombination = coverageMap.getUncoveredCombination();

            if(uncoveredCombination.isPresent()) {
                int[] combination = uncoveredCombination.get();

                Optional<int[]> candidate = addCombinationToTestInput(combination, combinationPartitioner, testSuite);

                if (candidate.isPresent()) {
                    int[] extension = candidate.get();

                    coverageMap.markAsCovered(extension);

                    if (containsAllParameters(extension, index)) {
                        combinationPartitioner.removeCombination(extension);
                    }
                } else {
                    coverageMap.markAsCovered(combination);
                }
            }
        }
    }
    
    private List<int[]> getIncompleteCombinations(int index, List<int[]> testSuite) {
        List<int[]> incompleteCombinations = new LinkedList<>();
        for (int[] testInput : testSuite) {
            if (!containsAllParameters(testInput, index)) {
                incompleteCombinations.add(testInput);
            }
        }
        return incompleteCombinations;
    }
    
    private Optional<int[]> addCombinationToTestInput(int[] combination,
                                                      CombinationPartitioner combinationPartitioner,
                                                      List<int[]> testSuite) {
        if (!model.getConstraintChecker().isValid(combination)) {
            return Optional.empty();
        }
        
        final Optional<int[]> testInput = combinationPartitioner
                .extendSuitableCombination(combination, model.getConstraintChecker());
        
        if (testInput.isPresent()) {
            return testInput;
        } else {
            testSuite.add(combination);
            combinationPartitioner.addCombination(combination);
            
            return Optional.of(combination);
        }
    }
    
    private void fillEmptyValues(List<int[]> testSuite, Int2IntMap parameters) {
        for (int[] testInput : testSuite) {
            for (int parameter = 0; parameter < parameters.size(); parameter++) {
                if (testInput[parameter] == NO_VALUE) {
                    fillEmptyValue(testInput, parameter, parameters.get(parameter));
                }
            }
        }
    }
    
    private void fillEmptyValue(int[] testInput, int parameter, int parameterSize) {
        for (int value = 0; value < parameterSize; value++) {
            if (model.getConstraintChecker().isExtensionValid(testInput, parameter, value)) {
                testInput[parameter] = value;
                return;
            }
        }
        
        // If you reach this branch, there's a programming error somewhere else"
        throw new IllegalStateException("ERROR: could not replace random value for parameter " + parameter + " in test input: " + Arrays.toString(testInput));
    }
}
