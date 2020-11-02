package de.rwth.swc.coffee4j.junit.engine.annotation.parameter;

import de.rwth.swc.coffee4j.engine.configuration.model.Combination;

import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * Context of one {@link Parameter} being resolved for a combinatorial test method.
 *
 * <p>This context holds all information which should be needed by a {@link ParameterValueProvider}.
 */
public final class ParameterContext {
    
    private final Parameter parameter;
    private final Combination combination;
    
    private ParameterContext(Parameter parameter, Combination combination) {
        this.parameter = Objects.requireNonNull(parameter);
        this.combination = Objects.requireNonNull(combination);
    }
    
    public static ParameterContext of(Parameter parameter, Combination combination) {
        return new ParameterContext(parameter, combination);
    }
    
    public Parameter getParameter() {
        return parameter;
    }
    
    public Combination getCombination() {
        return combination;
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final ParameterContext other = (ParameterContext) object;
        return Objects.equals(parameter, other.parameter)
                && Objects.equals(combination, other.combination);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(parameter, combination);
    }
    
    @Override
    public String toString() {
        return "ParameterContext{" + "parameter=" + parameter + ", combination=" + combination + '}';
    }
    
}
