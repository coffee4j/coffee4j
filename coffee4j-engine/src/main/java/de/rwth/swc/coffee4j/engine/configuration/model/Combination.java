package de.rwth.swc.coffee4j.engine.configuration.model;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A encapsulation of a mapping from {@link Parameter} to {@link Value}. Can be used to represent an arbitrary
 * combination or a test input.
 */
public final class Combination {
    
    private static final Combination EMPTY_COMBINATION = new Combination(Map.of());
    
    private final Map<Parameter, Value> parameterValueMap;
    
    /**
     * Creates a new combination with the mappings.
     *
     * @param parameterValueMap the mapping. Must not be, nor contain as key or value {@code null}. All values
     *                          to which parameters are mapped must come out of the respective parameters
     */
    private Combination(Map<Parameter, Value> parameterValueMap) {
        Preconditions.notNull(parameterValueMap);
        assertValidValues(parameterValueMap);
        
        this.parameterValueMap = new HashMap<>(parameterValueMap);
    }
    
    public static Combination of(Map<Parameter, Value> parameterValueMap) {
        return new Combination(parameterValueMap);
    }
    
    public static Combination empty() {
        return EMPTY_COMBINATION;
    }
    
    private static void assertValidValues(Map<Parameter, Value> parameterValueMap) {
        for (Map.Entry<Parameter, Value> mapping : parameterValueMap.entrySet()) {
            Preconditions.notNull(mapping.getKey());
            Preconditions.notNull(mapping.getValue());
            Preconditions.check(mapping.getKey().getValues().contains(mapping.getValue()));
        }
    }
    
    /**
     * Retrieves the value of the specific parameter.
     *
     * @param parameter a parameter
     * @return the value to which this parameter is mapped or {@code null} if it is not mapped
     */
    public Value getValue(Parameter parameter) {
        return parameterValueMap.get(parameter);
    }
    
    /**
     * Retrieves the value of the parameter corresponding to the name.
     *
     * @param parameterName the name of a parameter. Should be unique in the combination
     * @return the value to which the parameter with this name i mapped or {@code null} if there is not parameter
     * with this name
     */
    public Value getValue(String parameterName) {
        for (Map.Entry<Parameter, Value> mapping : parameterValueMap.entrySet()) {
            if (mapping.getKey().getName().equals(parameterName)) {
                return mapping.getValue();
            }
        }
        
        return null;
    }
    
    /**
     * The same as {@link #getValue(Parameter)} only with {@link Value#get()} begin called afterwards.
     *
     * @param parameter a parameter
     * @return the raw object value to which this parameter is mapped or {@code null} if it is not mapped. This may be
     * ambiguous as {@code null} can also be a valid value object
     */
    public Object getRawValue(Parameter parameter) {
        Preconditions.check(parameterValueMap.containsKey(parameter));
        
        return parameterValueMap.get(parameter).get();
    }
    
    /**
     * The same as {@link #getValue(Parameter)} only with {@link Value#get()} begin called afterwards.
     *
     * @param parameterName the name of a parameter. Should be unique in the combination
     * @return the raw object value to which this parameter is mapped or {@code null} if it is not mapped. This may be
     * ambiguous as {@code null} can also be a valid value object
     */
    public Object getRawValue(String parameterName) {
        for (Map.Entry<Parameter, Value> mapping : parameterValueMap.entrySet()) {
            if (mapping.getKey().getName().equals(parameterName)) {
                return mapping.getValue().get();
            }
        }
        
        throw new IllegalArgumentException("There is no parameter with name \"" + parameterName + "\"");
    }
    
    /**
     * @return a copy of the complete map of all parameter value mappings
     */
    public Map<Parameter, Value> getParameterValueMap() {
        return Collections.unmodifiableMap(parameterValueMap);
    }
    
    /**
     * @return the number of parameters which are mapped to a value
     */
    public int size() {
        return parameterValueMap.size();
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final Combination other = (Combination) object;
        return Objects.equals(parameterValueMap, other.parameterValueMap);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(parameterValueMap);
    }
    
    @Override
    public String toString() {
        return "["
                + parameterValueMap.entrySet().stream()
                        .map(entry -> entry.getKey().getName() + "=" + entry.getValue().toString())
                        .collect(Collectors.joining(", "))
                + ']';
    }

    /**
     * Checks if {@code this} combination contains all parameter-value pairs of {@code otherCombination}.
     *
     * @param otherCombination  another combination
     * @return                  {@code true} if all parameter-value pairs of {@code otherCombination} are contained by {@code this}.
     *                          Otherwise, {@code false}.
     */
    public boolean contains(Combination otherCombination) {
        Preconditions.notNull(otherCombination);

        for(Map.Entry<Parameter, Value> otherEntry : otherCombination.parameterValueMap.entrySet()) {
            final Value value = this.parameterValueMap.get(otherEntry.getKey());

            if(value == null) {
                return false;
            }

            final Value otherValue = otherEntry.getValue();

            if(!value.equals(otherValue)) {
                return false;
            }
        }

        return true;
    }
    
    @SafeVarargs
    public static Builder combination(Map.Entry<String, Object>... entries) {
        Preconditions.notNull(entries);
        
        return new Builder(List.of(entries));
    }
    
    public static final class Builder {
        
        private final List<Map.Entry<String, Object>> entries;
        
        private Builder(Collection<Map.Entry<String, Object>> entries) {
            this.entries = new ArrayList<>(entries);
        }
        
        public Combination build(Collection<Parameter> parameters) {
            final Map<Parameter, Value> parameterValueMap = new HashMap<>(entries.size());
            
            for (Map.Entry<String, Object> entry : entries) {
                final Parameter parameter = findParameterByName(entry.getKey(), parameters);
                final Value value = findValueByData(entry.getValue(), parameter);
                parameterValueMap.put(parameter, value);
            }
            
            return Combination.of(parameterValueMap);
        }
        
        private Parameter findParameterByName(String parameterName, Collection<Parameter> parameters) {
            return parameters.stream()
                    .filter(parameter -> parameter.getName().equals(parameterName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Could not find parameter with name " + parameterName + " in parameters " + parameters));
        }
        
        private Value findValueByData(Object data, Parameter parameter) {
            return parameter.getValues().stream()
                    .filter(value -> Objects.equals(value.get(), data))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Could not find value " + data + " in parameter " + parameter));
        }
        
    }
    
}
