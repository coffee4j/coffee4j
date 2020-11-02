package de.rwth.swc.coffee4j.junit.engine.result;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;

import java.util.Objects;

/**
 * Class used to wrap {@link Exception}s.
 */
public class ExceptionResult implements ExecutionResult {
    private final Exception thrownException;
    private final boolean isConstraintGenerationEnabled;

    /**
     * @param thrownException {@link Exception} to wrap.
     * @param isConstraintGenerationEnabled flag that indicates whether constraint-generation is enabled.
     *                                      If yes, {@link #equals(Object)} delivers ternary result, otherwise a binary
     *                                      result.
     */
    public ExceptionResult(Exception thrownException, boolean isConstraintGenerationEnabled) {
        this.thrownException = thrownException;
        this.isConstraintGenerationEnabled = isConstraintGenerationEnabled;
    }

    /**
     * @param o result to compare.
     * @return if the two results cause the same {@link Exception}, it returns true when testing and an
     * {@link ErrorConstraintException} when generating constraints. It returns false if the two results are not equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExceptionResult)) return false;

        ExceptionResult that = (ExceptionResult) o;

        if (thrownException.getClass().equals(that.thrownException.getClass())) {
            if (isConstraintGenerationEnabled) {
                throw new ErrorConstraintException(thrownException);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(thrownException, isConstraintGenerationEnabled);
    }
}
