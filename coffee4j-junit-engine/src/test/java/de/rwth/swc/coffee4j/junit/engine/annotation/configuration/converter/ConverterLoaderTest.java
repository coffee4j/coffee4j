package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.converter;

import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.engine.report.CombinationArgumentConverter;
import de.rwth.swc.coffee4j.engine.report.ParameterArgumentConverter;
import de.rwth.swc.coffee4j.engine.report.TupleListArgumentConverter;
import de.rwth.swc.coffee4j.engine.report.ValueArgumentConverter;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.MockingTest;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ConverterLoaderTest implements MockingTest {

    private static final ArgumentConverter converterOne = mock(ArgumentConverter.class);
    private static final ArgumentConverter converterTwo = mock(ArgumentConverter.class);
    private static final Collection<ArgumentConverter> mockedConverters = List.of(converterOne, converterTwo);

    private static String acceptedValue;
    private static final String annotationString = "hello";

    @Test
    void loadsConverters() throws NoSuchMethodException {
        final Method method = this.getClass().getMethod("testMethod");
        
        final ConverterLoader loader = new ConverterLoader();
        assertThat(loader.load(method))
                .isNotEmpty()
                .hasSize(6)
                .doesNotContainNull()
                .containsOnlyOnce(converterOne, converterTwo)
                .extracting("class")
                .containsOnlyOnce(CombinationArgumentConverter.class, ParameterArgumentConverter.class,
                        TupleListArgumentConverter.class, ValueArgumentConverter.class);
        assertThat(acceptedValue).isEqualTo(annotationString);
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface SomeAnnotation {

        String value();
    }
    
    @ConverterSource(SomeProvider.class)
    @SomeAnnotation(annotationString)
    public void testMethod() {
    
    }

    private static class SomeProvider implements ConverterProvider,
            AnnotationConsumer<SomeAnnotation> {

        @Override
        public Collection<ArgumentConverter> provide(Method method) {
            return mockedConverters;
        }

        @Override
        public void accept(SomeAnnotation someAnnotation) {
            acceptedValue  = someAnnotation.value();
        }
    }
}
