package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.interleaving.EnableInterleavingConstraintGeneration;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Class for loading a configuration provided by {@link EnableInterleavingConstraintGeneration}. If no configuration is provided,
 * the {@link DelegatingInterleavingConfigurationProvider} is used.
 */
public class GeneratingInterleavingConfigurationLoader implements
        Loader<InterleavingExecutionConfiguration> {
    
    @Override
    public InterleavingExecutionConfiguration load(Method method) {
        final InterleavingConfigurationProvider provider = getProvider(method);
        final InterleavingExecutionConfiguration configuration = provider.provide(method);
        
        if (!configuration.isGenerating()) {
            throw new IllegalArgumentException("Generating configuration required");
        }
        
        return configuration;
    }
    
    private InterleavingConfigurationProvider getProvider(Method method) {
        Optional<EnableInterleavingConstraintGeneration> annotation = AnnotationSupport.findAnnotation(
                method, EnableInterleavingConstraintGeneration.class);
    
        if (!annotation.isPresent() || annotation.get().value().equals("")) {
            return new DelegatingInterleavingConfigurationProvider(true);
        } else {
            Optional<InterleavingConfigurationProvider> optProvider = annotation
                    .map(EnableInterleavingConstraintGeneration::getProvider)
                    .map(ReflectionSupport::newInstance)
                    .map(instance -> AnnotationConsumerInitializer.initialize(method, instance))
                    .map(InterleavingConfigurationProvider.class::cast);
        
            if (optProvider.isPresent()) {
                return optProvider.get();
            } else {
                throw new Coffee4JException("No InterleavingConfigurationProvider is present!");
            }
        }
    }
    
}
