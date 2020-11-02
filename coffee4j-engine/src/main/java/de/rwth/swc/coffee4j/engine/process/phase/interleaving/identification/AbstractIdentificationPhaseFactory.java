package de.rwth.swc.coffee4j.engine.process.phase.interleaving.identification;

import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationContext;

/**
 * Interface for a Factory creating an {@link AbstractIdentificationPhase}.
 */
public interface AbstractIdentificationPhaseFactory {
    AbstractIdentificationPhase create(InterleavingGenerationContext context);
}
