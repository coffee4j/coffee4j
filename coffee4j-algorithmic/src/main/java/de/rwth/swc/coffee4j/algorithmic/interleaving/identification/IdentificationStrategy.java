package de.rwth.swc.coffee4j.algorithmic.interleaving.identification;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Map;
import java.util.Optional;

/**
 * Interface that each identification strategy used by interleaving CT must implement.
 */
public interface IdentificationStrategy {
    /**
     * @param failingTestInput test input to start the identification of exception/failure-inducing combinations for
     * @param result {@link TestResult} of the failing test case containing information needed to decide whether it is
     *               failure- or exception-inducing
     *
     * @return first test input used for identification of exception/failure-inducing combinations
     * (if present, otherwise empty Optional)
     */
    Optional<int[]> startIdentification(int[] failingTestInput, TestResult result);

    /**
     * restart the identification for a previously given failing test input if checking phase failed for given
     * exception/failure-inducing combinations of previous iteration
     *
     * @return first test input used for identification of exception/failure-inducing combinations
     * (if present, otherwise empty Optional)
     */
    Optional<int[]> restartIdentification();

    /**
     * @param testInput previously executed test input
     * @param testResult result of previously executed test input
     *
     * @return next test input used for identification of exception/failure-inducing combinations
     * (empty Optional, if exception/failure-inducing combinations identified)
     */
    Optional<int[]> generateNextTestInputForIdentification(int[] testInput, TestResult testResult);

    /**
     * @return identified combinations together with their type -- exception- or failure-inducing
     */
    Map<IntList, CombinationType> getIdentifiedCombinations();
}
