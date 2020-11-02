package de.rwth.swc.coffee4j.engine.process.interleaving;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.classification.InterleavingClassificationPhase;

import java.util.Map;

/**
 * Default implementation managing the generation of error-constraints by extending {@link AbstractInterleavingPhaseManager}.
 */
public class DefaultGeneratingInterleavingPhaseManager extends AbstractInterleavingPhaseManager {
    
    private InterleavingClassificationPhase classificationPhase;

    public DefaultGeneratingInterleavingPhaseManager(InterleavingPhaseManagerConfiguration configuration) {
        super(configuration);
    }
    
    @Override
    protected void createPhases() {
        super.createPhases();
        
        classificationPhase = configuration.getClassificationPhaseFactory()
                .create(generationContext);
    }
    
    @Override
    protected void executeClassificationPhase(Map<Combination, TestResult> errorConstraintExceptionCausingTestInputs) {
        Map<Combination, TestResult> executionResult;
        Combination nextTestInput = classificationPhase.initialize(errorConstraintExceptionCausingTestInputs);

        while (nextTestInput != null) {
            executionResult = runTestInput(nextTestInput);
            nextTestInput = classificationPhase.execute(executionResult);
        }
    }
}
