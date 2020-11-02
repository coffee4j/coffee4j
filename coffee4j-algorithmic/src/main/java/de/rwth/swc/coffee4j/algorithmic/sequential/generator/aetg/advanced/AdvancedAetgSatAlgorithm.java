package de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg.advanced;

import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg.AetgSatConfiguration;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Implementation of the AETGSat variant of the AETG algorithm from "Constructing Interaction Test Suites for
 * Highly-Configurable Systems in the Presence of Constraints: A Greedy Approach".
 * <p>
 * The first step of generating a new test case is choosing a value for a single parameter. This value is chosen by
 * taking the parameter-value pair that is contained in the most remaining uncovered t -way combinations. Subsequently,
 * a random order of remaining parameters is chosen. These parameters are then iterated in this order, always greedily
 * choosing a value so that the largest amount of additional combinations are covered. When all parameters are set, all
 * contained combinations are marked as covered and the generation for the next test case can start. Once full coverage
 * is reached all generated test cases are aggregated to a covering array. Because the size of this array heavily
 * depends on the random parameter orders, this whole process is repeated for a fixed number times. In the end the
 * covering array with the smallest number of test cases is selected.
 * <p>
 * It also supports constraints by excluding invalid combinations from the covering array, as well as checking each test
 * case with a SAT checker before adding it to the covering array.
 */
public class AdvancedAetgSatAlgorithm {

    private final AetgSatConfiguration configuration;
    private final TestModel model;
    private final MixedStrengthCoverageMap coverageMap;
    private final SeedCoverageMap seedCoverageMap;
    private final IntList parameterIndices;
    private final int totalValues;
    private final Random random = ThreadLocalRandom.current();

    /**
     * Constructor.
     *
     * @param configuration the configuration. It may not be {@code null}.
     */
    public AdvancedAetgSatAlgorithm(AetgSatConfiguration configuration) {
        Preconditions.notNull(configuration, "configuration required");
        
        this.configuration = Preconditions.notNull(configuration);
        this.model = configuration.getModel();
        this.coverageMap = new MixedStrengthCoverageMap(model);
        this.seedCoverageMap = new SeedCoverageMap(model);
        this.parameterIndices = new IntArrayList();

        for (int i = 0; i < model.getNumberOfParameters(); i++) {
            parameterIndices.add(i);
        }

        totalValues = Arrays.stream(model.getParameterSizes())
                .reduce(Integer::sum)
                .orElse(0);
    }
    
    /**
     * Generate a complete covering array.
     *
     * @return a list of test cases that cover all t-way combinations
     */
    public List<int[]> generate() {
        final List<int[]> result = new ArrayList<>();
        
        Optional<int[]> nextTestCase = getNextTestCase();
        while (nextTestCase.isPresent()) {
            coverageMap.updateSubCombinationCoverage(nextTestCase.get());
            result.add(nextTestCase.get());
            nextTestCase = getNextTestCase();
        }
        
        return result;
    }
    
    /**
     * Generate a singe test case.
     *
     * @return a single test case, or an empty optional if no test case could be found
     */
    private Optional<int[]> getNextTestCase() {
        if (!coverageMap.hasUncoveredCombinations()) {
            return Optional.empty();
        }
        
        final int[] startTestCase = seedCoverageMap.getMostImportantPartialTestCase();
        return IntStream.range(0, configuration.getNumberOfCandidates())
                .mapToObj(index -> startTestCase)
                .map(this::getTestCaseWithFixedValues)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(Comparator.comparing(coverageMap::getNumberOfUncoveredCombinations));
    }

    private Optional<int[]> getTestCaseWithFixedValues(int[] fixedValues) {
        final int[] testCase = Arrays.copyOf(fixedValues, fixedValues.length);
        final boolean couldExtendTestCaseByFirstValue = extendTestCaseByFirstValue(testCase);
        
        if (!couldExtendTestCaseByFirstValue) {
            return Optional.empty();
        }

        Collections.shuffle(parameterIndices, random);
        for (int parameter : parameterIndices) {
            if (testCase[parameter] == CombinationUtil.NO_VALUE) {
                Optional<ParameterValuePair> best = selectBestSatisfiableValue(testCase, parameter);
                
                if (best.isPresent()) {
                    testCase[best.get().getParameter()] = best.get().getValue();
                } else {
                    return Optional.empty();
                }
            }
        }
        return Optional.of(testCase);
    }
    
    private boolean extendTestCaseByFirstValue(int[] testCase) {
        final Set<ParameterValuePair> forbiddenPairs = new HashSet<>();
        final IntSet forbiddenParameters = new IntArraySet(getFixedParameters(testCase));
        
        boolean sat = false;
        ParameterValuePair first = null;
    
        while (!sat) {
            if (forbiddenPairs.size() >= totalValues) {
                return false;
            }
            first = selectFirstFactorValue(forbiddenPairs, forbiddenParameters);
            sat = checkTestCase(testCase, first);
            if (!sat) {
                forbiddenPairs.add(first);
            }
        }
        
        testCase[first.getParameter()] = first.getValue();
        
        return true;
    }
    
    private IntSet getFixedParameters(int[] testCase) {
        final IntSet fixedParameters = new IntOpenHashSet(testCase.length);
    
        for (int i = 0; i < testCase.length; i++) {
            if (testCase[i] != CombinationUtil.NO_VALUE) {
                fixedParameters.add(i);
            }
        }
        
        return fixedParameters;
    }
    
    private ParameterValuePair selectFirstFactorValue(Set<ParameterValuePair> forbiddenPairs, IntSet forbiddenParameters) {
        return coverageMap.getMostCommonValue(forbiddenPairs, forbiddenParameters);
    }
    
    private Optional<ParameterValuePair> selectBestSatisfiableValue(int[] testCase, int parameter) {
        final int maxTries = configuration.getMaximumNumberOfTries();
        final IntSet forbiddenValues = new IntArraySet();
        
        boolean sat = false;
        int tries = 0;
        Optional<ParameterValuePair> best = Optional.empty();
    
        while (!sat && tries < maxTries) {
            best = selectBestValue(parameter, forbiddenValues, testCase);
            if (best.isPresent()) {
                sat = checkTestCase(testCase, best.get());
            } else {
                break;
            }
            if (!sat) {
                forbiddenValues.add(best.get().getValue());
                best = Optional.empty();
            }
        }
        
        return best;
    }

    private Optional<ParameterValuePair> selectBestValue(int parameter, IntSet forbiddenValues, int[] testCase) {
        int[] candidate = Arrays.copyOf(testCase, testCase.length);
        int bestValue = -1;
        long bestValueResult = -1;
        for (int value = 0; value < model.getParameterSize(parameter); value++) {
            if (!forbiddenValues.contains(value)) {
                candidate[parameter] = value;
                long valueResult = coverageMap.getNumberOfUncoveredCombinations(candidate);
                if (valueResult > bestValueResult) {
                    bestValueResult = valueResult;
                    bestValue = value;
                }
            }

        }
        if (bestValue == -1) {
            return Optional.empty();
        }
        return Optional.of(new ParameterValuePair(parameter, bestValue));
    }

    private boolean checkTestCase(int[] testCase, ParameterValuePair pair) {
        return model.getConstraintChecker()
                .isExtensionValid(testCase, pair.getParameter(), pair.getValue());
    }

}
