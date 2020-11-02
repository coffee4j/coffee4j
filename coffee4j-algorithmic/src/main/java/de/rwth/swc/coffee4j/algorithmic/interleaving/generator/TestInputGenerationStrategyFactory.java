package de.rwth.swc.coffee4j.algorithmic.interleaving.generator;

/**
 * Factory for creating a {@link TestInputGenerationStrategy}.
 */
@FunctionalInterface
public interface TestInputGenerationStrategyFactory {
    TestInputGenerationStrategy create(TestInputGenerationConfiguration configuration);
}
