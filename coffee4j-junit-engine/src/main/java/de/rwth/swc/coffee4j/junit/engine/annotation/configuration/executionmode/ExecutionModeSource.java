package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.executionmode;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which registers a custom {@link ExecutionModeProvider} implementation for the annotated test method.
 *
 * <p>This can be used as a meta-annotation in order to create a custom annotation that inherits the semantics
 * of {@link ExecutionModeSource}.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutionModeSource {
    
    /**
     * Gets the class of the {@link ExecutionModeProvider} to use
     *
     * @return the class of the {@link ExecutionModeProvider} to use
     */
    Class<? extends ExecutionModeProvider> value();
    
}
