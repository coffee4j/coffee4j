package de.rwth.swc.coffee4j.algorithmic.sequential.characterization.mixtgte;


import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.CombinationType;
import de.rwth.swc.coffee4j.algorithmic.interleaving.util.OptimalValue;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * An Implementation of the MixTgTe algorithm described in "Efficient and Guaranteed Detection of t-way Failure-Inducing
 * Combinations" by Arcaini, Gargantini and Radavelli.
 * It is an interleaving Fault Characterization Algorithm that collects tuples that seem to cause failures and isolates
 * them.
 * <p>
 * It starts from identifying combinations of size t = 1 until the given strength is reached.
 * It guarantees to detect and isolate all minimal FICs with a size up to the given strength.
 */
public class Mixtgte implements FaultCharacterizationAlgorithm {
    
    private final TestModel testModel;
    private final ConstraintChecker checker;
    // currently used strength (and size of used tuples)
    private int currentStrength = 1;
    // target strength of testing (maximal size of FICs that can be detected)
    private final int expectedStrength;
    // store inputs of all passing and failing tests
    private final Set<int[]> passingTestInputs = new HashSet<>();
    private final Map<IntList, CombinationType> failingAndExceptionalPassingTestInputs = new HashMap<>();

    // all possible tuples of size currentStrength
    private Set<int[]> tupleSet = null;
    private final List<int[]> unknownTuples = new ArrayList<>();
    private final Set<int[]> passingTuples = new HashSet<>();
    private final Map<IntList, CombinationType> failingAndExceptionalPassingTuples = new HashMap<>();
    final Map<IntList, CombinationType> isolatedMinInducingCombinations = new HashMap<>();

    private final List<Set<BitSet>> bitmasks;

    private final IntList parameterSizes;
    private final IntList parameters;
    private final int numberOfParameters;
    private final Set<IntList> executedInputs = new HashSet<>();
    private static final long MAX_NUMBER_OF_ITERATIONS = 30000;

    /**
     * Creates a new MixTgTe algorithm for a given testModel.
     *
     * @param testModel for the Fault Characterization Algorithm
     */
    public Mixtgte(TestModel testModel) {
        Preconditions.notNull(testModel);

        this.testModel = testModel;
        expectedStrength = testModel.getDefaultTestingStrength();
        checker = testModel.getConstraintChecker();

        bitmasks = new ArrayList<>(testModel.getDefaultTestingStrength());

        parameterSizes = new IntArrayList(testModel.getParameterSizes());
        numberOfParameters = testModel.getNumberOfParameters();
        parameters = new IntArrayList(numberOfParameters);
        IntStream.range(0, numberOfParameters).forEach(parameters::add);

        computeBitmasks();
    }

    private void computeBitmasks() {
        Set<BitSet> leaves = new HashSet<>(numberOfParameters);

        for (int index = 0; index < numberOfParameters; index++) {
            BitSet leaf = new BitSet(numberOfParameters);
            leaf.set(index);
            leaves.add(leaf);
        }

        bitmasks.add(leaves);

        for (int currentSize = 0; currentSize < testModel.getDefaultTestingStrength() - 1; currentSize++) {
            Set<BitSet> nextSet = new HashSet<>();

            for (BitSet mask : bitmasks.get(currentSize)) {
                for (int index = mask.length(); index < numberOfParameters; index++) {
                    BitSet parent = (BitSet) mask.clone();
                    parent.set(index);
                    nextSet.add(parent);
                }
            }

            bitmasks.add(nextSet);
        }
    }

    /**
     * Creates a new MixTgTe algorithm for a given configuration.
     *
     * @param config for the Fault Characterization Algorithm
     */
    public Mixtgte(FaultCharacterizationConfiguration config) {
        this(config.getModel());
    }

    /**
     * @return a factory always returning new instances of the MixTgTe algorithm
     */
    public static FaultCharacterizationAlgorithmFactory mixtgte() {
        return Mixtgte::new;
    }

    /**
     * Implements a variant of Algorithm 2 (MixTgTe_t, due to framework restrictions) of the referenced paper.
     * Returns a non-empty list (one test case) to further refine the sets of unknown, failing and passing tuples
     * as well as the set of minimal FICs ({@link #unknownTuples}, {@link #failingAndExceptionalPassingTuples}, {@link #passingTuples},
     * {@link #isolatedMinInducingCombinations}). As soon as an empty list is returned, the computation of the
     * set of minimal FICs with size less or equal to the strength of {@link #testModel} is completed and
     * {@link #computeFailureInducingCombinations()} can be called.
     *
     * Reordering of algorithm lines due to framework restrictions.
     *
     * @param testResults the results of the initial test suite or previous test
     *                    inputs generated by this method. Must not be
     *                    {@code null}. Empty for the first iteration of the algorithm.
     * @return list of further test inputs which need to be executed to refine the list of suspicious combinations.
     * In MixTgTe, this list contains only one element.
     */
    @Override
    public List<int[]> computeNextTestInputs(Map<int[], TestResult> testResults) {
        int[] nextExpectedTestInput = null;

        // Save and separate all executed test inputs corresponding to their result.
        // PassingTestInputs and FailingTestInputs together build the test suite.

        // Algorithm 2 line 6
        Optional<Map.Entry<int[], TestResult>> testResult = testResults.entrySet().stream().findFirst();
        Map.Entry<int[], TestResult> testRes = null;
        if (testResult.isPresent()) {
            testRes = testResult.get();

            int[] testInput = testRes.getKey();
            TestResult result = testRes.getValue();

            executedInputs.add(new IntArrayList(testInput));

            if (result.isSuccessful()) {
                passingTestInputs.add(testInput);
            } else {
                Optional<Throwable> optCause = result.getResultValue();
                optCause.ifPresent(throwable -> failingAndExceptionalPassingTestInputs.put(new IntArrayList(testInput), throwable instanceof ErrorConstraintException ? CombinationType.EXCEPTION_INDUCING : CombinationType.FAILURE_INDUCING));
            }
        }

        while (nextExpectedTestInput == null) {
            if (currentStrength <= expectedStrength) {
                // Algorithm 2 lines 1 - 3
                if (tupleSet == null) {
                    computeAndClassifyTuplesOfGivenSize();
                }

                // lines 7 and 8 are executed before line 5 as due to the framework, an executed test input
                // could be given to the algorithm before the first iteration is executed (e.g. IPOG used to generate
                // an initial test suite, although in interleaving FCAs not necessary)
                // -> test result would not be considered

                // Algorithm 2 lines 7 and 8
                if (testResult.isPresent()) {
                    updateTupleSets(testRes.getKey(), testRes.getValue());
                    updateMinInducingCombinations();
                }

                if (!(unknownTuples.isEmpty() && (failingAndExceptionalPassingTuples.isEmpty() || inducingTuplesExplained()))) {
                    // Algorithm 2 line 5
                    nextExpectedTestInput = buildTest();

                    /*
                     * if no new test input can be derived, this means that there are unresolved tuples
                     * due to constraints or the maximum number of iterations is reached for all failing tuples.
                     * No new tests can be derived to classify them as MFIC or as passing tuple
                     */
                    if (nextExpectedTestInput == null) {
                        currentStrength++;
                        tupleSet = null;
                    }

                } else {
                    // Algorithm 2 completed for currentStrength -> continue with currentStrength + 1
                    currentStrength++;
                    tupleSet = null;
                }
            } else {
                // return empty list if desired testing strength is reached
                return Collections.emptyList();
            }
        }

        return Collections.singletonList(nextExpectedTestInput);
    }

    /**
     * Computes all tuples of size {@link #currentStrength}. Afterwards, the tuples are classified as failing, passing
     * or unknown.
     */
    private void computeAndClassifyTuplesOfGivenSize() {
        tupleSet = generateTupleSetOfGivenSize(testModel.getParameterSizes(), currentStrength);
        boolean isPassing;

        // compute passing and failing tuples
        // Algorithm 2 lines 1 and 2
        for (int[] tuple : tupleSet) {
            isPassing = false;
            for (int[] input : passingTestInputs) {
                if (CombinationUtil.contains(input, tuple)) {
                    passingTuples.add(tuple);
                    isPassing = true;
                    break;
                }
            }

            if (isPassing) {
                continue;
            }

            for (Map.Entry<IntList, CombinationType> entry : failingAndExceptionalPassingTestInputs.entrySet()) {
                if (CombinationUtil.contains(entry.getKey().toIntArray(), tuple)) {
                    failingAndExceptionalPassingTuples.put(new IntArrayList(tuple), entry.getValue());
                    break;
                }
            }
        }

        passingTuples.forEach(tuple -> failingAndExceptionalPassingTuples.remove(new IntArrayList(tuple)));

        // Algorithm 2 line 3
        unknownTuples.addAll(tupleSet);
        unknownTuples.removeAll(passingTuples);
        unknownTuples.removeAll(failingAndExceptionalPassingTuples.keySet().stream().map(IntList::toIntArray).collect(Collectors.toList()));
    }

    /**
     * @return list of all minimal FICs of size less or equal to strength of {@link #testModel}
     */
    @Override
    public List<int[]> computeFailureInducingCombinations() {
        return isolatedMinInducingCombinations
                .keySet()
                .stream()
                .map(IntCollection::toIntArray)
                .collect(Collectors.toList());
    }

    /**
     * @param parameters all parameters of the {@link #testModel}. They define the number of values.
     * @param size       size of sub-combinations that are computed
     * @return all sub-combinations of the values with the given size not containing any error combination.
     */
    // Computes all possible tuples of size t
    private Set<int[]> generateTupleSetOfGivenSize(int[] parameters, int size) {
        Set<int[]> subCombinations = Combinator.computeCombinations(parameters, size);
        subCombinations.removeAll(subCombinations.stream().filter(combination -> !checker.isValid(combination)).collect(Collectors.toList()));

        return subCombinations;
    }

    /**
     * @return a new test case according to Algorithm 3. If {@link #unknownTuples} is non-empty, as many unknown tuples
     * as possible are merged together to generate a new test case. Parameters with no associated value are set by an
     * SMT solver afterwards. In the paper, the remaining parameters were set randomly. As every newly set parameter must
     * be checked by the ConstraintChecker, it is more efficient to let solver search for a solution once.
     *
     * If {@link #unknownTuples} is empty, an unexplained failing tuple t from {@link #failingAndExceptionalPassingTuples} is chosen.
     * A SMT solver is used to find a test case (see {@link }) that contains t but is
     * different from all tests in {@link #failingAndExceptionalPassingTestInputs} and {@link #passingTestInputs}.
     */
    private int[] buildTest() {
        int[] newTest = null;

        // Algorithm 3 lines 2 - 12
        if (!unknownTuples.isEmpty()) {
            Collections.shuffle(unknownTuples);
            // start with an empty test case
            newTest = CombinationUtil.emptyCombination(testModel.getNumberOfParameters());
            // add as many unknown tuples to generate a new test case
            for (int[] tuple : unknownTuples) {
                // Algorithm 3 lines 4 and 5
                if (CombinationUtil.canBeAdded(newTest, tuple, checker)) {
                    CombinationUtil.add(newTest, tuple);

                    // Algorithm 3 lines 7 and 8
                    // complete test case is found -> return the new test case
                    if (CombinationUtil.containsAllParameters(newTest, newTest.length - 1)) {
                        return newTest;
                    }
                }
            }

            newTest = generateTestInputForTuple(newTest);

        } else {
            List<IntList> failingCombinations = new ArrayList<>(failingAndExceptionalPassingTuples.keySet());
            failingCombinations.removeAll(failingCombinations.stream().filter(combination -> isExplained(combination.toIntArray())).collect(Collectors.toList()));
            Collections.shuffle(failingCombinations);

            List<IntList> toRemove = new ArrayList<>();
            // Algorithm 3 lines 14 and 15
            for (IntList tuple : failingCombinations) {
                newTest = generateTestInputForTuple(tuple.toIntArray());

                if (newTest != null) {
                    toRemove.forEach(failingAndExceptionalPassingTuples::remove);
                    return newTest;
                } else {
                    toRemove.add(tuple);
                }
            }
        }

        return newTest;
    }

    private int[] generateTestInputForTuple(int[] newTest) {
        int[] nextTestInput = null;

        long tries = 0;
        long iterations = 1;

        for (int i = 0; i < newTest.length; i++) {
            if (newTest[i] == -1) {
                iterations *= parameterSizes.getInt(i);
            }
        }

        iterations = Long.min(iterations, MAX_NUMBER_OF_ITERATIONS);

        while (nextTestInput == null && tries < iterations) {
            tries++;
            nextTestInput = Arrays.copyOf(newTest, numberOfParameters);

            Collections.shuffle(parameters);

            for (int parameter : parameters) {
                if (nextTestInput[parameter] == -1) {
                    Optional<ParameterValuePair> optimalValue = OptimalValue.valueForParameter(
                            parameter,
                            testModel.getParameterSize(parameter),
                            nextTestInput,
                            executedInputs,
                            checker);

                    // select valid value that is most dissimilar for current partial test input and previously
                    // generated inputs
                    if (optimalValue.isPresent()) {
                        nextTestInput[parameter] = optimalValue.get().getValue();
                    } else {
                        executedInputs.add(new IntArrayList(nextTestInput));
                        nextTestInput = null;
                        break;
                    }
                }
            }
        }

        return nextTestInput;
    }


    /**
     * Algorithm 4
     * Depending on the result of the last executed test case, the sets {@link #unknownTuples}, {@link #failingAndExceptionalPassingTuples},
     * {@link #passingTuples} and {@link #isolatedMinInducingCombinations} are updated.
     *
     * @param testCase last executed test case
     * @param result   result of the last executed test case
     */
    private void updateTupleSets(int[] testCase, TestResult result) {
        Set<int[]> toMove = new HashSet<>();

        // collect all unknown tuples that are contained in the last executed test case
        for (int[] tuple : unknownTuples) {
            if (CombinationUtil.contains(testCase, tuple)) {
                toMove.add(tuple);
            }
        }

        // update sets according to the result of the test case
        if (result.isUnsuccessful() || result.isExceptionalSuccessful()) {
            // Algorithm 4 line 2
            unknownTuples.removeAll(toMove);
            toMove.forEach(tuple -> failingAndExceptionalPassingTuples.put(new IntArrayList(tuple),
                    result.getResultValue().orElse(null) instanceof ErrorConstraintException ?
                            CombinationType.EXCEPTION_INDUCING : CombinationType.FAILURE_INDUCING));
        } else {
            // Algorithm 4 lines 4 - 6
            unknownTuples.removeAll(toMove);
            passingTuples.addAll(toMove);

            toMove = new HashSet<>();

            for (int[] tuple : failingAndExceptionalPassingTuples.keySet().stream().map(IntList::toIntArray).collect(Collectors.toList())) {
                if (CombinationUtil.contains(testCase, tuple)) {
                    toMove.add(tuple);
                }
            }

            toMove.forEach(fic -> failingAndExceptionalPassingTuples.remove(new IntArrayList(fic)));
            passingTuples.addAll(toMove);

            toMove = new HashSet<>();

            for (int[] tuple : isolatedMinInducingCombinations.keySet().stream().map(IntCollection::toIntArray).collect(Collectors.toList())) {
                if (CombinationUtil.contains(testCase, tuple)) {
                    toMove.add(tuple);
                }
            }

            toMove.forEach(combination -> isolatedMinInducingCombinations.remove(new IntArrayList(combination)));
            passingTuples.addAll(toMove);
        }
    }

    /**
     * Algorithm 5
     * After updating all tuple sets {@link #failingAndExceptionalPassingTuples}, {@link #passingTuples} and {@link #unknownTuples} in
     * {@link #updateTupleSets(int[], TestResult)}, it is checked whether new minimal inducing combinations can be isolated and added
     * to the set of {@link #isolatedMinInducingCombinations}.
     */
    private void updateMinInducingCombinations() {
        Set<int[]> toMove = new HashSet<>();
        for (int[] tuple : failingAndExceptionalPassingTuples.keySet().stream().map(IntList::toIntArray).collect(Collectors.toList())) {
            if (isIsoMinInducing(tuple)) {
                toMove.add(tuple);
            }
        }

        if (!toMove.isEmpty()) {
            toMove.forEach(fic -> isolatedMinInducingCombinations.put(new IntArrayList(fic), failingAndExceptionalPassingTuples.get(new IntArrayList(fic))));
            toMove.forEach(fic -> failingAndExceptionalPassingTuples.remove(new IntArrayList(fic)));
        }
    }

    /**
     * Definition 6
     *
     * @param tuple combination that is checked
     * @return true iff the given combination is failure-/exception-inducing
     */
    private boolean isInducing(int[] tuple) {
        // empty combination cannot be failure inducing
        if (CombinationUtil.numberOfSetParameters(tuple) == 0) {
            return false;
        }

        for (int[] input : passingTestInputs) {
            if (CombinationUtil.contains(input, tuple)) {
                failingAndExceptionalPassingTuples.remove(new IntArrayList(tuple));
                isolatedMinInducingCombinations.remove(new IntArrayList(tuple));
                return false;
            }
        }

        return true;
    }

    /**
     * Definition 7
     *
     * @param tuple combination that is checked
     * @return true iff the given combination is a minimal failure-/exception-inducing combination
     */
    private boolean isMinInducing(int[] tuple) {
        if (!isInducing(tuple)) {
            return false;
        }

        Set<BitSet> subMasks = new HashSet<>();

        for (int i = 0; i < CombinationUtil.numberOfSetParameters(tuple) - 1; i++) {
            subMasks.addAll(bitmasks.get(i));
        }

        for (BitSet subMask : subMasks) {
            if (isInducing(getSubCombination(tuple, subMask))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Definition 8
     *
     * @param tuple combination that is checked
     * @return true iff the given combination is a minimal failure-/exception-inducing combination and can be isolated by
     * {@link #failingAndExceptionalPassingTestInputs}
     */
    private boolean isIsoMinInducing(int[] tuple) {
        if (!isMinInducing(tuple)) {
            return false;
        }

        for (int[] testInput : failingAndExceptionalPassingTestInputs.keySet().stream().map(IntList::toIntArray).collect(Collectors.toList())) {
            if (!CombinationUtil.contains(testInput, tuple)) {
                continue;
            }

            if (isIsoMinInducing(tuple, testInput)) {
                failingAndExceptionalPassingTuples.put(new IntArrayList(tuple), failingAndExceptionalPassingTestInputs.get(new IntArrayList(testInput)));
                return true;
            }
        }

        return false;
    }

    /**
     * Definition 8
     *
     * @param tuple    combination that is checked
     * @param testCase used for isolation of the given combination
     * @return true iff the given combination is a minimal failure-/exception-inducing combination and can be isolated by
     * the given testCase
     */
    private boolean isIsoMinInducing(int[] tuple, int[] testCase) {
        Set<BitSet> subMasks = new HashSet<>();

        for (int i = 0; i < CombinationUtil.numberOfSetParameters(tuple); i++) {
            subMasks.addAll(bitmasks.get(i));
        }

        for (BitSet subMask : subMasks) {
            int[] subCombination = getSubCombination(testCase, subMask);
            if (isMinInducing(subCombination) && !Arrays.equals(subCombination, tuple)) {
                return false;
            }
        }

        return true;
    }

    private int[] getSubCombination(int[] tuple, BitSet submask) {
        int[] combination = new int[tuple.length];

        for (int index = 0; index < tuple.length; index++) {
            if (submask.get(index)) {
                combination[index] = tuple[index];
            } else {
                combination[index] = -1;
            }
        }

        return combination;
    }

    /**
     * @param tuple failing tuple to be checked
     * @return true iff tuple can be explained by currently existing {@link #isolatedMinInducingCombinations}
     */
    private boolean isExplained(int[] tuple) {
        for (int[] mfic : isolatedMinInducingCombinations.keySet().stream().map(IntList::toIntArray).collect(Collectors.toList())) {
            if (CombinationUtil.contains(tuple, mfic)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true iff {@link #isExplained(int[])} for all failing/exceptional tuples in {@link #failingAndExceptionalPassingTuples}
     */
    private boolean inducingTuplesExplained() {
        for (int[] tuple : failingAndExceptionalPassingTuples.keySet().stream().map(IntList::toIntArray).collect(Collectors.toList())) {
            if (!isExplained(tuple)) {
                return false;
            }
        }

        return true;
    }
}
