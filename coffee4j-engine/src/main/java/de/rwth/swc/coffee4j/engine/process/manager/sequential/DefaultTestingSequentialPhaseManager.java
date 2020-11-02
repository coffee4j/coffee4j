package de.rwth.swc.coffee4j.engine.process.manager.sequential;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;

import java.util.Map;

/**
 * Default implementation managing an sequential combinatorial test by extending {@link AbstractSequentialPhaseManager}.
 */
public class DefaultTestingSequentialPhaseManager extends AbstractSequentialPhaseManager {
    
    /**
     * Creates a new {@link AbstractSequentialPhaseManager} with the supplied configuration
     *
     * @param configuration the configuration for the phase manager
     */
    public DefaultTestingSequentialPhaseManager(SequentialPhaseManagerConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void collectErrorConstraintExceptionCausingTestInputs(Map<Combination, TestResult> executionResults) {
        // Nothing to do here as no exception-inducing combinations present
    }

    @Override
    protected void executeClassificationPhase() {
        // Nothing to do here as no exception-inducing combinations present
    }
    
}
