package io.github.coffee4j.algorithmic.report;

import io.github.coffee4j.algorithmic.util.Preconditions;

/**
 * Used to encapsulate two ints as a value for a parameter. This is used to distinguish normal ints and values
 * for eventual argument conversion using an {@link ArgumentConverter}. Therefore, all values
 * should be reported in a {@link Report} using this class.
 */
public record ValueArgument(int parameter, int value) {

    /**
     * Creates a new argument for the value uniquely defined by both ints.
     *
     * @param parameter the parameter
     * @param value     a value of the given parameter
     */
    public ValueArgument {
        Preconditions.check(parameter >= 0);
        Preconditions.check(value >= 0);
    }

    @Deprecated
    public static ValueArgument value(int parameter, int value) {
        return new ValueArgument(parameter, value);
    }

    @Override
    public String toString() {
        return Integer.toString(parameter) + ':' + value;
    }

}
