package de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation;

/**
 * Interface for a Factory creating an {@link InterleavingGenerationPhase}.
 */
public interface InterleavingGenerationPhaseFactory {
    
    InterleavingGenerationPhase create(InterleavingGenerationContext context);
    
}
