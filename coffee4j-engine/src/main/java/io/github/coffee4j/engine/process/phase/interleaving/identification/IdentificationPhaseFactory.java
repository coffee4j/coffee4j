package io.github.coffee4j.engine.process.phase.interleaving.identification;

import io.github.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationContext;

/**
 * Interface for a Factory creating an {@link IdentificationPhase}.
 */
public interface IdentificationPhaseFactory {
    
    IdentificationPhase create(InterleavingGenerationContext context);
}
