package de.rwth.swc.coffee4j.junit.engine.annotation;

import java.lang.reflect.Method;

/**
 * Loads a certain type of object, that is supplied via a {@link MethodBasedProvider} specified by a source annotation.
 *
 * @param <T> the type of the object this loader is loading
 */
@FunctionalInterface
public interface Loader<T> {

    /**
     * Loads a T based on the class
     * <p>
     *     Initializes the provider with the necessary annotation located somewhere on the class beforehand
     * </p>
     *
     * @param method the method from which the information should be loaded
     * @return the loaded object
     */
    T load(Method method);
}
