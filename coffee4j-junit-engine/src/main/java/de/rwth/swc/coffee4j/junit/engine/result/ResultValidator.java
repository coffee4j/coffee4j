package de.rwth.swc.coffee4j.junit.engine.result;

/**
 * Example validator that can be used to compare a given and an expected {@link ExecutionResult}.
 */
public class ResultValidator {
    /**
     * checks whether the given {@link ExecutionResult}s are equal.
     *
     * @param result the given result of the SUT.
     * @param expectedResult the expected result provided by a test oracle.
     */
    public void check(ExecutionResult result, ExecutionResult expectedResult) {
        if (!result.equals(expectedResult)) {
            throw new AssertionError();
        }
    }
}
