package de.rwth.swc.coffee4j.engine.configuration.extension.execution;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;

import java.util.Map;

/**
 * Represents a phase callback after the execution phase
 */
@FunctionalInterface
public interface AfterExecutionCallback extends Extension {

    /**
     * Executes the callback with the supplied preliminary test results.
     * The callback should only modify the {@link TestResult} elements in the map
     *
     * <p>This <strong>must</strong> always preserve the order of the given map.
     *
     * @param combinationTestResultMap the preliminary test results of the execution phase
     * @return the possibly alter test results
     */
    Map<Combination, TestResult> afterExecution(
            Map<Combination, TestResult> combinationTestResultMap);

}
