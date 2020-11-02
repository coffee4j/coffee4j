package de.rwth.swc.coffee4j.engine.process.phase;

/**
 * Manages the execution of a phase.
 * Can be compared to a filter in the pipes and filters pattern.
 *
 * @param <I> the input type of the phase
 * @param <O> the output type of the phase
 */
@FunctionalInterface
public interface Phase<I, O> {

    /**
     * Executes the phase.
     * Transform some input int some output
     *
     * @param input the input of the phase
     * @return the output of the phase
     */
    O execute(I input);
}
