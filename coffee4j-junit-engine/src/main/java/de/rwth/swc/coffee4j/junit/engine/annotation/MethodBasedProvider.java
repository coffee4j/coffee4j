package de.rwth.swc.coffee4j.junit.engine.annotation;

import java.lang.reflect.Method;

/**
 * Provides a certain type of object, that is requested by a {@link Loader}.
 *
 * @param <T> the type of the loaded object
 *
 */
@FunctionalInterface
public interface MethodBasedProvider<T> {

    /**
     * Loads a T based on the method
     *
     * @param method the method from which the information should be loaded
     * @return the loaded object
     */
    T provide(Method method);
}
