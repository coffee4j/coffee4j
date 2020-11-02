package de.rwth.swc.coffee4j.engine.configuration.model;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a group of parameters which should be tested at a given higher strength than the rest of the parameters
 * in the model.
 *
 * <p>This concept is in general called mixed-strength combinatorial testing where there is a defined default
 * testing strength and a number of parameter groups which deviate from this default strength. Such a mechanism
 * is useful to model multiple subsystems in a combinatorial test without increasing the testing strength between
 * the subsystems unnecessarily. Additionally, parameters which are prone to appear in failure-causing combinations
 * can be tested at a higher strength to increase confidence.
 */
public class StrengthGroup {
    
    private final Set<Parameter> parameters;
    
    private final int strength;
    
    private StrengthGroup(Collection<Parameter> parameters, int strength) {
        Preconditions.notNull(parameters, "parameters required");
        Preconditions.check(strength >= 0 && strength <= parameters.size(),
                "Strength for parameters " + parameters + " must be int range [0," + parameters.size()
                + "] but was " + strength);
        
        this.parameters = new HashSet<>(parameters);
        this.strength = strength;
    }
    
    /**
     * Creates a new {@link Builder} for a {@link StrengthGroup} with the parameters corresponding to the
     * given names.
     *
     * @param parameterNames the unique names of the parameters. Must not be {@code null}
     * @return the builder instance for the given parameter names
     */
    public static Builder mixedStrengthGroup(String... parameterNames) {
        return mixedStrengthGroup(List.of(parameterNames));
    }
    
    /**
     * Creates a new {@link Builder} for a {@link StrengthGroup} with the parameters corresponding to the
     * given names.
     *
     * @param parameterNames the unique names of the parameters. Must not be {@code null}
     * @return the builder instance for the given parameter names
     */
    public static Builder mixedStrengthGroup(Collection<String> parameterNames) {
        return new Builder(parameterNames);
    }
    
    public Set<Parameter> getParameters() {
        return parameters;
    }
    
    public int getStrength() {
        return strength;
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final StrengthGroup other = (StrengthGroup) object;
        return strength == other.strength &&
                Objects.equals(parameters, other.parameters);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(parameters, strength);
    }
    
    @Override
    public String toString() {
        return "ParameterStrengthGroup{" +
                "parameters=" + parameters +
                ", strength=" + strength +
                '}';
    }
    
    /**
     * Class which realizes the builder pattern for constructing a new {@link StrengthGroup}.
     */
    public static final class Builder {
        
        private final List<String> parameterNames;
        
        private int strength;
        
        private Builder(Collection<String> parameterNames) {
            Preconditions.notNull(parameterNames);
            Preconditions.check(!parameterNames.isEmpty(),
                    "A mixed strength group must contain at least one parameter");
            
            this.parameterNames = new ArrayList<>(parameterNames);
            this.strength = parameterNames.size();
        }
    
        /**
         * Specifies that the group of parameters should be tested to the highest possible strength. This means that
         * the strength will be the number of parameters in the group (e.g. if there are three parameters, the strength
         * will also be three, so the cartesian product of the parameter will appear in the final test suite).
         *
         * @return this
         */
        public Builder ofHighestStrength() {
            this.strength = parameterNames.size();
            
            return this;
        }
    
        /**
         * Specifies an explicit strength to which the parameters in the group should be tested. This needs to be at
         * least zero and at most the number of parameters.
         *
         * @param strength the strength at which to test the parameter group. Must be at greater than or equal to zero
         *     and at most the number of parameters
         * @return this
         */
        public Builder ofStrength(int strength) {
            this.strength = strength;
            
            return this;
        }
    
        /**
         * Builds the {@link StrengthGroup} with the information given to the builder. It attempts to replace
         * each parameter name by the correct {@link Parameter} instance. If no instance matches the given name,
         * an exception is thrown.
         *
         * @param parameters the parameter for substitution. Must not be {@code null} and contain exactly one parameter
         *     with the given name for each parameter name given to the builder
         * @return the created group with the parameters corresponding to the given names and the given strength
         */
        public StrengthGroup build(Collection<Parameter> parameters) {
            final Set<Parameter> groupParameters = parameterNames.stream()
                    .map(parameterName -> parameters.stream()
                            .filter(parameter -> parameter.getName().equals(parameterName))
                            .findFirst()
                            .orElseThrow(() ->
                                    new Coffee4JException("Could not find parameter with name " + parameterName)))
                    .collect(Collectors.toSet());
            
            return new StrengthGroup(groupParameters, strength);
        }
        
    }
    
}
