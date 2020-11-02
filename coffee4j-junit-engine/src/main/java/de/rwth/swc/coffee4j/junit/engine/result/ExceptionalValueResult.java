package de.rwth.swc.coffee4j.junit.engine.result;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;

import java.util.Objects;

/**
 * Class used to wrap results of the normal control-flow that are not within a valid range.
 */
public class ExceptionalValueResult implements ExecutionResult {
    private final Object exceptionalValue;
    private final boolean isConstraintGenerationEnabled;

    /**
     * @param exceptionalValue value to wrap.
     * @param isConstraintGenerationEnabled flag that indicates whether constraint-generation is enabled.
     *                                      If yes, {@link #equals(Object)} delivers ternary result, otherwise a binary
     *                                      result.
     */
    public ExceptionalValueResult(Object exceptionalValue, boolean isConstraintGenerationEnabled) {
        this.exceptionalValue = exceptionalValue;
        this.isConstraintGenerationEnabled = isConstraintGenerationEnabled;
    }

    /**
     * @param o result to compare.
     * @return if the two results cause the same exceptional value, it returns true when testing and an
     * {@link ErrorConstraintException} when generating constraints. It returns false if the two results are not equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExceptionalValueResult)) return false;

        ExceptionalValueResult that = (ExceptionalValueResult) o;

        if (exceptionalValue.equals(that.exceptionalValue)) {
            if (isConstraintGenerationEnabled) {
                throw new ErrorConstraintException(new ExceptionalValueException(exceptionalValue.toString()));
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(exceptionalValue, isConstraintGenerationEnabled);
    }

    /**
     * If an {@link ErrorConstraintException} is thrown, it wraps an {@link Exception}. Since this wrapped result does not
     * wrap an exception, this class is created as default value.
     */
    public static class ExceptionalValueException extends Exception {
        public ExceptionalValueException(String exceptionalValue) {
            super(exceptionalValue);
        }
    }
}
