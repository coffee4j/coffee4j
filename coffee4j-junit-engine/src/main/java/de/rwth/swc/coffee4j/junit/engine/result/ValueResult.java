package de.rwth.swc.coffee4j.junit.engine.result;

import java.util.Objects;

/**
 * Class used to wrap results of the normal control-flow that are within a valid range.
 */
public class ValueResult implements ExecutionResult {
    private final Object result;

    /**
     * @param result result to wrap.
     */
    public ValueResult(Object result) { this.result = result; }

    /**
     * @param o compared result.
     * @return true iff the stored values are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValueResult)) return false;

        ValueResult that = (ValueResult) o;

        return result.equals(that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }
}
