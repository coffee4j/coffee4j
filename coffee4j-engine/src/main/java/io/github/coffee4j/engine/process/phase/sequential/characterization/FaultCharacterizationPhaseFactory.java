package io.github.coffee4j.engine.process.phase.sequential.characterization;

import io.github.coffee4j.engine.process.phase.sequential.generation.SequentialGenerationContext;

public interface FaultCharacterizationPhaseFactory {

    FaultCharacterizationPhase create(SequentialGenerationContext generationContext);

}
