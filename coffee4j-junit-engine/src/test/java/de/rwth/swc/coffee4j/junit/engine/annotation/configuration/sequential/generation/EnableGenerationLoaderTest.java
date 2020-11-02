package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation;

import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.Ipog;
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

class EnableGenerationLoaderTest implements MockingTest {

    private static final TestInputGroupGenerator generatorOne = mock(TestInputGroupGenerator.class);
    private static final TestInputGroupGenerator generatorTwo = mock(TestInputGroupGenerator.class);
    private static final Collection<TestInputGroupGenerator> mockedGenerators = List.of(generatorOne, generatorTwo);

    private static String acceptedValue;
    private static final String annotationString = "hello";

    @Test
    void loadsAnnotatedGenerators() throws NoSuchMethodException {
        final GeneratorLoader loader = new GeneratorLoader();
        final Method method = this.getClass().getMethod("annotatedTestMethod");
        assertThat(loader.load(method))
                .isNotEmpty()
                .hasSize(2)
                .doesNotContainNull()
                .containsExactlyInAnyOrder(generatorOne, generatorTwo);
        assertThat(acceptedValue).isEqualTo(annotationString);
    }

    @Test
    void loadsDefaultGenerator() throws NoSuchMethodException {
        final GeneratorLoader loader = new GeneratorLoader();
        final Method method = this.getClass().getMethod("defaultTestMethod");
        assertThat(loader.load(method))
                .isNotEmpty()
                .hasSize(1)
                .hasOnlyElementsOfType(Ipog.class);
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface SomeAnnotation {

        String value();
    }

    @GeneratorSource(SomeProvider.class)
    @SomeAnnotation(annotationString)
    public void annotatedTestMethod() {
    }
    
    public void defaultTestMethod() {
    }

    private static class SomeProvider implements GeneratorProvider,
            AnnotationConsumer<SomeAnnotation> {

        @Override
        public Collection<TestInputGroupGenerator> provide(Method method) {
            return mockedGenerators;
        }

        @Override
        public void accept(SomeAnnotation someAnnotation) {
            acceptedValue  = someAnnotation.value();
        }
    }
}
