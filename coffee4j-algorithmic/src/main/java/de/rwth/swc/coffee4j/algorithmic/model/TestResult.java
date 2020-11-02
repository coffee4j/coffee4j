package de.rwth.swc.coffee4j.algorithmic.model;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;

import java.util.Objects;
import java.util.Optional;

/**
 * Specifies the result of a test. Either a test threw no exception and was therefore successful, or it has an
 * exception as a cause for failure.
 */
public final class TestResult {

    private static final TestResult SUCCESSFUL_RESULT = new TestResult(null);
    private final Throwable resultValue;

    /**
     * Creates a new result for a successful test if the exception is {@code null}, a exceptional-successful test if
     * an {@link ErrorConstraintException} is present or a failed test otherwise.
     *
     * @param resultValue the exception that is thrown
     */
    private TestResult(Throwable resultValue) {
        this.resultValue = resultValue;
    }
    
    /**
     * Descriptive convenience method for constructing a result for a successful test input.
     *
     * @return a successful result
     */
    public static TestResult success() {
        return SUCCESSFUL_RESULT;
    }

    /**
     * Descriptive convenience method for constructing a result for a exceptional-successful test input.
     *
     * @param exception thrown {@link ErrorConstraintException}
     * @return an exceptional-successful result
     */
    public static TestResult exceptionalSuccess(Throwable exception) { return new TestResult(exception); }
    
    /**
     * Descriptive convenience method for constructing a result for a failed test input.
     *
     * @param causeForFailure the exception which caused the test to fail, or indicates that it has failed
     * @return a failed result
     */
    public static TestResult failure(Throwable causeForFailure) {
        return new TestResult(causeForFailure);
    }
    
    /**
     * @return whether the result indicates success (no exception given)
     */
    public boolean isSuccessful() {
        return resultValue == null;
    }

    /**
     * @return whether the result indicates an exceptional-success ({@link ErrorConstraintException} given)
     */
    public boolean isExceptionalSuccessful() {
        return resultValue instanceof ErrorConstraintException;
    }
    
    /**
     * @return whether the result indicates failure (exception given and not an {@link ErrorConstraintException})
     */
    public boolean isUnsuccessful() {
        return resultValue != null && !(resultValue instanceof ErrorConstraintException);
    }
    
    /**
     * @return an optional containing the exception which caused the failure or an empty optional if the test was
     * successful
     */
    public Optional<Throwable> getResultValue() {
        return Optional.ofNullable(resultValue);
    }
    
    /**
     * @return the required exception which caused the failure. Never {@code null}
     * @throws NullPointerException if there was no exception (e.g. if the test was successful)
     */
    public Throwable getRequiredResultValue() {
        if (resultValue == null) {
            throw new NullPointerException("resultValue must not be null");
        }
        
        return resultValue;
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final TestResult other = (TestResult) object;
        return Objects.equals(resultValue, other.resultValue);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(resultValue);
    }
    
    @Override
    public String toString() {
        if (resultValue == null) {
            return "TestResult{success}";
        } else if (resultValue instanceof ErrorConstraintException) {
            return "TestResult{exceptional-success}";
        } else {
            return "TestResult{failure, cause=" + resultValue + "}";
        }
    }
    
}
