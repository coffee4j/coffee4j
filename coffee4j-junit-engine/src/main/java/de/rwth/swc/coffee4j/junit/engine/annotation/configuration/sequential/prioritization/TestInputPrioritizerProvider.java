package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.prioritization;

import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedProvider;

/**
 * Provider for {@link TestInputPrioritizer} used in the execution of {@link CombinatorialTest} methods.
 *
 * <p>To register a {@link TestInputPrioritizerProvider} use the {@link TestInputPrioritizerSource} annotation.
 *
 * <p>Implementations must provide a no-args constructor.
 *
 * <p>This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsProvider} from the
 * junit-jupiter-params project.
 */
public interface TestInputPrioritizerProvider extends MethodBasedProvider<TestInputPrioritizer> {
}
