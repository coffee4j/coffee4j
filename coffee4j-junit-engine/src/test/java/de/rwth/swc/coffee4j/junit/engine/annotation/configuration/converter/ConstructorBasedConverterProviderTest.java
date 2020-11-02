package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.converter;

import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.junit.engine.annotation.MockingTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class ConstructorBasedConverterProviderTest implements MockingTest {

    @Test
    void providesConverter() {
        final ConstructorBasedConverterProvider provider = new ConstructorBasedConverterProvider();
        final EnableConverter annotation = new EnableConverter() {

            @Override
            @SuppressWarnings("unchecked")
            public Class<? extends ArgumentConverter>[] value() {
                return new Class[] {SomeArgumentConverter.class, AnotherArgumentConverter.class};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return EnableConverter.class;
            }
        };
        provider.accept(annotation);

        final Method someMethod = Mockito.mock(Method.class);

        final Collection<ArgumentConverter> providedConverters = provider.provide(someMethod);
        assertThat(providedConverters)
                .extracting("class")
                .containsExactlyInAnyOrder(SomeArgumentConverter.class, AnotherArgumentConverter.class);
    }

    private static class SomeArgumentConverter implements ArgumentConverter {

        @Override
        public boolean canConvert(Object argument) {
            return false;
        }

        @Override
        public Object convert(Object argument) {
            return null;
        }

    }

    private static class AnotherArgumentConverter implements ArgumentConverter {

        @Override
        public boolean canConvert(Object argument) {
            return false;
        }

        @Override
        public Object convert(Object argument) {
            return null;
        }
    }
}
