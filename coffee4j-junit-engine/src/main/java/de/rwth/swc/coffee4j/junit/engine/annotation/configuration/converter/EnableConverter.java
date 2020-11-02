package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.converter;

import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a {@link ConverterSource} using the {@link ConstructorBasedConverterProvider} to create new instances of a
 * {@link ArgumentConverter} via a no-args constructor. Since multiple converters are allowed,
 * just return multiple classes in the {@link #value()} method to register more converters, or use any other
 * {@link ConverterSource} since {@link ConverterSource} is a repeatable annotation.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ConverterSource(ConstructorBasedConverterProvider.class)
public @interface EnableConverter {

    /**
     * Gets the class of the {@link ArgumentConverter}
     *
     * @return the class of a {@link ArgumentConverter} which has a no-args constructor
     */
    Class<? extends ArgumentConverter>[] value();
}
