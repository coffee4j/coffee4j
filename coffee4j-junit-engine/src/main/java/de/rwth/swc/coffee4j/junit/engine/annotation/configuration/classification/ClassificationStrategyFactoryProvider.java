package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.classification;

import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedProvider;

/**
 * This interface is responsible for providing a {@link ClassificationStrategyFactory}. It can be registered
 * using the {@link EnableClassification} annotation.
 */
@FunctionalInterface
public interface ClassificationStrategyFactoryProvider extends MethodBasedProvider<ClassificationStrategyFactory> {
}
