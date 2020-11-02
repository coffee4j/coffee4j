package de.rwth.swc.coffee4j.engine.process.report.util;

import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;

/**
 * Interface used by {@link ExecutionReporter}s to format found exception-inducing
 * combinations. One format might be java-code that can directly be integrated into an
 * {@link InputParameterModel}.
 */
public interface CombinationFormatter {
    /**
     * Formats a given combination.
     *
     * @param combination the combination to format.
     * @return returns the formatted combination. Default: no formatting.
     */
    default String format(Combination combination){
        return combination.toString();
    }
}
