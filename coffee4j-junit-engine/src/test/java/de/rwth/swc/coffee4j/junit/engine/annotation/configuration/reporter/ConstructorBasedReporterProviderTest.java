package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.reporter;

import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;
import de.rwth.swc.coffee4j.junit.engine.annotation.MockingTest;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class ConstructorBasedReporterProviderTest implements MockingTest {

    @Test
    void providesReporter() throws NoSuchMethodException {
        final ConstructorBasedReporterProvider provider = new ConstructorBasedReporterProvider();
        final EnableReporter annotation = new EnableReporter() {

            @Override
            @SuppressWarnings("unchecked")
            public Class<? extends ExecutionReporter>[] value() {
                return new Class[] {SomeReporter.class, AnotherReporter.class};
            }

            @Override
            public ReportLevel level() {
                return ReportLevel.ERROR;
            }

            @Override
            public boolean useLevel() {
                return true;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return EnableReporter.class;
            }
        };
        provider.accept(annotation);

        final Method someMethod = this.getClass().getMethod("someMethod");

        final Collection<ExecutionReporter> providedReporters = provider.provide(someMethod);
        assertThat(providedReporters)
                .extracting("class")
                .containsExactlyInAnyOrder(SomeReporter.class, AnotherReporter.class);
        assertThat(providedReporters)
                .allSatisfy(reporter ->
                        assertThat(reporter.getReportLevel()).isEqualTo(ReportLevel.ERROR)
                );
    }
    
    public void someMethod() {
    }

    private static class SomeReporter implements ExecutionReporter {

        private final ReportLevel level;

        public SomeReporter(ReportLevel level) {
            this.level = level;
        }

        @Override
        public ReportLevel getReportLevel() {
            return level;
        }

    }

    private static class AnotherReporter implements ExecutionReporter {

        private final ReportLevel level;

        public AnotherReporter(ReportLevel level) {
            this.level = level;
        }

        @Override
        public ReportLevel getReportLevel() {
            return level;
        }
    }
}
