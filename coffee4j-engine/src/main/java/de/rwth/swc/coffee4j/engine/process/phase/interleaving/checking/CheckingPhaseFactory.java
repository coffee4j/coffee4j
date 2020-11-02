package de.rwth.swc.coffee4j.engine.process.phase.interleaving.checking;

import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationContext;

/**
 * Interface for a Factory creating a {@link CheckingPhase}.
 */
public interface CheckingPhaseFactory {
    CheckingPhase create(InterleavingGenerationContext context);
}
