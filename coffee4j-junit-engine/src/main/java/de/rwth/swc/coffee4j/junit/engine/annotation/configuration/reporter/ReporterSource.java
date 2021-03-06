package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.reporter;

import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code ReporterSource} is an annotation used to register
 * {@linkplain ReporterProvider reporter providers} for the annotated test method.
 * <p>
 * This may also be used as a meta-annotation in order to create a custom composed annotation that inherits the
 * semantics of {@code ReporterSource} (demonstrated by {@link EnableReporter}).
 * <p>
 * This annotation is repeatable via {@link ReporterSources}, and as such multiple providers can be registered.
 * <p>
 * This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsSource} from the
 * junit-jupiter-params project.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ReporterSources.class)
public @interface ReporterSource {

    /**
     * Gets the class of the {@link ReporterProvider}
     *
     * @return the class which provides {@link ExecutionReporter}s. Must
     * have a no-args constructor
     */
    Class<? extends ReporterProvider> value();
}
