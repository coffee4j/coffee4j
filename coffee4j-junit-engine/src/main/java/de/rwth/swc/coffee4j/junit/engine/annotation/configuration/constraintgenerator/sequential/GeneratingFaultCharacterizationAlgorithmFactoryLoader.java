package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.sequential;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization.FaultCharacterizationAlgorithmFactoryProvider;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Class for loading the defined generating fault characterization characterization for a
 * {@link CombinatorialTest}.
 */
public class GeneratingFaultCharacterizationAlgorithmFactoryLoader implements Loader<FaultCharacterizationAlgorithmFactory> {

    @Override
    public FaultCharacterizationAlgorithmFactory load(Method method) {
        FaultCharacterizationAlgorithmFactoryProvider provider;
        Optional<EnableSequentialConstraintGeneration> annotation = AnnotationSupport.findAnnotation(method, EnableSequentialConstraintGeneration.class);

        Optional<FaultCharacterizationAlgorithmFactoryProvider> optProvider = annotation
                .map(EnableSequentialConstraintGeneration::getProvider)
                .map(ReflectionSupport::newInstance)
                .map(instance -> AnnotationConsumerInitializer.initialize(method, instance))
                .map(FaultCharacterizationAlgorithmFactoryProvider.class::cast);

        if (optProvider.isPresent()) {
            provider = optProvider.get();
        } else {
            throw new Coffee4JException("No FaultCharacterizationAlgorithmFactoryProvider is present!");
        }

        return provider.provide(method);
    }
    
}
