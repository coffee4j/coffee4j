package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.EnableInterleavingGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.ConfigurationLoader;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Class for loading the defined configuration for a {@link CombinatorialTest}.
 * By default, this class uses the {@link DelegatingInterleavingConfigurationProvider} provider to construct a new
 * {@link InterleavingExecutionConfiguration}, but instead it is also possible to provide exactly
 * one {@link InterleavingConfigurationSource}. Since {@link InterleavingConfigurationSource} is a meta-annotation, any inheriting
 * annotation such as {@link EnableInterleavingGeneration} can also be found by this loader.
 * <p>
 *     Copy of {@link ConfigurationLoader} for Interleaving Combinatorial Testing.
 * </p>
 */
public class InterleavingConfigurationLoader implements Loader<InterleavingExecutionConfiguration> {
    
    private final boolean isGeneratingConfigurationNeeded;

    public InterleavingConfigurationLoader(boolean isGeneratingConfigurationNeeded) {
        this.isGeneratingConfigurationNeeded = isGeneratingConfigurationNeeded;
    }

    @Override
    public InterleavingExecutionConfiguration load(Method method) {
        return AnnotationSupport.findAnnotation(method, InterleavingConfigurationSource.class)
                .map(InterleavingConfigurationSource::value)
                .map(ReflectionSupport::newInstance)
                .map(provider -> AnnotationConsumerInitializer.initialize(method, provider))
                .map(InterleavingConfigurationProvider.class::cast)
                .or(() -> Optional.of(new DelegatingInterleavingConfigurationProvider(isGeneratingConfigurationNeeded)))
                .map(provider -> provider.provide(method))
                .orElseThrow(() -> new Coffee4JException("A configuration must be provided for an interleaving combinatorial test!"));
    }
    
}
