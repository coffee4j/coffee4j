package de.rwth.swc.coffee4j.junit.engine.annotation;

import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.ConfigurationFromMethod;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.ConfigurationProvider;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.ConfigurationSource;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.DelegatingConfigurationProvider;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.report.CombinationArgumentConverter;
import de.rwth.swc.coffee4j.engine.report.ParameterArgumentConverter;
import de.rwth.swc.coffee4j.engine.report.TupleListArgumentConverter;
import de.rwth.swc.coffee4j.engine.report.ValueArgumentConverter;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.model.InputParameterModelSource;
import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a combinatorial test.
 *
 * <p>Configuration of the combinatorial
 * test is also possible via a {@link ConfigurationProvider} using
 * a {@link ConfigurationSource}.
 * By default, a {@link DelegatingConfigurationProvider} will be used, but
 * alternatively it is also possible to use custom configurations such as the
 * {@link ConfigurationFromMethod}. All configurable aspects have
 * sensible default.
 * If only a {@link InputParameterModel} is specified, the combinatorial
 * test will be executed with a {@link de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.Ipog},
 * no fault characterization and execution reporter, and some default
 * {@link de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter} such as {@link ParameterArgumentConverter},
 * {@link ValueArgumentConverter}, {@link TupleListArgumentConverter}, and {@link CombinationArgumentConverter}.
 *
 * <p>{@link CombinatorialTest} may also be used as a meta-annotation in order to create a custom composed annotation
 * which inherits the semantics of a {@link CombinatorialTest}.
 *
 * <p>This annotation is more or less a copy of the {@code org.junit.jupiter.params.ParameterizedTest} annotation
 * provided in the junit-jupiter-params project.
 */
@Documented
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Testable
public @interface CombinatorialTest {
    
    /**
     * Defines a custom display name for individual invocations of the {@link CombinatorialTest}. Should never
     * be blank or consist of white spaces. This text is what is show in various IDEs to make a test identifiable
     * to the user.
     *
     * <p>Multiple placeholders are supported:
     * <ul>
     *     <li>{combination}: the complete {@link Combination} which is tested by the test</li>
     *     <li>{PARAMETER_NAME}: the value of the {@link Parameter} with the given name in the currently tested
     *         {@link Combination}</li>
     * </ul>
     *
     * @return the name pattern for all test inputs in this {@link CombinatorialTest}
     */
    String name() default "{combination}";
    
    /**
     * Determines the name of the static method that provides the {@link InputParameterModel}.
     *
     * <p>The default value is {@code "model"} but it can be changed to any valid method name.
     *
     * <p>Please note the field can be made obsolete when a custom {@link InputParameterModelSource} is used.
     *
     * @return the name of the static method that provides the {@link InputParameterModel}
     */
    String inputParameterModel() default "model";
    
    /**
     * Defines the execution mode used for the combinatorial test.
     *
     * <p>The default value of {@link ExecutionMode#EXECUTE_ALL} enforces the execution of all individual test cases
     * even if some of them fail.
     *
     * @return the execution mode used for this combinatorial test method
     */
    ExecutionMode executionMode() default ExecutionMode.EXECUTE_ALL;
    
}
