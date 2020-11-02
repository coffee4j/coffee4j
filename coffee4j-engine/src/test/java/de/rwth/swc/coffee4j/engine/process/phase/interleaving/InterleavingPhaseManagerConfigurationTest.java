package de.rwth.swc.coffee4j.engine.process.phase.interleaving;

import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.feedback.FeedbackCheckingStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingManagerFactory;
import de.rwth.swc.coffee4j.engine.MockingTest;
import de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration;
import de.rwth.swc.coffee4j.engine.process.interleaving.InterleavingPhaseManagerConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.engine.configuration.extension.execution.AfterExecutionCallback;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverterFactory;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.checking.CheckingPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.classification.InterleavingClassificationPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.identification.IdentificationPhaseFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InterleavingPhaseManagerConfigurationTest implements MockingTest {
    
    private static InterleavingPhaseManagerConfiguration configuration;

    private static final InterleavingExecutionConfiguration executionConfiguration
            = mock(InterleavingExecutionConfiguration.class);
    private static final TestMethodConfiguration TEST_METHOD_CONFIGURATION = mock(TestMethodConfiguration.class);
    private static final Extension extension = mock(AfterExecutionCallback.class);
    private static final ExtensionExecutor extensionExecutor = mock(ExtensionExecutor.class);
    private static final ExecutionPhaseFactory executionPhaseFactory = mock(ExecutionPhaseFactory.class);
    private static final InterleavingGenerationPhaseFactory
            generationPhaseFactory = mock(InterleavingGenerationPhaseFactory.class);
    private static final IdentificationPhaseFactory identificationPhaseFactory = mock(IdentificationPhaseFactory.class);
    private static final CheckingPhaseFactory checkingPhaseFactory = mock(CheckingPhaseFactory.class);
    private static final InterleavingClassificationPhaseFactory
            classificationPhaseFactory = mock(InterleavingClassificationPhaseFactory.class);
    private static final ModelConverterFactory modelConverterFactory = mock(ModelConverterFactory.class);

    @BeforeAll
    static void prepareTests() {
        when(executionConfiguration.getModelConverterFactory())
                .thenReturn(modelConverterFactory);
        when(modelConverterFactory.create(any()))
                .thenReturn(mock(ModelConverter.class));
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
                .thenReturn(mock(InterleavingManagerFactory.class));
        
        configuration = InterleavingPhaseManagerConfiguration.phaseManagerConfiguration()
                .executionConfiguration(executionConfiguration)
                .testMethodConfiguration(TEST_METHOD_CONFIGURATION)
                .extensions(List.of(extension))
                .extensionExecutorFactory(extensions -> extensionExecutor)
                .executionPhaseFactory(executionPhaseFactory)
                .generationPhaseFactory(generationPhaseFactory)
                .identificationPhaseFactory(identificationPhaseFactory)
                .checkingPhaseFactory(checkingPhaseFactory)
                .classificationPhaseFactory(classificationPhaseFactory)
                .build();
    }

    @Test
    void setsExecutionConfiguration() {
        assertThat(configuration.getExecutionConfiguration())
                .isEqualTo(executionConfiguration);
    }

    @Test
    void setsTestClassConfiguration() {
        assertThat(configuration.getTestMethodConfiguration())
                .isEqualTo(TEST_METHOD_CONFIGURATION);
    }

    @Test
    void setsExtensions() {
        assertThat(configuration.getExtensions())
                .containsExactly(extension);
    }

    @Test
    void setsExecutionPhase() {
        assertThat(configuration.getExecutionPhaseFactory())
                .isEqualTo(executionPhaseFactory);
    }

    @Test
    void setsGenerationPhase() {
        assertThat(configuration.getGenerationPhaseFactory())
                .isEqualTo(generationPhaseFactory);
    }

    @Test
    void setsIdentificationPhase() {
        assertThat(configuration.getIdentificationPhaseFactory())
                .isEqualTo(identificationPhaseFactory);
    }

    @Test
    void setsCheckingPhaseFactory() {
        assertThat(configuration.getCheckingPhaseFactory())
                .isEqualTo(checkingPhaseFactory);
    }

    @Test
    void setsClassificationPhaseFactory() {
        assertThat(configuration.getClassificationPhaseFactory())
                .isEqualTo(classificationPhaseFactory);
    }
    
}
