package de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg;

import de.rwth.swc.coffee4j.algorithmic.constraint.DynamicHardConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
public class AetgSatAlgorithm {

    private final AetgSatConfiguration configuration;
    private final TestModel model;
    private final CoverageMap coverageMap;
    private final DynamicHardConstraintChecker checker;
    private final IntList parameterIndices;
    private final int totalValues;
    private final Random random = ThreadLocalRandom.current();

    /**
     * Constructor.
     *
     * @param configuration the configuration. It may not be {@code null}.
     */
    public AetgSatAlgorithm(AetgSatConfiguration configuration) {
        Preconditions.notNull(configuration, "configuration required");
        Preconditions.check(configuration.getModel().getConstraintChecker() instanceof DynamicHardConstraintChecker,
                "Can only use DynamicHardConstraintChecker with AETG");
        
        this.configuration = Preconditions.notNull(configuration);
        this.model = configuration.getModel();
        this.checker = (DynamicHardConstraintChecker) model.getConstraintChecker();
        this.coverageMap = new CoverageMap(model.getParameterSizes(), model.getDefaultTestingStrength(), checker);
        this.parameterIndices = new IntArrayList();

        for (int i = 0; i < model.getNumberOfParameters(); i++) {
            parameterIndices.add(i);
        }

        totalValues = Arrays.stream(model.getParameterSizes())
                .reduce(Integer::sum)
                .orElse(0);
    }

    private Optional<int[]> getTestCaseWithFixedValues(int[] fixedValues, Set<ParameterValuePair> forbidden) {

        int[] testCase = Arrays.copyOf(fixedValues, fixedValues.length);
        boolean sat = false;
        Set<ParameterValuePair> forbiddenPairs = new HashSet<>(forbidden);
        ParameterValuePair first = null;
        IntSet forbiddenParameters = new IntArraySet(fixedValues.length);
        for (int i = 0; i < fixedValues.length; i++) {
            if (fixedValues[i] != -1) {
                forbiddenParameters.add(i);
            }
        }
        while (!sat) {
            if (forbidden.size() >= totalValues) {
                return Optional.empty();
            }
            first = selectFirstFactorValue(forbiddenPairs, forbiddenParameters);
            sat = checkTestCase(testCase, first);
            if (!sat) {
                forbiddenPairs.add(first);
            }
        }
        testCase[first.getParameter()] = first.getValue();

        Collections.shuffle(parameterIndices, random);
        for (int parameter : parameterIndices) {
            if (testCase[parameter] == CombinationUtil.NO_VALUE) {
                sat = false;
                int tries = 0;
                int maxTries = configuration.getMaximumNumberOfTries();
                IntSet forbiddenValues = new IntArraySet();
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
                    }
                }
                if (!sat) {
                    return Optional.empty();
                }
                testCase[best.get().getParameter()] = best.get().getValue();
            }
        }
        return Optional.of(testCase);

    }

    /**
     * Gets a mutated test case for localizing fixed parameters.
     *
     * @param parameter     the parameter to mutate
     * @param testCase      the test case to mutate
     * @param lastMutations the already done mutations.
     * @return a single test case, or an empty optional if no test case could be found
     */
    public Optional<int[]> getMutatedTestCase(int parameter, int[] testCase, List<int[]> lastMutations) {
        int[] result = Arrays.copyOf(testCase, testCase.length);
        boolean sat = false;
        int tries = 0;
        IntSet forbiddenValues = new IntArraySet();
        forbiddenValues.add(testCase[parameter]);
        lastMutations.forEach(m -> forbiddenValues.add(m[parameter]));
        Optional<ParameterValuePair> best = Optional.empty();
        while (!sat && tries < configuration.getMaximumNumberOfTries()) {
            tries++;
            best = selectBestValue(parameter, forbiddenValues, testCase);
            if (best.isPresent()) {
                sat = checkTestCase(testCase, best.get());
            } else {
                sat = false;
                break;
            }
            if (!sat) {
                forbiddenValues.add(best.get().getValue());
            }
        }
        if (!sat) {
            return Optional.empty();
        } else {
            result[best.get().getParameter()] = best.get().getValue();
            return Optional.of(result);
        }

    }

    /**
     * Generate a singe test case.
     *
     * @return a single test case, or an empty optional if no test case could be found
     */
    public Optional<int[]> getNextTestCase() {
        if (!coverageMap.hasUncoveredCombinations()) {
            return Optional.empty();
        }
        final List<int[]> candidates = new ArrayList<>();
        for (int candidateCount = 0; candidateCount < configuration.getNumberOfCandidates(); candidateCount++) {
            Optional<int[]> testCase = getTestCaseWithFixedValues(CombinationUtil.emptyCombination(model.getNumberOfParameters()), new HashSet<>());
            testCase.ifPresent(candidates::add);

        }

        if (!candidates.isEmpty()) {
            return Optional.of(Collections.max(candidates, Comparator.comparing(coverageMap::getNumberOfUncoveredCombinations)));
        } else {
            return Optional.empty();
        }

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

    private boolean checkTestCase(int[] testCase, ParameterValuePair pv) {
        if (checker.getInvolvedParameters().contains(pv.getParameter())) {
            int[] candidate = Arrays.copyOf(testCase, testCase.length);
            candidate[pv.getParameter()] = pv.getValue();
            return checker.isValid(candidate);
        } else {
            return true;
        }
    }

    private ParameterValuePair selectFirstFactorValue(Set<ParameterValuePair> forbiddenPairs, IntSet forbiddenParameters) {
        return coverageMap.getMostCommonValue(forbiddenPairs, forbiddenParameters);
    }

    /**
     * Generate a complete covering array.
     *
     * @return a list of test cases that cover all t-way combinations
     */
    public List<int[]> generate() {
        List<int[]> result = new ArrayList<>();
        Optional<int[]> nextTestCase = getNextTestCase();
        while (nextTestCase.isPresent()) {
            updateCoverage(nextTestCase.get());
            result.add(nextTestCase.get());
            nextTestCase = getNextTestCase();
        }
        return result;
    }

    /**
     * Mark a test case as covered.
     *
     * @param testCase the test case that was covered
     */
    public void updateCoverage(int[] testCase) {
        coverageMap.updateSubCombinationCoverage(testCase);
    }

    /**
     * Add a forbidden combination to the constraints.
     *
     * @param combination the combination to add
     */
    public void addForbiddenCombination(int[] combination) {
        coverageMap.addForbiddenCombination(combination);
    }

    /**
     * Select a combination that covers the most uncovered combinations and is the most dissimilar to a given other
     * combination. This is used for feedback checking.
     * <p>
     * Because this method is stateful, a list of the last feedback rounds need to be supplied.
     *
     * @param failureInducingCombination the combination that should be feedback checked
     * @param failure                    the failure the FIC belongs to
     * @param lastFeedback               the last rounds of feedback checking
     * @return a dissimilar test case that covers the most uncovered combinations
     */
    public int[] selectDissimilar(int[] failureInducingCombination, int[] failure, List<int[]> lastFeedback) {
        int[] result = Arrays.copyOf(failureInducingCombination, failureInducingCombination.length);
        for (int parameter = 0; parameter < failureInducingCombination.length; parameter++) {
            if (failureInducingCombination[parameter] == CombinationUtil.NO_VALUE) {
                IntSet forbiddenValues = new IntArraySet();
                forbiddenValues.add(failure[parameter]);
                for (int[] combination : lastFeedback) {
                    forbiddenValues.add(combination[parameter]);
                }
                Optional<ParameterValuePair> pv = selectBestValue(parameter, forbiddenValues, result);
                if (pv.isPresent()) {
                    result[parameter] = pv.get().getValue();
                } else {
                    result[parameter] = ThreadLocalRandom.current().nextInt(0, model.getParameterSize(parameter));
                }
            }
        }
        return result;
    }

}
