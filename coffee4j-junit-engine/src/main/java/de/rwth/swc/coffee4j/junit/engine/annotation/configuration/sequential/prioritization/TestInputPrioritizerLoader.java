package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.prioritization;

import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.NoOpTestInputPrioritizer;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;

/**
 * Class for loading the defined {@link TestInputPrioritizer} for a {@link CombinatorialTest}.
 *
 * <p>At most one annotation of {@link TestInputPrioritizerSource} is allowed per test method.
 * Since {@link TestInputPrioritizerSource} is inherited, any inheriting annotation such as
 * {@link EnableTestInputPrioritization} is also found by this loader.
 *
 * <p>If no annotation is found, the {@link NoOpTestInputPrioritizer} is returned. This means that
 * the test inputs in the annotation {@link CombinatorialTest} will not be prioritized in any way.
 */
public class TestInputPrioritizerLoader implements Loader<TestInputPrioritizer> {
    
    @Override
    public TestInputPrioritizer load(Method method) {
        return AnnotationSupport.findAnnotation(method, TestInputPrioritizerSource.class)
                .map(TestInputPrioritizerSource::value)
                .map(ReflectionSupport::newInstance)
                .map(provider -> AnnotationConsumerInitializer.initialize(method, provider))
                .map(provider -> provider.provide(method))
                .orElse(new NoOpTestInputPrioritizer());
    }
    
}
