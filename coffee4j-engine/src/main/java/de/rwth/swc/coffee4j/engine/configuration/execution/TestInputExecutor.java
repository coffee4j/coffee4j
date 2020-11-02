package de.rwth.swc.coffee4j.engine.configuration.execution;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;

/**
 * Executor for one {@link Combination}.
 */
@FunctionalInterface
public interface TestInputExecutor {
    
    /**
     * Executes the given {@link Combination} and throws an error if necessary.
     *
     * @param combination the combination for which the test is executed
     * @return the result of executing the given combination
     */
    TestResult execute(Combination combination);
    
}
