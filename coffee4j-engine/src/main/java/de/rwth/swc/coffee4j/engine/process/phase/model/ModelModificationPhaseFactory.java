package de.rwth.swc.coffee4j.engine.process.phase.model;

/**
 * Factory for creating {@link ModelModificationPhase} instances with a given context.
 */
public interface ModelModificationPhaseFactory {
    
    /**
     * Creates a {@link ModelModificationPhase} with will work with the given context.
     *
     * @param context all necessary information for a {@link ModelModificationPhase}. Must never be {@code null}
     * @return the created phase. Will never be {@code null}
     */
    ModelModificationPhase create(ModelModificationContext context);
    
}
