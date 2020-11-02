package de.rwth.swc.coffee4j.junit.engine.annotation.test.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link InputParameterModelSource} is an annotation used to register a custom
 * {@linkplain InputParameterModelProvider testModel providers} for the annotated test method.
 *
 * <p>This can be used as a meta-annotation in order to create a custom annotation that inherits the
 * semantics of {@link InputParameterModelSource}).
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InputParameterModelSource {

    /**
     * Gets the class of the {@link InputParameterModelProvider} to use
     *
     * @return the class of the {@link InputParameterModelProvider} to use
     */
    Class<? extends InputParameterModelProvider> value();
}
