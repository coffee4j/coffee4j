package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration;

import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedProvider;

/**
 * This interface is responsible for providing {@link InterleavingExecutionConfiguration}.
 * It can be registered using the {@link InterleavingConfigurationSource} annotation.
 */
public interface InterleavingConfigurationProvider extends
        MethodBasedProvider<InterleavingExecutionConfiguration> {
}