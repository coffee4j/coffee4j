package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.reporter;

import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class for loading multiple {@link ExecutionReporter}s via {@link ReporterProvider}. These providers are
 * discovered using the {@link ReporterSource} repeatable annotations. As such, multiple {@link ExecutionReporter}
 * provided by each {@link ReporterSource} are aggregated into one single list in this loader.
 */
public class ReporterLoader implements Loader<List<ExecutionReporter>> {

    @Override
    public List<ExecutionReporter> load(Method method) {
        return AnnotationSupport.findRepeatableAnnotations(method, ReporterSource.class)
                .stream()
                .map(ReporterSource::value)
                .map(ReflectionSupport::newInstance)
                .map(provider -> AnnotationConsumerInitializer.initialize(method, provider))
                .map(provider -> provider.provide(method))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
