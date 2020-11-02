package de.rwth.swc.coffee4j.engine.configuration.extension.characterization;

import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;

import java.util.List;

/**
 * Represents a phase callback after the fault characterization phase
 */
@FunctionalInterface
public interface AfterFaultCharacterizationCallback extends Extension {

    /**
     * Executes the callback with the supplied {@link Combination combinations}
     *
     * @param additionalTestInput the additional combinations generated by the fault characterization phase
     */
    void afterFaultCharacterization(List<Combination> additionalTestInput);

}
