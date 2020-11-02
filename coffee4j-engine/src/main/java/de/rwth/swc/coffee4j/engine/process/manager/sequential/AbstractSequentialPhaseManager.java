package de.rwth.swc.coffee4j.engine.process.manager.sequential;

import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.manager.ConflictDetector;
import de.rwth.swc.coffee4j.engine.process.manager.PhaseManager;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionContext;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionPhase;
import de.rwth.swc.coffee4j.engine.process.phase.model.ModelModificationContext;
import de.rwth.swc.coffee4j.engine.process.phase.model.ModelModificationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.characterization.FaultCharacterizationPhase;
import de.rwth.swc.coffee4j.engine.report.DelegatingSequentialExecutionReporter;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.generation.SequentialGenerationContext;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.generation.SequentialGenerationPhase;

import java.util.List;
import java.util.Map;

/**
 * Abstract class implementing the {@link PhaseManager} Interface for Sequential CT. It provides general functionality
 * and fields used by all sub-classes.
 *
 * Manages the phases: initial generation, execution and fault characterization
 */
public abstract class AbstractSequentialPhaseManager implements PhaseManager {
    
    protected SequentialPhaseManagerConfiguration configuration;
    
    protected ExecutionContext executionContext;
    protected SequentialGenerationContext generationContext;
    
    protected ExtensionExecutor extensionExecutor;
    protected ExecutionPhase executionPhase;
    protected SequentialGenerationPhase generationPhase;
    protected FaultCharacterizationPhase faultCharacterizationPhase;

    /**
     * Creates a new {@link AbstractSequentialPhaseManager} with the supplied configuration
     *
     * @param configuration the configuration for the phase manager
     */
    public AbstractSequentialPhaseManager(SequentialPhaseManagerConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Automates the combinatorial process.
     *
     * Executes the execution callbacks before and after the test class.
     * The rest of the combinatorial process is described as in the thesis
     * "Development of an Automated Combinatorial Testing Framework"
     */
    @Override
    public void run() {
        executeModelModificationPhase();
        createTestContexts();
        detectConflicts();
        createTestPhases();
        
        List<Combination> currentCombinations = generationPhase
                .execute(configuration.getTestMethodConfiguration().getInputParameterModel());
        
        do {
            // Execution Phase
            final Map<Combination, TestResult> executionResults = executionPhase.execute(currentCombinations);
            
            // Even if the execution phase was FAIL_FAST, it should now change so that all fault characterization test
            // inputs are executed as expected
            executionPhase.setExecutionMode(ExecutionMode.EXECUTE_ALL);

            // collect all test inputs triggering ErrorConstraintExceptions
            collectErrorConstraintExceptionCausingTestInputs(executionResults);

            // Failure Characterization Phase
            currentCombinations = faultCharacterizationPhase.execute(executionResults);
        } while(!currentCombinations.isEmpty());

        executeClassificationPhase();
    }
    
    private void executeModelModificationPhase() {
        extensionExecutor = configuration.getExtensionExecutorFactory().create(configuration.getExtensions());
        
        final ModelModificationContext context = new ModelModificationContext(extensionExecutor,
                new DelegatingSequentialExecutionReporter(configuration.getExecutionConfiguration().getExecutionReporters()));
        final ModelModificationPhase phase = configuration.getModelModificationPhaseFactory().create(context);
        final InputParameterModel model = configuration.getTestMethodConfiguration().getInputParameterModel();
        final InputParameterModel modifiedModel = phase.execute(model);
        
        configuration = configuration.toBuilder()
                .testMethodConfiguration(configuration.getTestMethodConfiguration().toBuilder()
                        .inputParameterModel(modifiedModel)
                        .build())
                .build();
    }
    
    private void createTestContexts() {
        executionContext = ExecutionContext.createExecutionContext(
                extensionExecutor,
                configuration.getTestMethodConfiguration(),
                configuration.getExecutionConfiguration().getExecutionReporters());
        
        generationContext = new SequentialGenerationContext(
                configuration.getExecutionConfiguration(),
                configuration.getTestMethodConfiguration().getInputParameterModel(),
                extensionExecutor);
    }
    
    private void detectConflicts() {
        final ConflictDetector conflictDetector = configuration.getConflictDetectorFactory()
                .create(configuration.getExecutionConfiguration().getConflictDetectionConfiguration(),
                        generationContext.getModelConverter());
    
        conflictDetector.diagnoseConstraints();
    }
    
    protected void createTestPhases() {
        executionPhase = configuration.getExecutionPhaseFactory().create(executionContext);
        executionPhase.setExecutionMode(configuration.getExecutionConfiguration().getExecutionMode());
        
        generationPhase = configuration.getGenerationPhaseFactory()
                .create(generationContext);
        
        faultCharacterizationPhase = configuration.getFaultCharacterizationPhaseFactory()
                .create(generationContext);
    }

    /**
     * Template method implemented by error-constraint generating managers.
     * @param executionResults map containing all executed {@link Combination}s and the corresponding {@link TestResult}s
     */
    protected abstract void collectErrorConstraintExceptionCausingTestInputs(Map<Combination, TestResult> executionResults);

    /**
     * Template method implemented by error-constraint generating managers.
     */
    protected abstract void executeClassificationPhase();
    
}
