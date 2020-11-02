package de.rwth.swc.coffee4j.junit.engine.result;

import java.util.concurrent.Callable;

/**
 * Interface used to wrap execution results for identification of exception-inducing combinations.
 */
public interface ResultWrapper {
    /**
     * Executes a function and wraps the result into an {@link ExecutionResult}.
     * @param callable the test function to execute.
     * @return returns the wrapped result in form of an {@link ExecutionResult}.
     */
    ExecutionResult runTestFunction(Callable<?> callable);
}
