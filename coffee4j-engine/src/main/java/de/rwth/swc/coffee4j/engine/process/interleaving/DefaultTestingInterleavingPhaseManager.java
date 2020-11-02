package de.rwth.swc.coffee4j.engine.process.interleaving;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;

import java.util.Map;

/**
 * Default implementation managing an interleaving combinatorial test by extending {@link AbstractInterleavingPhaseManager}.
 */
public class DefaultTestingInterleavingPhaseManager extends AbstractInterleavingPhaseManager {
    
    public DefaultTestingInterleavingPhaseManager(InterleavingPhaseManagerConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void executeClassificationPhase(Map<Combination, TestResult> errorConstraintExceptionCausingTestInputs) {
        // Nothing to do here as no exception-inducing combinations present
    }
}
