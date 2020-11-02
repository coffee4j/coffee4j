package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving;

import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration.InterleavingConfigurationSource;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration.MethodBasedInterleavingConfigurationProvider;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;

import java.lang.annotation.*;

/**
 * used to model a combinatorial test that uses the interleaving approach.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@InterleavingConfigurationSource(MethodBasedInterleavingConfigurationProvider.class)
public @interface EnableInterleavingGeneration {
    /**
     * The name of the method from which a {@link InterleavingExecutionConfiguration} can be loaded.
     * Consequently, the method defined by the value must either return a
     * {@link InterleavingExecutionConfiguration} directly, or a
     * {@link InterleavingExecutionConfiguration.Builder} which can be
     * build. The method should not require any parameters.
     *
     * <p>There are four valid ways to specify the factory method which should be used:
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
