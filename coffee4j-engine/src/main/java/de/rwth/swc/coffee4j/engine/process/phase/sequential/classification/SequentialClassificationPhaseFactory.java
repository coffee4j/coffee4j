package de.rwth.swc.coffee4j.engine.process.phase.sequential.classification;

import de.rwth.swc.coffee4j.engine.process.phase.sequential.generation.SequentialGenerationContext;

/**
 * Interface for a Factory creating an {@link SequentialClassificationPhase}.
 */
public interface SequentialClassificationPhaseFactory {
    
    SequentialClassificationPhase create(SequentialGenerationContext context);
    
}
