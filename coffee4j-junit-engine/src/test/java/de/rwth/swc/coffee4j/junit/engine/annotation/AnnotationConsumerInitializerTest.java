package de.rwth.swc.coffee4j.junit.engine.annotation;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class AnnotationConsumerInitializerTest {

    private static final String annotatedValue = "annotatedValue";

    @Test
    void singleAnnotationTest() {
        Consumer consumer = new Consumer();
        assertThatCode(() -> AnnotationConsumerInitializer.initialize(AnnotatedElement.class, consumer))
                .doesNotThrowAnyException();
        assertThat(consumer.value)
                .isEqualTo(annotatedValue);

    }

    @Test
    void missingAnnotation() {
        Consumer consumer = new Consumer();
        assertThatExceptionOfType(Coffee4JException.class)
                .isThrownBy(() -> AnnotationConsumerInitializer.initialize(MissingAnnotationElement.class, consumer))
                .withMessageContaining("Consumer")
                .withMessageContaining("SomeAnnotation");
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface SomeAnnotation {
        String value();
    }

    @SomeAnnotation(annotatedValue)
    static class AnnotatedElement {}

    static class MissingAnnotationElement {}

    static class Consumer implements AnnotationConsumer<SomeAnnotation> {

        private String value;

        @Override
        public void accept(SomeAnnotation someAnnotation) {
            this.value = someAnnotation.value();
        }

    }
}
