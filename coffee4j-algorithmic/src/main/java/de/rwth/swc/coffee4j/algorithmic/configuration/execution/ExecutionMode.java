package de.rwth.swc.coffee4j.algorithmic.configuration.execution;

import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;

/**
 * How the combinatorial test cases should be executed.
 *
 * <p>In particular, this configures when the initial (non-fault characterizing) test case execution should stop.
 */
public enum ExecutionMode {
    
    /**
     * An execution mode which only has the purpose to produce a failure as fast as possible.
     *
     * <p>This means that after the first test case fails in the initial testing phase only the fault characterization
     * is run to find the responsible failure-inducing combination. Consider this mode in regression testing in a
     * pipeline when the priority is to know that the system no longer works as fast as possible.
     *
     * <p>When using the fail fast, consider using a {@link TestInputPrioritizer} to make sure that the combinatorial
     * test fails as fast as possible. Also, make sure that the used {@link FaultCharacterizationAlgorithm} can handle
     * incomplete test results, i.e. the test results of a partial test suite where there may be t-way combinations not
     * covered by any test case.
     */
    FAIL_FAST,
    
    /**
     * An execution mode which has the purpose of finding all possible failures in a system.
     *
     * <p>This means that every test case in the initial testing phase will be executed regardless of the result. In
     * an extreme case, this mode executes all test cases even if every single one of them fails. Consider this mode
     * when executing a combinatorial test to find all failure modes. For example, you can use {@link #FAIL_FAST} in
     * the pipeline and then run the test in this mode locally to get further information if the given information from
     * the pipeline is not sufficient.
     *
     * <p>In this mode, a {@link TestInputPrioritizer} does not make much sense as every test case is executed
     * regardless of any failures.
     */
    EXECUTE_ALL
    
}
