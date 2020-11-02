package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration;

import de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedProvider;
import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;

/**
 * An {@code ConfigurationProvider} is responsible for {@linkplain #provide providing} exactly one
 * {@link SequentialExecutionConfiguration} for use in a
 * {@link CombinatorialTest}.
 * <p>
 * To register a {@link ConfigurationProvider}, use the {@link ConfigurationSource} annotation as demonstrated by
 * {@link ConfigurationFromMethod}.
 * <p>
 * Implementations must provide a no-args constructor.
 * <p>
 * This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsProvider} from the
 * junit-jupiter-params project.
 */
@FunctionalInterface
public interface ConfigurationProvider extends MethodBasedProvider<SequentialExecutionConfiguration> {
}
