package de.rwth.swc.coffee4j.engine.configuration;

/**
 * Represents a builder class for a certain type
 * @param <T> the type this builder builds
 */
@FunctionalInterface
public interface Buildable<T> {

    /**
     * Builds a T
     *
     * @return the built T
     */
    T build();

}
