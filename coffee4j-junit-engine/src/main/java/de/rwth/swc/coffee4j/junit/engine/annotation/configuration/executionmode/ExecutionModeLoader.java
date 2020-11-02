package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.executionmode;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Class for loading the defined {@link ExecutionMode} for a {@link CombinatorialTest}.
 * The default provider is based on {@link CombinatorialTest#executionMode()} but
 * can be overridden with {@link ExecutionModeSource}.
 */
public class ExecutionModeLoader implements Loader<ExecutionMode> {
    
    @Override
    public ExecutionMode load(Method method) {
        final Supplier<Optional<Class<? extends ExecutionModeProvider>>> defaultProviderSource
                = () -> Optional.of(CombinatorialTestAnnotationBasedExecutionModeProvider.class);
        
        final Optional<Class<? extends ExecutionModeProvider>> providerSource = AnnotationSupport
                .findAnnotation(method, ExecutionModeSource.class)
                .map(ExecutionModeSource::value);
        
        return providerSource
                .or(defaultProviderSource)
                .map(ReflectionSupport::newInstance)
                .map(provider -> AnnotationConsumerInitializer.initialize(method, provider))
                .map(provider -> provider.provide(method))
                .orElseThrow(() -> new Coffee4JException(
                        "An execution mode has to be provided for a combinatorial test"));
    }
    
}
