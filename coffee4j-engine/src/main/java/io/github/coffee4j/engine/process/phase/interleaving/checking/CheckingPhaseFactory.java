package io.github.coffee4j.engine.process.phase.interleaving.checking;

import io.github.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationContext;

/**
 * Interface for a Factory creating a {@link CheckingPhase}.
 */
public interface CheckingPhaseFactory {
    CheckingPhase create(InterleavingGenerationContext context);
}
