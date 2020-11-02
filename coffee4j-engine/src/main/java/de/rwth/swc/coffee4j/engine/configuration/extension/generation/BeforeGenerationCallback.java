package de.rwth.swc.coffee4j.engine.configuration.extension.generation;

import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;

/**
 * Represents a phase callback before the generation phase
 */
@FunctionalInterface
public interface BeforeGenerationCallback extends Extension {

    /**
     * Execute the callback
     */
    void beforeGeneration();
}
