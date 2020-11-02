package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.converter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The repeatable variant of {@link ConverterSource}.
 * <p>
 * This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsSources} from the
 * junit-jupiter-params project.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConverterSources {

    /**
     * Gets an array of multiple {@link ConverterSource} annotations
     *
     * @return an array of multiple {@link ConverterSource} annotations
     */
    ConverterSource[] value();
}
