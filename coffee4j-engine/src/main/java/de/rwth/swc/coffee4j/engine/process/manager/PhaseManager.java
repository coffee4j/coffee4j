package de.rwth.swc.coffee4j.engine.process.manager;

import de.rwth.swc.coffee4j.engine.process.phase.Phase;

/**
 * Manages different {@link Phase}s
 */
@FunctionalInterface
public interface PhaseManager {

    /**
     * Runs the automation of the phases defined in the manager
     */
    void run();
}
