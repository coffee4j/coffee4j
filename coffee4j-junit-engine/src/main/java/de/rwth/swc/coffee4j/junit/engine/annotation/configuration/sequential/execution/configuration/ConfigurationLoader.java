package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Class for loading the defined configuration for a {@link CombinatorialTest}.
 * by default, this class uses the {@link DelegatingConfigurationProvider} provider to construct a new
 * {@link SequentialExecutionConfiguration}, but instead it is also possible to provide exactly
 * one {@link ConfigurationSource}. Since {@link ConfigurationSource} is a meta-annotation, any inheriting
 * annotation such as {@link ConfigurationFromMethod} can also be found by this loader.
 */
public class ConfigurationLoader implements Loader<SequentialExecutionConfiguration> {

    @Override
    public SequentialExecutionConfiguration load(Method method) {
        return AnnotationSupport.findAnnotation(method, ConfigurationSource.class)
                .map(ConfigurationSource::value)
                .map(ReflectionSupport::newInstance)
                .map(provider -> AnnotationConsumerInitializer.initialize(method, provider))
                .map(ConfigurationProvider.class::cast)
                .or(() -> Optional.of(new DelegatingConfigurationProvider()))
                .map(provider -> provider.provide(method))
                .orElseThrow(() -> new Coffee4JException("A configuration has to be provided for a combinatorial test"));
    }

}
