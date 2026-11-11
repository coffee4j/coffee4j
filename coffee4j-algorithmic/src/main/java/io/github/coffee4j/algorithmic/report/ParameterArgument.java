package io.github.coffee4j.algorithmic.report;

import io.github.coffee4j.algorithmic.util.Preconditions;

/**
 * Used to encapsulate a int as a parameter. This is used to distinguish normal ints and parameters
 * for eventual argument conversion using an {@link ArgumentConverter}. Therefore, all parameters
 * should be reported in a {@link Report} using this class.
 */
public record ParameterArgument(int parameter) {

    /**
     * Creates a new argument with the given parameter.
     *
     * @param parameter the parameter for the argument
     */
    public ParameterArgument {
        Preconditions.check(parameter >= 0);
    }

    @Deprecated
    public static ParameterArgument parameter(int parameter) {
        return new ParameterArgument(parameter);
    }

    @Override
    public String toString() {
        return Integer.toString(parameter);
    }

}
