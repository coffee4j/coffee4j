package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.reporter;

import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a {@link ReporterSource} using the {@link ConstructorBasedReporterProvider} to create new instances of a
 * {@link ExecutionReporter} via a no-args, or one-arg constructor. Since reporters are allowed,
 * just return multiple classes in the {@link #value()} method to register more reporters, or use any other
 * {@link ReporterSource} since {@link ReporterSource} is a repeatable annotation.
 * <p>
 * If {@link #useLevel()} is set to {@code false} (this is the default) a no-args constructor is used to instantiate
 * the {@link ExecutionReporter}s. Otherwise, the {@link ReportLevel} defined by {@link #level()} is given to the
 * constructor so an excepting constructor is needed. This can be used to directly configure the level to which
 * reporters should listen at the method.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ReporterSource(ConstructorBasedReporterProvider.class)
public @interface EnableReporter {

    /**
     * Gets the class of the supplied {@link ExecutionReporter}
     *
     * @return the class of {@link ExecutionReporter} which listen to combinatorial test execution. Either a no-args
     * or constructor needing one {@link ReportLevel} is needed depending on the setting of {@link #useLevel()}
     * as described in {@link EnableReporter}
     */
    Class<? extends ExecutionReporter>[] value();

    /**
     * Gets the {@link ReportLevel} of the supplied {@link ExecutionReporter}
     *
     * @return at which level the {@link ExecutionReporter}s should listen if {@link #useLevel()} is enabled
     */
    ReportLevel level() default ReportLevel.INFO;

    /**
     * Gets {@code boolean} on whether to tu used the {@link ReportLevel}
     *
     * @return whether to use {@link ReportLevel} during the instantiation of all {@link ExecutionReporter}s
     */
    boolean useLevel() default false;
}