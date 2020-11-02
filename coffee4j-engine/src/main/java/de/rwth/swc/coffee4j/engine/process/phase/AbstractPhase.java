package de.rwth.swc.coffee4j.engine.process.phase;

/**
 * An abstract class of a {@link Phase} providing a convenience constructor
 *
 * @param <C> the type of the context required to execute the phase
 * @param <I> the input of the phase
 * @param <O> the output of the phase
 */
public abstract class AbstractPhase<C extends PhaseContext, I, O> implements Phase<I, O> {

    protected final C context;

    protected AbstractPhase(C context) {
        this.context = context;
    }
}
