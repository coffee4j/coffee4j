package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation;

import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedProvider;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;

import java.util.Collection;

/**
 * An {@code GeneratorProvider} is responsible for {@linkplain #provide providing}
 * an arbitrary number of {@link TestInputGroupGenerator} implementations (even none is allowed) for a
 * {@link CombinatorialTest}.
 * <p>
 * To register a {@link GeneratorProvider}, use the {@link GeneratorSource}
 * annotation as demonstrated by {@link EnableGeneration}.
 * <p>
 * Implementations must provide a no-args constructor.
 * <p>
 * This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsProvider} from the
 * junit-jupiter-params project.
 */
@FunctionalInterface
public interface GeneratorProvider extends MethodBasedProvider<Collection<TestInputGroupGenerator>> {
}
