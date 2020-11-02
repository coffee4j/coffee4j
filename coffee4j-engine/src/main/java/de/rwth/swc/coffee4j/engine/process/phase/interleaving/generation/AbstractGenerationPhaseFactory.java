package de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation;

/**
 * Interface for a Factory creating an {@link AbstractGenerationPhase}.
 */
public interface AbstractGenerationPhaseFactory {
    AbstractGenerationPhase create(InterleavingGenerationContext context);
}
