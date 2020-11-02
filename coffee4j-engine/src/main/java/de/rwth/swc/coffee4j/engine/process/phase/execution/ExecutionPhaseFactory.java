package de.rwth.swc.coffee4j.engine.process.phase.execution;

/**
 * A factory for the {@link ExecutionPhase}
 */
@FunctionalInterface
public interface ExecutionPhaseFactory {

    /**
     * Creates a new {@link ExecutionPhase} configured with an {@link ExecutionContext}
     *
     * @param executionContext the {@link ExecutionContext} with which to configure the {@link ExecutionPhase}
     * @return the created {@link ExecutionPhase}
     */
    ExecutionPhase create(ExecutionContext executionContext);

}
