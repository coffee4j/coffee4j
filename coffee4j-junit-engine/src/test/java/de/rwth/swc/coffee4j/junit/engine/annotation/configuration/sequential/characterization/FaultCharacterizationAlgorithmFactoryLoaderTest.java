package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization;

import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.MockingTest;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class FaultCharacterizationAlgorithmFactoryLoaderTest implements MockingTest {

    private static final FaultCharacterizationAlgorithmFactory mockedFactory =
            mock(FaultCharacterizationAlgorithmFactory.class);
    private static String acceptedValue;
    private static final String annotationString = "hello";

    @Test
    void loadsFaultCharacterizationAlgorithmFactory() throws NoSuchMethodException {
        final FaultCharacterizationAlgorithmFactoryLoader loader = new FaultCharacterizationAlgorithmFactoryLoader();
        final Method method = this.getClass().getMethod("testMethod");
        assertThat(loader.load(method))
                .isPresent()
                .hasValue(mockedFactory);
        assertThat(acceptedValue).isEqualTo(annotationString);
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface SomeAnnotation {

        String value();
    }
    
    @FaultCharacterizationAlgorithmFactorySource(SomeProvider.class)
    @SomeAnnotation(annotationString)
    public void testMethod() {
    }

    private static class SomeProvider implements FaultCharacterizationAlgorithmFactoryProvider,
            AnnotationConsumer<SomeAnnotation> {

        @Override
        public FaultCharacterizationAlgorithmFactory provide(Method method) {
            return mockedFactory;
        }

        @Override
        public void accept(SomeAnnotation someAnnotation) {
            acceptedValue  = someAnnotation.value();
        }
    }
}
