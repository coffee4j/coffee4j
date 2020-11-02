package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration;

import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a {@link ConfigurationSource} which provides access to values returned from a
 * {@linkplain #value() factory method} of the class in which this annotation is declared or from static
 * factory methods in external classes referenced by the fully qualified name (classname#methodname).
 *
 * <p>Factory methods within the test class can either be {@code static} or not.
 * If the method is not {@code static}, however, the declaring class must have a no-args constructor.
 * In any case, factory methods must not declare any parameters.
 * <p>
 * This is a more of less direct copy of {@code org.junit.jupiter.params.provider.MethodSource} from the
 * junit-jupiter-params project.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ConfigurationSource(MethodBasedConfigurationProvider.class)
public @interface ConfigurationFromMethod {

    /**
     * The name of the method from which a
     * {@link SequentialExecutionConfiguration} can be loaded.
     * Consequently, the method defined by the value must either return a
     * {@link SequentialExecutionConfiguration} directly, or a
     * {@link SequentialExecutionConfiguration.Builder} which can be
     * build. The method should not require any parameters.
     * <p>
     * There are four valid ways to specify the factory method which should be used:
     * -annotated on the {@link CombinatorialTest}:
     *      -empty string: this is the default and looks for a factory method in the same class as the test method and
     *      with the same name as the test method. As a {@link CombinatorialTest} has at least
     *      one parameter, java will allow methods with the same name, but no parameters
     *      -the name of a method: The method needs to be in the same class as the test method
     *      -a fully qualified name in the format of classname#methodname from which the testModel is then loaded
     * -annotate the method inside the {@link CombinatorialTest} directly.
     * the name is then ignored.
     *
     * @return the name of the method in one of the three schemas explained above
     */
    String value() default "";
    
}
