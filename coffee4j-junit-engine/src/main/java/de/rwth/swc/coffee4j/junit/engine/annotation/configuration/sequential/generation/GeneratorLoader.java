package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation;

import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.Ipog;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class for loading multiple {@link TestInputGroupGenerator}s via {@link GeneratorProvider}. These providers are
 * discovered using the {@link GeneratorSource} repeatable annotations. As such, multiple {@link TestInputGroupGenerator}
 * provided by each {@link GeneratorSource} are aggregated into one single list in this loader.
 * <p>
 * If no {@link GeneratorSource} is registered, the default of one {@link Ipog} is loaded.
 */
public class GeneratorLoader implements Loader<List<TestInputGroupGenerator>> {

    private static final TestInputGroupGenerator DEFAULT_GENERATOR = new Ipog();

    @Override
    public List<TestInputGroupGenerator> load(Method method) {
        return AnnotationSupport.findAnnotation(method, GeneratorSource.class).stream()
                .map(GeneratorSource::value)
                .map(ReflectionSupport::newInstance)
                .map(provider -> AnnotationConsumerInitializer.initialize(method, provider))
                .map(provider -> provider.provide(method))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.isEmpty() ?
                                        Collections.singletonList(DEFAULT_GENERATOR) :
                                        list
                ));

    }
}
