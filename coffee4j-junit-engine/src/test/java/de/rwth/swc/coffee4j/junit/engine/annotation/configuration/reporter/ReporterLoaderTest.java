package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.reporter;

import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;
import de.rwth.swc.coffee4j.junit.engine.annotation.MockingTest;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ReporterLoaderTest implements MockingTest {

    private static final ExecutionReporter reporterOne = mock(ExecutionReporter.class);
    private static final ExecutionReporter reporterTwo = mock(ExecutionReporter.class);
    private static final Collection<ExecutionReporter> mockedReporters = List.of(reporterOne, reporterTwo);

    private static String acceptedValue;
    private static final String annotationString = "hello";

    @Test
    void loadsReporters() throws NoSuchMethodException {
        final ReporterLoader loader = new ReporterLoader();
        final Method method = this.getClass().getMethod("testMethod");
        assertThat(loader.load(method))
                .isNotEmpty()
                .hasSize(2)
                .doesNotContainNull()
                .containsOnlyOnce(reporterOne, reporterTwo);
        assertThat(acceptedValue).isEqualTo(annotationString);
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface SomeAnnotation {

        String value();
    }

    @ReporterSource(SomeProvider.class)
    @SomeAnnotation(annotationString)
    public void testMethod() {
    }

    private static class SomeProvider implements ReporterProvider,
            AnnotationConsumer<SomeAnnotation> {

        @Override
        public Collection<ExecutionReporter> provide(Method method) {
            return mockedReporters;
        }

        @Override
        public void accept(SomeAnnotation someAnnotation) {
            acceptedValue  = someAnnotation.value();
        }
    }
}
