package de.rwth.swc.coffee4j.engine.process.phase.sequential.characterization;

import de.rwth.swc.coffee4j.engine.process.phase.sequential.generation.SequentialGenerationContext;

public interface FaultCharacterizationPhaseFactory {

    FaultCharacterizationPhase create(SequentialGenerationContext generationContext);

}
