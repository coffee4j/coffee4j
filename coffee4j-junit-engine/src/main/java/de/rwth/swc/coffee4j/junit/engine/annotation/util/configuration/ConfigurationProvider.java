package de.rwth.swc.coffee4j.junit.engine.annotation.util.configuration;

import de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedProvider;

/**
 * A provider for configurations which can be registered using {@link ConfigurationSource}.
 *
 * @param <T> the type of the configuration which is provided
 */
@FunctionalInterface
public interface ConfigurationProvider<T> extends MethodBasedProvider<T> {
}
