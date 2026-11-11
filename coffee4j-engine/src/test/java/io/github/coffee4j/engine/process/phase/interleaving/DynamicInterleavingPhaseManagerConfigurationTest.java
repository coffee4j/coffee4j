package io.github.coffee4j.engine.process.phase.interleaving;

import io.github.coffee4j.engine.MockingTest;
import io.github.coffee4j.engine.configuration.TestMethodConfiguration;
import io.github.coffee4j.engine.process.interleaving.DynamicInterleavingPhaseManagerConfiguration;
import io.github.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import io.github.coffee4j.engine.configuration.extension.Extension;
import io.github.coffee4j.engine.configuration.extension.execution.AfterExecutionCallback;
import io.github.coffee4j.engine.process.extension.ExtensionExecutor;
import io.github.coffee4j.engine.process.phase.execution.ExecutionContext;
import io.github.coffee4j.engine.process.phase.execution.ExecutionPhaseFactory;
import io.github.coffee4j.engine.process.phase.interleaving.checking.CheckingPhaseFactory;
import io.github.coffee4j.engine.process.phase.interleaving.classification.InterleavingClassificationPhaseFactory;
import io.github.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationPhaseFactory;
import io.github.coffee4j.engine.process.phase.interleaving.identification.IdentificationPhaseFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DynamicInterleavingPhaseManagerConfigurationTest implements MockingTest {
    private static DynamicInterleavingPhaseManagerConfiguration configuration;

    private static final InterleavingExecutionConfiguration executionConfiguration
            = mock(InterleavingExecutionConfiguration.class);
    private static final InterleavingExecutionConfiguration
            errorConstraintGenerationExecutionConfiguration = mock(InterleavingExecutionConfiguration.class);
    private static final TestMethodConfiguration TEST_METHOD_CONFIGURATION = mock(TestMethodConfiguration.class);
    private static final Extension extension = mock(AfterExecutionCallback.class);
    private static final ExtensionExecutor extensionExecutor = mock(ExtensionExecutor.class);
    private static final ExecutionContext executionContext = mock(ExecutionContext.class);
    private static final ExecutionPhaseFactory executionPhaseFactory = mock(ExecutionPhaseFactory.class);
    private static final InterleavingGenerationPhaseFactory
            generationPhaseFactory = mock(InterleavingGenerationPhaseFactory.class);
    private static final IdentificationPhaseFactory identificationPhaseFactory = mock(IdentificationPhaseFactory.class);
    private static final CheckingPhaseFactory checkingPhaseFactory = mock(CheckingPhaseFactory.class);
    private static final InterleavingClassificationPhaseFactory
            classificationPhaseFactory = mock(InterleavingClassificationPhaseFactory.class);

    @BeforeAll
    static void prepareTests() {
        configuration = DynamicInterleavingPhaseManagerConfiguration.phaseManagerConfiguration()
                .executionConfiguration(executionConfiguration)
                .errorConstraintGenerationConfiguration(errorConstraintGenerationExecutionConfiguration)
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
    void setsErrorConstraintGenerationConfiguration() {
        assertThat(configuration.getErrorConstraintGenerationExecutionConfiguration())
                .isEqualTo(errorConstraintGenerationExecutionConfiguration);
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
    void setsGenerationPhaseFactory() {
        assertThat(configuration.getGenerationPhaseFactory())
                .isEqualTo(generationPhaseFactory);
    }

    @Test
    void setsIdentificationPhaseFactory() {
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
