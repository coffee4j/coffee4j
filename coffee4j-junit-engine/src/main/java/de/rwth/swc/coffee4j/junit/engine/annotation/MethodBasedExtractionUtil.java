package de.rwth.swc.coffee4j.junit.engine.annotation;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.Buildable;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Util class for extracting an element from a method and initiating its builder if necessary
 */
public class MethodBasedExtractionUtil {

    private MethodBasedExtractionUtil() { }

    public static <A, B extends Buildable<A>> A extractTypedObjectFromMethod(Method method,
                                                                              Class<A> typeClass,
                                                                              Class<B> builderClass) {
        return toTypeClass(
                ReflectionUtils.getObjectReturnedByMethod(method),
                typeClass,
                builderClass
        );
    }

    private static <T, S extends Buildable<T>> T toTypeClass(Object object, Class<T> typeClass, Class<S> builderClass) {
        Preconditions.notNull(object);
        if (typeClass.isAssignableFrom(object.getClass())) {
            return typeClass.cast(object);
        } else if (builderClass.isAssignableFrom(object.getClass())) {
            return builderClass.cast(object).build();
        } else {
            throw new Coffee4JException(
                    "The given method must either return an %s or an %s. Instead a %s was returned",
                    typeClass.getName(),
                    builderClass.getName(),
                    object.getClass().getName()
            );
        }
    }
}
