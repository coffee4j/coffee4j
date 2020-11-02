package de.rwth.swc.coffee4j.engine.process.phase.sequential.generation;

/**
 * Factory for a {@link SequentialGenerationPhase}
 */
@FunctionalInterface
public interface SequentialGenerationPhaseFactory {

    /**
     * Creates a new {@link SequentialGenerationPhase} configured with a supplied {@link SequentialGenerationContext}
     *
     * @param generationContext the {@link SequentialGenerationContext} with which to configure the {@link SequentialGenerationContext}
     * @return the created {@link SequentialGenerationPhase}
     */
    SequentialGenerationPhase create(SequentialGenerationContext generationContext);

}
