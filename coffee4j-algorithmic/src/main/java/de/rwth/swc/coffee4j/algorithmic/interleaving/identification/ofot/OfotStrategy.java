package de.rwth.swc.coffee4j.algorithmic.interleaving.identification.ofot;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.CoverageMap;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.CombinationType;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.util.OptimalValue;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.*;

/**
 * OFOT-Identification-Strategy described in "The minimal failure-causing schema of combinatorial testing"
 *
 * When a failing test input is detected, it generates numberOfParameters new test inputs changing exactly one
 * parameter value per new test input. All changed parameters that make the corresponding test passing are part of
 * the (possibly) minimal failure inducing combination.
 *
 * Problems occur when a test input contains multiple faults.
 */
public class OfotStrategy implements IdentificationStrategy {
    
    private final CoverageMap coverageMap;
    private final ConstraintChecker checker;
    private final CompleteTestModel testModel;

    private IntList currentlyProcessedTestInput;
    private CombinationType errorType;
    // set of test inputs that need to be tested for current identification iteration
    private List<IntList> mutatedTestInputsToTest;
    // all test inputs that were executed for current failing test input
    private Map<int[], TestResult> executedMutatedTestInputs;

    // stores for each parameter the values that were used in previous mutations
    private List<IntSet> alreadyUsedValuesInMutations;

    private final int numberOfParameters;

    // identified failure-inducing combination: should contain at most one element
    private Map<IntList, CombinationType> possiblyInducingCombinations = new HashMap<>();

    private OfotStrategy(IdentificationConfiguration configuration) {
        this.coverageMap = configuration.getCoverageMap();
        this.checker = configuration.getConstraintChecker();
        this.testModel = configuration.getTestModel();

        numberOfParameters = testModel.getNumberOfParameters();
    }

    /**
     * @return Factory creating OFOT-Strategy
     */
    public static IdentificationStrategyFactory ofotStrategy() {
        return OfotStrategy::new;
    }

    @Override
    public Optional<int[]> startIdentification(int[] testInput, TestResult result) {
        Optional<Throwable> optCause = result.getResultValue();

        if (!optCause.isPresent()) {
            throw new Coffee4JException("Cause of Failure must be present!");
        }

        // reset
        this.currentlyProcessedTestInput = new IntArrayList(testInput);
        alreadyUsedValuesInMutations = new ArrayList<>();
        executedMutatedTestInputs = new HashMap<>();

        if (optCause.get() instanceof ErrorConstraintException) {
            errorType = CombinationType.EXCEPTION_INDUCING;
        } else {
            errorType = CombinationType.FAILURE_INDUCING;
        }

        // initialize set with values used in failing test input
        for (int parameter = 0; parameter < numberOfParameters; parameter++) {
            alreadyUsedValuesInMutations.add(new IntArraySet());
            alreadyUsedValuesInMutations.get(parameter).add(this.currentlyProcessedTestInput.getInt(parameter));
        }

        return restartIdentification();
    }

    private List<IntList> mutateFailingTestInput(IntList testInput) {
        List<IntList> mutatedTestInputs = new ArrayList<>(numberOfParameters);

        // generate a set of new test inputs mutating each parameter once
        for (int parameter = 0; parameter < numberOfParameters; parameter++) {
            Optional<IntList> possibleTestInput = computeMutatedTestInputForParameter(parameter, testInput, alreadyUsedValuesInMutations.get(parameter));

            // add the used value to ignore them in following iterations
            if (possibleTestInput.isPresent()) {
                mutatedTestInputs.add(possibleTestInput.get());
                alreadyUsedValuesInMutations.get(parameter).add(possibleTestInput.get().getInt(parameter));
            }
        }

        return mutatedTestInputs;
    }

    private Optional<IntList> computeMutatedTestInputForParameter(int parameter, IntList testInput, IntSet forbidden) {
        IntList nextTestInput = new IntArrayList(testInput);
        OptimalValue optimalValueFinder = new OptimalValue();

        // search for a value for the given parameter and partial test input to maximize number of covered t-tuples
        Optional<ParameterValuePair> optimalValue = optimalValueFinder.forParameter(parameter,
                testModel.getParameterSize(parameter),
                new IntArraySet(forbidden),
                nextTestInput.toIntArray(),
                coverageMap,
                checker);

        // no valid value for given parameter and partial test input available
        if (!optimalValue.isPresent()) {
            return Optional.empty();
        }

        nextTestInput.set(optimalValue.get().getParameter(), optimalValue.get().getValue());
        return Optional.of(nextTestInput);
    }

    @Override
    public Optional<int[]> restartIdentification() {
        // reset
        possiblyInducingCombinations = new HashMap<>();

        // compute test suite to identify possibly failure-/exception-inducing combination
        mutatedTestInputsToTest = mutateFailingTestInput(this.currentlyProcessedTestInput);

        if (mutatedTestInputsToTest.isEmpty()) {
            // all possible mutated test inputs were generated and executed: no fic/eic found for test input
            identifyPossiblyInducingCombinations();
            return Optional.empty();
        }

        return Optional.of(mutatedTestInputsToTest.remove(0).toIntArray());
    }

    @Override
    public Optional<int[]> generateNextTestInputForIdentification(int[] testInput, TestResult testResult) {
        executedMutatedTestInputs.put(testInput, testResult);

        // all inputs in generated test suite are executed: identify possibly failure-/exception-inducing combinations
        if (mutatedTestInputsToTest.isEmpty()) {
            identifyPossiblyInducingCombinations();
            return Optional.empty();
        } else {
            // return next input from generated test suite
            return Optional.of(mutatedTestInputsToTest.remove(0).toIntArray());
        }
    }

    private void identifyPossiblyInducingCombinations() {
        int[] possiblyInducingCombination = CombinationUtil.emptyCombination(numberOfParameters);

        // if test input is successful or triggers another exception, the parameter changed in this input is part
        // of the possibly failure-/exception-inducing combination
        for (Map.Entry<int[], TestResult> test : executedMutatedTestInputs.entrySet()) {
            if (!test.getValue().isSuccessful()) {
                Optional<Throwable> optionalThrowable = test.getValue().getResultValue();

                if (!optionalThrowable.isPresent() || !differentError(optionalThrowable.get())) {
                    continue;
                }
            }

            extractChangedParameter(possiblyInducingCombination, test.getKey());
        }

        possiblyInducingCombinations.put(new IntArrayList(possiblyInducingCombination), errorType);
    }

    private void extractChangedParameter(int[] possiblyInducingCombination, int[] testInput) {
        for (int parameter = 0; parameter < numberOfParameters; parameter++) {
            if (testInput[parameter] != currentlyProcessedTestInput.getInt(parameter)) {
                possiblyInducingCombination[parameter] = currentlyProcessedTestInput.getInt(parameter);
            }
        }
    }

    private boolean differentError(Throwable throwable) {
        if (errorType == CombinationType.FAILURE_INDUCING) {
            return throwable instanceof ErrorConstraintException;
        } else {
            return !(throwable instanceof ErrorConstraintException);
        }
    }

    @Override
    public String toString() {
        return "OfotStrategy";
    }

    @Override
    public Map<IntList, CombinationType> getIdentifiedCombinations() {
        return possiblyInducingCombinations;
    }
}
