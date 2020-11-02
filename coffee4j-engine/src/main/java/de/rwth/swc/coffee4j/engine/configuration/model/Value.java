package de.rwth.swc.coffee4j.engine.configuration.model;

import java.util.Objects;
import java.util.OptionalDouble;

/**
 * Represents a value for a input parameter testModel in combinatorial testing. One could say that this class is not really
 * needed as it just wraps an object, but it gives the additional distinction between this value object being
 * {@code null} and no object being present. The id field is need for quicker comparison in {@link #hashCode()}
 * and {@link #equals(Object)} and is only valid when comparing values inside one {@link Parameter}.
 */
public final class Value {
    
    private final int id;
    
    private final Object data;
    
    private final Double weight;
    
    /**
     * Creates a new value with the given id and object
     *
     * @param id   an id which should be unique inside the values parameter
     * @param data the data value to be saved. Can be {@code null}
     */
    public Value(int id, Object data) {
        this(id, data, null);
    }
    
    /**
     * Creates a new value with the given id and object
     *
     * @param id   an id which should be unique inside the values parameter
     * @param data the data value to be saved. Can be {@code null}
     * @param weight the weight of the value which can be used to prioritize values. Higher weight = higher priority
     */
    public Value(int id, Object data, Double weight) {
        this.id = id;
        this.data = data;
        this.weight = weight;
    }
    
    /**
     * @return the values id which is unique only inside its parameter
     */
    public int getId() {
        return id;
    }
    
    /**
     * @return the actual value. May be {@code null}
     */
    public Object get() {
        return data;
    }
    
    public OptionalDouble getWeight() {
        if (weight == null) {
            return OptionalDouble.empty();
        } else {
            return OptionalDouble.of(weight);
        }
    }
    
    public double getRequiredWeight() {
        if (weight == null) {
            throw new IllegalStateException("weight was required but is not present");
        }
        
        return weight;
    }
    
    public boolean hasWeight() {
        return weight != null;
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final Value other = (Value) object;
        return Objects.equals(id, other.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return (data == null ? "null" : data.toString())
                + (weight == null ? "" : " (weight=" + weight + ')');
    }
    
    /**
     * Convenience method which can be statically imported for easier and more readable code.
     *
     * @param id   an id which should be unique inside the values parameter
     * @param data the data value to be saved. Can be {@code null}
     * @return a value with the given id and data
     */
    public static Value value(int id, Object data) {
        return new Value(id, data);
    }
    
    /**
     * Convenience method which can be statically imported for easier and more readable code.
     *
     * @param id   an id which should be unique inside the values parameter
     * @param data the data value to be saved. Can be {@code null}
     * @param weight the weight of the value which can be used to prioritize values. Higher weight = higher priority
     * @return a value with the given id and data
     */
    public static Value value(int id, Object data, double weight) {
        return new Value(id, data, weight);
    }
    
    /**
     * Creates a {@link Builder} with the given data and weight.
     *
     * <p>This can be used together with {@link Parameter.Builder} to given weights to values.
     *
     * @param data the data value to be saved. Can be {@code null}
     * @param weight the weight of the value which can be used to prioritize values. Higher weight = higher priority
     * @return the builder with the given data
     */
    public static Builder weighted(Object data, double weight) {
        return new Builder(data, weight);
    }
    
    /**
     * Helper class which temporary stores data and weight until an id can be assigned to the value.
     */
    public static final class Builder {
        
        private final Object data;
        private final double weight;
        
        private Builder(Object data, double weight) {
            this.data = data;
            this.weight = weight;
        }
    
        /**
         * Creates the value instance with the missing given id.
         *
         * @param id an id which should be unique inside the values parameter
         * @return the value with the given id and the data and weight stored in the builder
         */
        public Value build(int id) {
            return new Value(id, data, weight);
        }
        
    }
    
}
