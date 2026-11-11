package io.github.coffee4j.algorithmic.interleaving.identification;

/**
 * Factory for creating an {@link IdentificationStrategy}.
 */
@FunctionalInterface
public interface IdentificationStrategyFactory {
    IdentificationStrategy create(IdentificationConfiguration configuration);
}
