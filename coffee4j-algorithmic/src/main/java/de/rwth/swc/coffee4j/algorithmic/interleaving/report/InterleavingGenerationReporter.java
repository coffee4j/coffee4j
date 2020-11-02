package de.rwth.swc.coffee4j.algorithmic.interleaving.report;

import de.rwth.swc.coffee4j.algorithmic.interleaving.InterleavingCombinatorialTestGroup;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingCombinatorialTestConfiguration;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;
import de.rwth.swc.coffee4j.algorithmic.sequential.report.GenerationReporter;

import java.util.Map;
import java.util.Set;

/**
 * An interface defining a reporter listening for events during the generation of combinatorial test inputs.
 * Can be used together with the {@link InterleavingCombinatorialTestConfiguration}.
 * All methods are implemented as empty default methods to that any implementing class must only overwrite methods
 * it needs.
 *
 * <p>
 * Interleaving version of {@link GenerationReporter}
 * </p>
 */
public interface InterleavingGenerationReporter extends Reporter {
    /**
     * called if a {@link InterleavingCombinatorialTestGroup} has been generated and the Interleaving Combinatorial
     * Testing started.
     *
     * @param group group which was generated
     */
    default void interleavingGroupGenerated(InterleavingCombinatorialTestGroup group){}

    /**
     * called if Interleaving Combinatorial Testing finished for a {@link InterleavingCombinatorialTestGroup}.
     *
     * @param group group which has been finished
     * @param exceptionInducingCombinations all error-combinations that were found together with their type of
     *     exception.
     * @param possibleFailureInducingCombinations all possibly failure inducing combinations that were found.
     */
    default void interleavingGroupFinished(InterleavingCombinatorialTestGroup group,
                                  Map<int[], Class<? extends Throwable>> exceptionInducingCombinations,
                                  Set<int[]> possibleFailureInducingCombinations){}

    /**
     * called if the identification phase started for a failing test input.
     *
     * @param group group for which the identification phase started.
     * @param failingTestInput test input for which the identification was started.
     */
    default void identificationStarted(InterleavingCombinatorialTestGroup group, int[] failingTestInput){}

    /**
     * called if the identification phase ended for a failing test input.
     *
     * @param group group for which the identification phase was started.
     * @param exceptionInducingCombinations all error-combinations that were found during the identification phase.
     * @param failureInducingCombinations all possibly failure-inducing combinations that were found during the
     *                                    identification phase.
     */
    default void identificationFinished(InterleavingCombinatorialTestGroup group, Set<int[]> exceptionInducingCombinations, Set<int[]> failureInducingCombinations){}

    /**
     * called if a new test input was generated during identification phase.
     *
     * @param group group for which the identification phase is started.
     * @param testInput test inputs that was generated.
     */
    default void identificationTestInputGenerated(InterleavingCombinatorialTestGroup group, int[] testInput){}

    /**
     * called if the feedback-checking-phase started.
     *
     * @param group group for which the feedback-checking-phase is started.
     * @param failureInducingCombination the possibly failure-inducing combination that is checked.
     */
    default void checkingStarted(InterleavingCombinatorialTestGroup group, int[] failureInducingCombination){}

    /**
     * called if the feedback-checking-phase finished.
     *
     * @param group group for which the feedback-checking-phase has benn started.
     * @param failureInducingCombination the possibly failure-inducing combination that has been checked.
     * @param isFailureInducing true iff the checked combination is failure-inducing.
     */
    default void checkingFinished(InterleavingCombinatorialTestGroup group, int[] failureInducingCombination, boolean isFailureInducing){}
}
