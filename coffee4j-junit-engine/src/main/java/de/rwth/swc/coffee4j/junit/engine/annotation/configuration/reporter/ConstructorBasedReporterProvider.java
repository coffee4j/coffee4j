package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.reporter;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.EnableInterleavingGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.ReflectionUtils;
import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;
import de.rwth.swc.coffee4j.engine.report.InterleavingExecutionReporter;
import de.rwth.swc.coffee4j.engine.report.SequentialExecutionReporter;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

class ConstructorBasedReporterProvider implements ReporterProvider, AnnotationConsumer<EnableReporter> {

    private Class<? extends ExecutionReporter>[] reporterClasses;

    private ReportLevel level;

    @Override
    public void accept(EnableReporter reporter) {
        reporterClasses = reporter.value();
        level = reporter.useLevel() ? reporter.level() : null;
    }

    @Override
    public Collection<ExecutionReporter> provide(Method method) {
        if (AnnotationSupport.isAnnotated(method, EnableInterleavingGeneration.class)) {
            for (Class<? extends ExecutionReporter> reporter : reporterClasses) {
                if (SequentialExecutionReporter.class.isAssignableFrom(reporter)) {
                    throw new Coffee4JException("Reporter implementing InterleavingExecutionReporterInterface must be provided!");
                }
            }
        } else {
            for (Class<? extends ExecutionReporter> reporter : reporterClasses) {
                if (InterleavingExecutionReporter.class.isAssignableFrom(reporter)) {
                    throw new Coffee4JException("Reporter implementing SequentialExecutionReporterInterface must be provided!");
                }
            }
        }

        return Arrays.stream(reporterClasses)
                .map(level != null ?
                    reporterClass -> ReflectionUtils.createNewInstance(reporterClass, level) :
                    ReflectionUtils::createNewInstance
                    )
                .collect(Collectors.toList());
    }

}
