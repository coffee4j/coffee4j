package de.rwth.swc.coffee4j.algorithmic.interleaving.manager;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;

import java.util.Optional;

/**
 * Interface that each {@link InterleavingCombinatorialTestManager} for interleaving CT must implement.
 */
public interface InterleavingCombinatorialTestManager {
    /**
     * @param testInput previously executed test input. Null if called for the first time.
     * @param result result of previously executed test input. Null if called for the first time.
     *
     * @return next test input needed for the current phase.
     *
     * Empty Optional, if
     * <ul>
     *     <li>all t-tuples covered,</li>
     *     <li>or possibly exception/failure-inducing combination is identified,</li>
     *     <li>or feedback-checking finished.</li>
     * </ul>
     */
    Optional<int[]> generateNextTestInput(int[] testInput, TestResult result);

    /**
     * initialize identification phase for a failing test input.
     * @param testInput failing test input to find exception/failure-inducing combination for.
     * @param result {@link TestResult} of the failing test case containing information needed to decide whether it is
     *              failure- or exception-inducing
     * @return first test input needed for identification.
     */
    Optional<int[]> initializeIdentification(int[] testInput, TestResult result);

    /**
     * reinitialize identification phase if feedback-checking phase was not successful.
     * @return first test input needed for identification.
     */
    Optional<int[]> reinitializeIdentification();

    /**
     * initialize feedback-checking phase.
     * @return first test input needed for identification.
     */
    Optional<int[]> initializeFeedbackChecking();

    /**
     * updates the coverage map for a given (passing) test input
     * @param combination to update coverage map for
     */
    void updateCoverage(int[] combination);

    /**
     * @return true iff currently checked combination is most likely exception/failure-inducing
     */
    boolean combinationIdentified();
}
