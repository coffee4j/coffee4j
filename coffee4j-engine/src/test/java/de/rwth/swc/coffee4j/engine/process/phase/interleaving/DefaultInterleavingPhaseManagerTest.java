package de.rwth.swc.coffee4j.engine.process.phase.interleaving;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.MockingTest;
import de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.execution.TestInputExecutor;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverterFactory;
import de.rwth.swc.coffee4j.engine.process.extension.DefaultExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.interleaving.*;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionContext;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionPhase;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.algorithmic.interleaving.feedback.FeedbackCheckingStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.DefaultGeneratingInterleavingManager;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingCombinatorialTestManager;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingManagerFactory;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.checking.CheckingPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.checking.CheckingPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.classification.InterleavingClassificationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.classification.InterleavingClassificationPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.identification.IdentificationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.identification.IdentificationPhaseFactory;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration.testMethodConfiguration;
import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.process.interleaving.InterleavingPhaseManagerConfiguration.phaseManagerConfiguration;
import static org.mockito.Mockito.*;

class DefaultInterleavingPhaseManagerTest implements MockingTest {
    
    final Combination combination1 = mock(Combination.class);
    final Map<Combination, TestResult> result1 = new HashMap<>();
    final Combination combination2 = mock(Combination.class);
    final Map<Combination, TestResult> result2 = new HashMap<>();
    final Combination combination3 = mock(Combination.class);
    final Map<Combination, TestResult> result3 = new HashMap<>();
    final Map<Combination, TestResult> errorConstraintExceptionCausingTestInputs = new HashMap<>();
    final DefaultExtensionExecutor extensionExecutor = mock(DefaultExtensionExecutor.class);
    final ExecutionContext executionContext = mock(ExecutionContext.class);
    final InputParameterModel model = mock(InputParameterModel.class);
    final TestMethodConfiguration testMethodConfiguration = testMethodConfiguration()
            .inputParameterModel(model)
            .testExecutor(mock(TestInputExecutor.class))
            .build();
    final ExecutionPhase executionPhase = mock(ExecutionPhase.class);
    final InterleavingGenerationPhase generationPhase = mock(InterleavingGenerationPhase.class);
    final IdentificationPhase identificationPhase = mock(IdentificationPhase.class);
    final CheckingPhase checkingPhase = mock(CheckingPhase.class);
    final InterleavingClassificationPhase classificationPhase = mock(InterleavingClassificationPhase.class);
    final InterleavingExecutionConfiguration executionConfiguration = mock(InterleavingExecutionConfiguration.class);
    final ModelConverterFactory modelConverterFactory = mock(ModelConverterFactory.class);
    final ModelConverter modelConverter = mock(ModelConverter.class);
    
    void prepare() {
        result1.put(combination1, TestResult.success());
        result2.put(combination2, TestResult.failure(new ErrorConstraintException()));
        result3.put(combination3, TestResult.success());
        errorConstraintExceptionCausingTestInputs.put(combination2, result2.get(combination2));
        
        when(executionContext.getExtensionExecutor())
                .thenReturn(extensionExecutor);
        when(executionPhase.execute(Collections.singletonList(combination1)))
                .thenReturn(result1);
        when(executionPhase.execute(Collections.singletonList(combination2)))
                .thenReturn(result2);
        when(executionPhase.execute(Collections.singletonList(combination3)))
                .thenReturn(result3);
        when(generationPhase.execute(new HashMap<>()))
                .thenReturn(combination1);
        when(generationPhase.execute(result1))
                .thenReturn(combination2);
        when(generationPhase.execute(result3))
                .thenReturn(null);
        when(identificationPhase.initialize(combination2, result2.get(combination2)))
                .thenReturn(combination3);
        when(identificationPhase.execute(result3))
                .thenReturn(null);
        when(checkingPhase.initialize())
                .thenAnswer(new Answer<>() {
                    int iteration = 0;

                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) {
                        iteration++;
                        if (iteration == 1 || iteration == 3) {
                            return combination3;
                        }

                        return null;
                    }
                });
        when(checkingPhase.failureInducingCombinationsFound())
                .thenAnswer(new Answer<>() {
                    int iteration = 0;

                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) {
                        iteration++;
                        return iteration != 1 && iteration != 4;
                    }
                });
        when(checkingPhase.execute(result3))
                .thenReturn(null);
        when(classificationPhase.initialize(errorConstraintExceptionCausingTestInputs))
                .thenReturn(combination2);
        when(classificationPhase.execute(result2))
                .thenReturn(null);
        
        when(executionConfiguration.getModelConverterFactory())
                .thenReturn(model -> mock(ModelConverter.class));
        when(executionConfiguration.getTestInputGenerationStrategyFactory())
                .thenReturn(mock(TestInputGenerationStrategyFactory.class));
        when(executionConfiguration.getIdentificationStrategyFactory())
                .thenReturn(mock(IdentificationStrategyFactory.class));
        when(executionConfiguration.getFeedbackCheckingStrategyFactory())
                .thenReturn(mock(FeedbackCheckingStrategyFactory.class));
        when(executionConfiguration.getClassificationStrategyFactory())
                .thenReturn(mock(ClassificationStrategyFactory.class));
        when(executionConfiguration.getConstraintCheckerFactory())
                .thenReturn(mock(ConstraintCheckerFactory.class));
        when(executionConfiguration.getManagerFactory())
                .thenReturn((someConfiguration, model) -> mock(InterleavingCombinatorialTestManager.class));
    }

    @Test
    void runDefaultTestingInterleavingPhaseManager() {
        prepare();
    
        final InterleavingPhaseManagerConfiguration configuration = phaseManagerConfiguration()
                .testMethodConfiguration(testMethodConfiguration)
                .executionPhaseFactory(context -> executionPhase)
                .generationPhaseFactory(context -> generationPhase)
                .checkingPhaseFactory(context -> checkingPhase)
                .identificationPhaseFactory(context -> identificationPhase)
                .classificationPhaseFactory(context -> classificationPhase)
                .extensionExecutorFactory(extensions -> extensionExecutor)
                .executionConfiguration(executionConfiguration)
                .build();

        final AbstractInterleavingPhaseManager manager = new DefaultTestingInterleavingPhaseManager(configuration);
        manager.run();

        InOrder inOrder = Mockito.inOrder(generationPhase, executionPhase, identificationPhase, checkingPhase, classificationPhase);

        inOrder.verify(generationPhase).execute(new HashMap<>());
        inOrder.verify(executionPhase).execute(Collections.singletonList(combination1));
        inOrder.verify(generationPhase).execute(result1);
        inOrder.verify(executionPhase).execute(Collections.singletonList(combination2));
        inOrder.verify(identificationPhase).initialize(combination2, result2.get(combination2));
        inOrder.verify(checkingPhase).failureInducingCombinationsFound();
        inOrder.verify(executionPhase).execute(Collections.singletonList(combination3));
        inOrder.verify(identificationPhase).execute(result3);
        inOrder.verify(checkingPhase).initialize();
        inOrder.verify(checkingPhase).execute(result3);
        inOrder.verify(checkingPhase, times(2)).failureInducingCombinationsFound();
        inOrder.verify(generationPhase).execute(result3);
        inOrder.verify(classificationPhase, times(0)).initialize(errorConstraintExceptionCausingTestInputs);
        inOrder.verify(classificationPhase, times(0)).execute(result2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void runDefaultGeneratingInterleavingPhaseManager() {
        prepare();
    
        final InterleavingPhaseManagerConfiguration configuration = phaseManagerConfiguration()
                .testMethodConfiguration(testMethodConfiguration)
                .executionPhaseFactory(context -> executionPhase)
                .generationPhaseFactory(context -> generationPhase)
                .checkingPhaseFactory(context -> checkingPhase)
                .identificationPhaseFactory(context -> identificationPhase)
                .classificationPhaseFactory(context -> classificationPhase)
                .extensionExecutorFactory(extensions -> extensionExecutor)
                .executionConfiguration(executionConfiguration)
                .build();

        final AbstractInterleavingPhaseManager manager = new DefaultGeneratingInterleavingPhaseManager(configuration);
        manager.run();

        InOrder inOrder = Mockito.inOrder(generationPhase, executionPhase, identificationPhase, checkingPhase, classificationPhase);

        inOrder.verify(generationPhase).execute(new HashMap<>());
        inOrder.verify(executionPhase).execute(Collections.singletonList(combination1));
        inOrder.verify(generationPhase).execute(result1);
        inOrder.verify(executionPhase).execute(Collections.singletonList(combination2));
        inOrder.verify(identificationPhase).initialize(combination2, result2.get(combination2));
        inOrder.verify(checkingPhase).failureInducingCombinationsFound();
        inOrder.verify(executionPhase).execute(Collections.singletonList(combination3));
        inOrder.verify(identificationPhase).execute(result3);
        inOrder.verify(checkingPhase).initialize();
        inOrder.verify(checkingPhase).execute(result3);
        inOrder.verify(checkingPhase, times(2)).failureInducingCombinationsFound();
        inOrder.verify(generationPhase).execute(result3);
        inOrder.verify(classificationPhase).initialize(errorConstraintExceptionCausingTestInputs);
        inOrder.verify(classificationPhase).execute(result2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void runDefaultDynamicInterleavingPhaseManager() {
        prepare();

        List<Value> values = new ArrayList<>();
        values.add(Value.value(0, 0));
        values.add(Value.value(1, 1));

        List<Parameter> parameters = Collections.singletonList(new Parameter("param1", values));

        DefaultGeneratingInterleavingManager generatingInterleavingManager = mock(DefaultGeneratingInterleavingManager.class);
        when(generatingInterleavingManager.getMinimalExceptionInducingCombinations())
                .thenReturn(new HashMap<>());

        when(model.toBuilder()).thenReturn(inputParameterModel("name")
                .positiveTestingStrength(1)
                .parameters(parameters));
        when(model.getParameters())
                .thenReturn(parameters);
        when(model.getPositiveTestingStrength())
                .thenReturn(1);
        when(model.getName())
                .thenReturn("name");
        when(model.getExclusionConstraints())
                .thenReturn(Collections.emptyList());
        when(model.getErrorConstraints())
                .thenReturn(Collections.emptyList());

        InterleavingGenerationPhaseFactory generationPhaseFactory = mock(InterleavingGenerationPhaseFactory.class);
        when(generationPhaseFactory.create(any()))
                .thenReturn(generationPhase);

        CheckingPhaseFactory checkingPhaseFactory = mock(CheckingPhaseFactory.class);
        when(checkingPhaseFactory.create(any()))
                .thenReturn(checkingPhase);

        IdentificationPhaseFactory identificationPhaseFactory = mock(IdentificationPhaseFactory.class);
        when(identificationPhaseFactory.create(any()))
                .thenReturn(identificationPhase);

        InterleavingClassificationPhaseFactory classificationPhaseFactory = mock(InterleavingClassificationPhaseFactory.class);
        when(classificationPhaseFactory.create(any()))
                .thenReturn(classificationPhase);

        final DynamicInterleavingPhaseManagerConfiguration configuration = DynamicInterleavingPhaseManagerConfiguration
                .phaseManagerConfiguration()
                .testMethodConfiguration(testMethodConfiguration)
                .executionPhaseFactory(context -> executionPhase)
                .extensionExecutorFactory(extensions -> extensionExecutor)
                .generationPhaseFactory(generationPhaseFactory)
                .checkingPhaseFactory(checkingPhaseFactory)
                .classificationPhaseFactory(classificationPhaseFactory)
                .identificationPhaseFactory(identificationPhaseFactory)
                .executionConfiguration(executionConfiguration)
                .errorConstraintGenerationConfiguration(executionConfiguration)
                .build();
        
        when(executionConfiguration.getModelConverterFactory())
                .thenReturn(modelConverterFactory);
        when(modelConverterFactory.create(any()))
                .thenReturn(modelConverter);
        when(executionConfiguration.getTestInputGenerationStrategyFactory())
                .thenReturn(Mockito.mock(TestInputGenerationStrategyFactory.class));
        when(executionConfiguration.getIdentificationStrategyFactory())
                .thenReturn(mock(IdentificationStrategyFactory.class));
        when(executionConfiguration.getFeedbackCheckingStrategyFactory())
                .thenReturn(mock(FeedbackCheckingStrategyFactory.class));
        when(executionConfiguration.getClassificationStrategyFactory())
                .thenReturn(mock(ClassificationStrategyFactory.class));
        when(executionConfiguration.getConstraintCheckerFactory())
                .thenReturn(mock(ConstraintCheckerFactory.class));
        
        final InterleavingManagerFactory managerFactory = mock(InterleavingManagerFactory.class);
        final DefaultGeneratingInterleavingManager testManager = mock(DefaultGeneratingInterleavingManager.class);
        when(executionConfiguration.getManagerFactory())
                .thenReturn(managerFactory);
        when(managerFactory.create(any(), any()))
                .thenReturn(testManager);
        when(testManager.getMinimalExceptionInducingCombinations())
                .thenReturn(Map.of());
        
        final DefaultDynamicInterleavingPhaseManager manager = new DefaultDynamicInterleavingPhaseManager(configuration);
        manager.run();

        InOrder inOrder = Mockito.inOrder(generationPhase, executionPhase, identificationPhase, checkingPhase, classificationPhase);

        inOrder.verify(generationPhase).execute(new HashMap<>());
        inOrder.verify(executionPhase).execute(Collections.singletonList(combination1));
        inOrder.verify(generationPhase).execute(result1);
        inOrder.verify(executionPhase).execute(Collections.singletonList(combination2));
        inOrder.verify(identificationPhase).initialize(combination2, result2.get(combination2));
        inOrder.verify(checkingPhase).failureInducingCombinationsFound();
        inOrder.verify(executionPhase).execute(Collections.singletonList(combination3));
        inOrder.verify(identificationPhase).execute(result3);
        inOrder.verify(checkingPhase).initialize();
        inOrder.verify(checkingPhase).execute(result3);
        inOrder.verify(checkingPhase, times(2)).failureInducingCombinationsFound();
        inOrder.verify(generationPhase).execute(result3);
        inOrder.verify(classificationPhase).initialize(errorConstraintExceptionCausingTestInputs);
        inOrder.verify(classificationPhase).execute(result2);
        inOrder.verify(generationPhase).execute(new HashMap<>());
        inOrder.verify(generationPhase).execute(result1);
        inOrder.verify(identificationPhase).initialize(combination2, result2.get(combination2));
        inOrder.verify(checkingPhase).failureInducingCombinationsFound();
        inOrder.verify(identificationPhase).execute(result3);
        inOrder.verify(checkingPhase).initialize();
        inOrder.verify(checkingPhase).execute(result3);
        inOrder.verify(checkingPhase, times(2)).failureInducingCombinationsFound();
        inOrder.verify(generationPhase).execute(result3);
        inOrder.verifyNoMoreInteractions();
    }
}
