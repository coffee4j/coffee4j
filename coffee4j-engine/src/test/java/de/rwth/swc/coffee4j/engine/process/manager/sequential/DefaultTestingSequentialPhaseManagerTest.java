package de.rwth.swc.coffee4j.engine.process.manager.sequential;

import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.SequentialCombinatorialTestManager;
import de.rwth.swc.coffee4j.engine.MockingTest;
import de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.execution.TestInputExecutor;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.manager.ConflictDetector;
import de.rwth.swc.coffee4j.engine.process.manager.PhaseManager;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionPhase;
import de.rwth.swc.coffee4j.engine.process.phase.model.ModelModificationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.characterization.FaultCharacterizationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.classification.SequentialClassificationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.generation.SequentialGenerationPhase;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration.testMethodConfiguration;
import static de.rwth.swc.coffee4j.engine.process.manager.sequential.SequentialPhaseManagerConfiguration.phaseManagerConfiguration;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultTestingSequentialPhaseManagerTest implements MockingTest {
    
    final SequentialExecutionConfiguration executionConfiguration = mock(SequentialExecutionConfiguration.class);
    final InputParameterModel model = mock(InputParameterModel.class);
    final Combination combinationOne = mock(Combination.class);
    final Combination combinationTwo = mock(Combination.class);
    final Combination combinationThree = mock(Combination.class);
    final Combination combinationFour = mock(Combination.class);
    final List<Combination> firstCombinationList = List.of(combinationOne, combinationTwo);
    final List<Combination> secondCombinationList = List.of(combinationThree, combinationFour);
    final TestResult testResultOne = mock(TestResult.class);
    final TestResult testResultTwo = mock(TestResult.class);
    final TestResult testResultThree = mock(TestResult.class);
    final TestResult testResultFour = mock(TestResult.class);
    final Map<Combination, TestResult> firstTestResults = Map.of(combinationOne, testResultOne, combinationTwo, testResultTwo);
    final Map<Combination, TestResult>  secondTestResults = Map.of(combinationThree, testResultThree, combinationFour, testResultFour);
    final TestMethodConfiguration testMethodConfiguration = testMethodConfiguration()
            .inputParameterModel(model)
            .testExecutor(mock(TestInputExecutor.class))
            .build();
    final ModelConverter modelConverter = mock(ModelConverter.class);
    final ModelModificationPhase modelModificationPhase = mock(ModelModificationPhase.class);
    
    final SequentialGenerationPhase generationPhase = mock(SequentialGenerationPhase.class);
    final ExecutionPhase executionPhase = mock(ExecutionPhase.class);
    final FaultCharacterizationPhase characterizationPhase = mock(FaultCharacterizationPhase.class);
    final SequentialClassificationPhase sequentialClassificationPhase = mock(SequentialClassificationPhase.class);
    
    SequentialPhaseManagerConfiguration configuration;

    void prepare() {
        when(generationPhase.execute(model))
                .thenReturn(firstCombinationList);
        when(executionPhase.execute(firstCombinationList))
                .thenReturn(firstTestResults);
        when(executionPhase.execute(secondCombinationList))
                .thenReturn(secondTestResults);
        when(executionPhase.execute(Collections.singletonList(combinationOne)))
                .thenReturn(Map.of(combinationOne, testResultOne));
        when(characterizationPhase.execute(firstTestResults))
                .thenReturn(secondCombinationList);
        when(characterizationPhase.execute(secondTestResults))
                .thenReturn(Collections.emptyList());
        when(sequentialClassificationPhase.initialize(new HashMap<>()))
                .thenReturn(combinationOne);
        when(sequentialClassificationPhase.execute(Map.of(combinationOne, testResultOne)))
                .thenReturn(null);
    
        when(executionConfiguration.getExecutionMode())
                .thenReturn(ExecutionMode.EXECUTE_ALL);
        when(executionConfiguration.getModelConverterFactory())
                .thenReturn(context -> modelConverter);
        when(executionConfiguration.getManagerFactory())
                .thenReturn((configuration, model) -> mock(SequentialCombinatorialTestManager.class));
        when(modelModificationPhase.execute(any()))
                .thenReturn(model);
        
        configuration = phaseManagerConfiguration()
                .testMethodConfiguration(testMethodConfiguration)
                .executionConfiguration(executionConfiguration)
                .extensionExecutorFactory(extensions -> mock(ExtensionExecutor.class))
                .generationPhaseFactory(context -> generationPhase)
                .executionPhaseFactory(context -> executionPhase)
                .faultCharacterizationPhaseFactory(context -> characterizationPhase)
                .classificationPhaseFactory(context -> sequentialClassificationPhase)
                .conflictDetectorFactory((configuration, model) -> mock(ConflictDetector.class))
                .modelModificationPhaseFactory(context -> modelModificationPhase)
                .build();
    }

    @Test
    void runDefaultTestingSequentialPhaseManager() {
        prepare();

        final PhaseManager manager = new DefaultTestingSequentialPhaseManager(configuration);
        manager.run();

        InOrder inOrder = Mockito.inOrder(generationPhase, executionPhase, characterizationPhase);

        inOrder.verify(generationPhase).execute(model);

        inOrder.verify(executionPhase).execute(firstCombinationList);
        inOrder.verify(characterizationPhase).execute(firstTestResults);

        inOrder.verify(executionPhase).execute(secondCombinationList);
        inOrder.verify(characterizationPhase).execute(secondTestResults);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void runDefaultGeneratingSequentialPhaseManager() {
        prepare();

        final PhaseManager manager = new DefaultGeneratingSequentialPhaseManager(configuration);
        manager.run();

        InOrder inOrder = Mockito.inOrder(generationPhase, executionPhase, characterizationPhase, sequentialClassificationPhase);

        inOrder.verify(generationPhase).execute(model);

        inOrder.verify(executionPhase).execute(firstCombinationList);
        inOrder.verify(characterizationPhase).execute(firstTestResults);

        inOrder.verify(executionPhase).execute(secondCombinationList);
        inOrder.verify(characterizationPhase).execute(secondTestResults);

        inOrder.verify(sequentialClassificationPhase).initialize(new HashMap<>());
        inOrder.verify(executionPhase).execute(List.of(combinationOne));
        inOrder.verify(sequentialClassificationPhase).execute(Map.of(combinationOne, testResultOne));

        inOrder.verifyNoMoreInteractions();
    }
    
}
