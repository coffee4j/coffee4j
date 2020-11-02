package de.rwth.swc.coffee4j.junit.engine.annotation.test.model;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Class for loading the defined testModel for a {@link CombinatorialTest}.
 * The default provider is based on {@link CombinatorialTest#inputParameterModel} but
 * can be overridden with {@link InputParameterModelSource}.
 */
public class InputParameterModelLoader implements Loader<InputParameterModel> {

    @Override
    public InputParameterModel load(Method method) {
        final Supplier<Optional<Class<? extends InputParameterModelProvider>>> defaultProviderSource
                = () -> Optional.of(MethodBasedInputParameterModelProvider.class);

        final Optional<Class<? extends InputParameterModelProvider>> providerSource = AnnotationSupport
                .findAnnotation(method, InputParameterModelSource.class)
                .map(InputParameterModelSource::value);

        return providerSource
                .or(defaultProviderSource)
                .map(ReflectionSupport::newInstance)
                .map(provider -> AnnotationConsumerInitializer.initialize(method, provider))
                .map(provider -> provider.provide(method))
                .orElseThrow(() -> new Coffee4JException("A model has to be provided for a combinatorial test"));
    }
    
}
