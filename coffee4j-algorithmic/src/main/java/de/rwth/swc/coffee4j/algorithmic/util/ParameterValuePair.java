package de.rwth.swc.coffee4j.algorithmic.util;

import java.util.Objects;

/**
 * Class for storing parameter-value pairs used by Interleaving CT.
 */
public class ParameterValuePair {
    
    private final int parameter;
    private final int value;

    /**
     * @param parameter parameter this class represents.
     * @param value value this class represents for the given parameter.
     */
    public ParameterValuePair(int parameter, int value) {
        this.parameter = parameter;
        this.value = value;
    }

    /**
     * Gets the parameter.
     *
     * @return the parameter
     */
    public int getParameter() {
        return parameter;
    }

    /**
     * Gets the value
     *
     * @return the value
     */
    public int getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameter, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ParameterValuePair) {
            return ((ParameterValuePair) obj).parameter == parameter && ((ParameterValuePair) obj).value == value;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "ParameterValuePair(" + parameter + ", " + value + ')';
    }
    
}