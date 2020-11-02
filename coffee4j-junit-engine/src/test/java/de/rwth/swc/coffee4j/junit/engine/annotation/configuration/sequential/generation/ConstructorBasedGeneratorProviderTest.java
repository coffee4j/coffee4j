package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;
import de.rwth.swc.coffee4j.junit.engine.annotation.MockingTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConstructorBasedGeneratorProviderTest implements MockingTest {

    @SuppressWarnings("unchecked")
    @Test
    void provideGenerators() {
        final ConstructorBasedGeneratorProvider provider = new ConstructorBasedGeneratorProvider();
        EnableGeneration generator = mock(EnableGeneration.class);
        when(generator.algorithms()).thenReturn(
                new Class[] {SomeGroupGenerator.class, AnotherGroupGenerator.class, SomeGroupGenerator.class}
                );

        provider.accept(generator);
        final Method someMethod = Mockito.mock(Method.class);

        when(someMethod.getDeclaredAnnotations()).thenReturn(new Annotation[0]);
        when(someMethod.getAnnotations()).thenReturn(new Annotation[0]);
        final Collection<TestInputGroupGenerator> providedGenerators = provider.provide(someMethod);

        assertThat(providedGenerators)
                .hasSize(3)
                .hasAtLeastOneElementOfType(SomeGroupGenerator.class)
                .hasAtLeastOneElementOfType(AnotherGroupGenerator.class);
    }

    static class SomeGroupGenerator implements TestInputGroupGenerator {

        @Override
        public Collection<Supplier<TestInputGroup>> generate(CompleteTestModel model, Reporter reporter) {
            return null;
       }
    }

    static class AnotherGroupGenerator implements TestInputGroupGenerator {

        @Override
        public Collection<Supplier<TestInputGroup>> generate(CompleteTestModel model, Reporter reporter) {
            return null;
        }
    }

}
