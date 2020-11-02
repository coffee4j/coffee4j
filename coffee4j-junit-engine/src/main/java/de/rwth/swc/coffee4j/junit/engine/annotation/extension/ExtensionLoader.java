package de.rwth.swc.coffee4j.junit.engine.annotation.extension;

import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Loads the {@link Extension extensions} of a {@link CombinatorialTest}
 * using the {@link EnableExtension} annotation
 */
public class ExtensionLoader implements Loader<List<Extension>> {
    
    @Override
    public List<Extension> load(Method method) {
        return AnnotationSupport.findRepeatableAnnotations(method, ExtensionSource.class).stream()
                .map(ExtensionSource::value)
                .map(ReflectionSupport::newInstance)
                .map(provider -> AnnotationConsumerInitializer.initialize(method, provider))
                .map(provider -> provider.provide(method))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
}
