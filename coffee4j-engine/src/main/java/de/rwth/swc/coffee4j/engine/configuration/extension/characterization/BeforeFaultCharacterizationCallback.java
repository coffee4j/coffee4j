package de.rwth.swc.coffee4j.engine.configuration.extension.characterization;

import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;

import java.util.Map;

/**
 * Represents a phase callback before the fault characterization phase
 */
@FunctionalInterface
public interface BeforeFaultCharacterizationCallback extends Extension {

    /**
     * Executes the callback with supplied test results
     *
     * @param combinationTestResultMap the summarized test results of the execution phase
     */
    void beforeFaultCharacterization(Map<Combination, TestResult> combinationTestResultMap);

}
