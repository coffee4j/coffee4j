package io.github.coffee4j.algorithmic.sequential.characterization;

import io.github.coffee4j.algorithmic.model.TestModel;
import io.github.coffee4j.algorithmic.report.Reporter;
import io.github.coffee4j.algorithmic.util.Preconditions;

/**
 * Class containing all information needed to perform fault characterization for combinatorial tests.
 */
public record FaultCharacterizationConfiguration(TestModel model, Reporter reporter) {

    /**
     * Creates a new configuration out of an IPM and a reporter. It is not guaranteed that the
     * constraints checker in the model will be respected by an algorithm.
     *
     * @param model    containing all parameters of the combinatorial test
     * @param reporter to give information to users during fault characterization execution
     */
    public FaultCharacterizationConfiguration {
        Preconditions.notNull(model);
        Preconditions.notNull(reporter);
    }
}
