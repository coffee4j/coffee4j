package de.rwth.swc.coffee4j.junit.engine;

import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import org.junit.platform.engine.UniqueId;

import java.lang.reflect.Method;

/**
 * Generator for {@link UniqueId}s for combinatorial test related {@link org.junit.platform.engine.TestDescriptor}s
 */
public class UniqueIdGenerator {
    
    public static final String SEGMENT_TYPE_CLASS = "class";
    public static final String SEGMENT_TYPE_METHOD = "method";
    public static final String SEGMENT_TYPE_COMBINATION = "combination";

    private UniqueIdGenerator() {}
    
    public static UniqueId appendIdFromClass(UniqueId uniqueId, Class<?> clazz) {
        return uniqueId
                .append(SEGMENT_TYPE_CLASS, clazz.getCanonicalName());
    }

    /**
     * Appends a method name to an existing {@link UniqueId}
     *
     * @param uniqueId the id which to append to
     * @param method the method for which a unique id is appended
     * @return the newly concatenated id
     */
    public static UniqueId appendIdFromMethod(UniqueId uniqueId, Method method) {
        return uniqueId
                .append(SEGMENT_TYPE_METHOD, method.getName());
    }

    /**
     * Appends a combination name to an existing {@link UniqueId}
     *
     * @param uniqueId the id which to append to
     * @param combination the combination for which a unique id is appended
     * @return the newly concatenated id
     */
    public static UniqueId appendIdFromCombination(UniqueId uniqueId, Combination combination) {
        return uniqueId
                .append(SEGMENT_TYPE_COMBINATION, combination.toString());
    }
}
