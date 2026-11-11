package io.github.coffee4j.junit.engine.annotation.configuration.classification;

import io.github.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import io.github.coffee4j.algorithmic.classification.NoOpClassificationStrategy;
import io.github.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import io.github.coffee4j.junit.engine.annotation.Loader;
import io.github.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;

/**
 * Class for loading the defined classification strategy using {@link EnableClassification} for a
 * {@link CombinatorialTest}.
 */
public class ClassificationStrategyFactoryLoader implements Loader<ClassificationStrategyFactory> {

    @Override
    public ClassificationStrategyFactory load(Method method) {
        return AnnotationSupport.findAnnotation(method, EnableClassification.class)
                .map(annotation -> new ConstructorBasedClassificationStrategyProvider())
                .map(provider -> AnnotationConsumerInitializer.initialize(method, provider))
                .map(provider -> provider.provide(method))
                .orElse(NoOpClassificationStrategy.noOpClassificationStrategy());
    }
}
