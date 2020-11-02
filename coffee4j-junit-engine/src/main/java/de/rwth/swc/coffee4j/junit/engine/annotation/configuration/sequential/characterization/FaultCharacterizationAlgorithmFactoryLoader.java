package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization;

import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.DelegatingConfigurationProvider;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Class for loading the defined fault characterization characterization for a
 * {@link CombinatorialTest}. At most one annotation of
 * {@link FaultCharacterizationAlgorithmFactorySource} is needed for this to find. Since
 * {@link FaultCharacterizationAlgorithmFactorySource} is inherited, any inheriting annotation such as
 * {@link EnableFaultCharacterization} can also be found by this loader.
 * If no annotation is given, the an empty optional is returned and no fault
 * characterization will be used in the corresponding {@link CombinatorialTest}.
 * <p>
 * This is used by {@link DelegatingConfigurationProvider}
 * to provide a configuration.
 */
public class FaultCharacterizationAlgorithmFactoryLoader implements
        Loader<Optional<FaultCharacterizationAlgorithmFactory>> {

    @Override
    public Optional<FaultCharacterizationAlgorithmFactory> load(Method method) {
        return AnnotationSupport.findAnnotation(method, FaultCharacterizationAlgorithmFactorySource.class)
                .map(FaultCharacterizationAlgorithmFactorySource::value)
                .map(ReflectionSupport::newInstance)
                .map(provider -> AnnotationConsumerInitializer.initialize(method, provider))
                .map(provider -> provider.provide(method));
    }
}
