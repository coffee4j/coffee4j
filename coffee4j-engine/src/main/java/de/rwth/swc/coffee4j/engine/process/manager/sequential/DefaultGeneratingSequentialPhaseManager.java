package de.rwth.swc.coffee4j.engine.process.manager.sequential;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.process.manager.sequential.AbstractSequentialPhaseManager;
import de.rwth.swc.coffee4j.engine.process.manager.sequential.SequentialPhaseManagerConfiguration;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.classification.SequentialClassificationPhase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default implementation managing the generation of error-constraints by extending {@link AbstractSequentialPhaseManager}.
 */
public class DefaultGeneratingSequentialPhaseManager extends AbstractSequentialPhaseManager {
    
    private final Map<Combination, TestResult> errorConstraintExceptionCausingTestInputs = new HashMap<>();
    
    private SequentialClassificationPhase classificationPhase;
    
    /**
     * Creates a new {@link AbstractSequentialPhaseManager} with the supplied configuration
     *
     * @param configuration the configuration for the phase manager
     */
    public DefaultGeneratingSequentialPhaseManager(SequentialPhaseManagerConfiguration configuration) {
        super(configuration);
    }
    
    @Override
    protected void createTestPhases() {
        super.createTestPhases();
        
        classificationPhase = configuration.getClassificationPhaseFactory().create(generationContext);
    }
    
    @Override
    protected void collectErrorConstraintExceptionCausingTestInputs(Map<Combination, TestResult> executionResults) {
        errorConstraintExceptionCausingTestInputs.putAll(executionResults
                .entrySet()
                .stream()
                .filter(testInput -> testInput.getValue().isExceptionalSuccessful())
                .filter(testInput -> testInput.getValue().getResultValue().orElse(null) instanceof ErrorConstraintException)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Override
    protected void executeClassificationPhase() {
        Map<Combination, TestResult> executionResult;
        Combination nextTestInput = classificationPhase.initialize(errorConstraintExceptionCausingTestInputs);

        while (nextTestInput != null) {
            executionResult = executionPhase.execute(List.of(nextTestInput));
            nextTestInput = classificationPhase.execute(executionResult);
        }
    }
}
