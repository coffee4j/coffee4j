package io.github.coffee4j.engine.process.phase.interleaving.classification;

import io.github.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationContext;

/**
 * Interface for a Factory creating an {@link InterleavingClassificationPhase}.
 */
public interface InterleavingClassificationPhaseFactory {
    InterleavingClassificationPhase create(InterleavingGenerationContext context);
}
