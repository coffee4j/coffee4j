package de.rwth.swc.coffee4j.junit.engine.annotation.util.configuration;

import java.lang.annotation.*;

/**
 * Repeatable annotation for {@link ConfigurationSource}.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationSources {

    ConfigurationSource[] value();
    
}
