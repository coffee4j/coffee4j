package de.rwth.swc.coffee4j.engine.process.manager.sequential;

import de.rwth.swc.coffee4j.engine.MockingTest;
import de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutorFactory;
import de.rwth.swc.coffee4j.engine.process.manager.ConflictDetectorFactory;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.characterization.FaultCharacterizationPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.classification.SequentialClassificationPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.generation.SequentialGenerationPhaseFactory;
import org.junit.jupiter.api.Test;

import static de.rwth.swc.coffee4j.engine.process.manager.sequential.SequentialPhaseManagerConfiguration.phaseManagerConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class SequentialPhaseManagerConfigurationTest implements MockingTest {
    
    private final SequentialExecutionConfiguration executionConfiguration =
            mock(SequentialExecutionConfiguration.class);
    private final TestMethodConfiguration testMethodConfiguration = mock(TestMethodConfiguration.class);
    
    @Test
    void checkExecutionConfigurationNotNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> phaseManagerConfiguration()
                        .executionConfiguration(null)
                        .testMethodConfiguration(testMethodConfiguration)
                        .build());
        
        assertThat(exception.getMessage()).contains("executionConfiguration");
    }
    
    @Test
    void checkTestMethodConfigurationNotNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> phaseManagerConfiguration()
                        .executionConfiguration(executionConfiguration)
                        .testMethodConfiguration(null)
                        .build());
        
        assertThat(exception.getMessage()).contains("testMethodConfiguration");
    }
    
    @Test
    void checkExtensionExecutorFactoryNotNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> phaseManagerConfiguration()
                        .executionConfiguration(executionConfiguration)
                        .testMethodConfiguration(testMethodConfiguration)
                        .extensionExecutorFactory(null)
                        .build());
        
        assertThat(exception.getMessage()).contains("extensionExecutorFactory");
    }
    
    @Test
    void checkExecutionPhaseFactoryNotNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> phaseManagerConfiguration()
                        .executionConfiguration(executionConfiguration)
                        .testMethodConfiguration(testMethodConfiguration)
                        .executionPhaseFactory(null)
                        .build());
        
        assertThat(exception.getMessage()).contains("executionPhaseFactory");
    }
    
    @Test
    void checkGenerationPhaseFactoryNotNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> phaseManagerConfiguration()
                        .executionConfiguration(executionConfiguration)
                        .testMethodConfiguration(testMethodConfiguration)
                        .generationPhaseFactory(null)
                        .build());
        
        assertThat(exception.getMessage()).contains("generationPhaseFactory");
    }
    
    @Test
    void checkFaultCharacterizationPhaseFactoryNotNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> phaseManagerConfiguration()
                        .executionConfiguration(executionConfiguration)
                        .testMethodConfiguration(testMethodConfiguration)
                        .faultCharacterizationPhaseFactory(null)
                        .build());
        
        assertThat(exception.getMessage()).contains("faultCharacterizationPhaseFactory");
    }
    
    @Test
    void checkClassificationPhaseFactoryNotNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> phaseManagerConfiguration()
                        .executionConfiguration(executionConfiguration)
                        .testMethodConfiguration(testMethodConfiguration)
                        .classificationPhaseFactory(null)
                        .build());
        
        assertThat(exception.getMessage()).contains("classificationPhaseFactory");
    }
    
    @Test
    void checkConflictDetectorFactoryNotNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> phaseManagerConfiguration()
                        .executionConfiguration(executionConfiguration)
                        .testMethodConfiguration(testMethodConfiguration)
                        .conflictDetectorFactory(null)
                        .build());
        
        assertThat(exception.getMessage()).contains("conflictDetectorFactory");
    }
    
    @Test
    void returnGivenExtensionExecutorFactory() {
        final ExtensionExecutorFactory factory = mock(ExtensionExecutorFactory.class);
        SequentialPhaseManagerConfiguration configuration = phaseManagerConfiguration()
                .executionConfiguration(executionConfiguration)
                .testMethodConfiguration(testMethodConfiguration)
                .extensionExecutorFactory(factory)
                .build();
        
        assertEquals(factory, configuration.getExtensionExecutorFactory());
    }
    
    @Test
    void returnGivenExecutionPhaseFactory() {
        final ExecutionPhaseFactory factory = mock(ExecutionPhaseFactory.class);
        SequentialPhaseManagerConfiguration configuration = phaseManagerConfiguration()
                .executionConfiguration(executionConfiguration)
                .testMethodConfiguration(testMethodConfiguration)
                .executionPhaseFactory(factory)
                .build();
        
        assertEquals(factory, configuration.getExecutionPhaseFactory());
    }
    
    @Test
    void returnGivenGenerationPhaseFactory() {
        final SequentialGenerationPhaseFactory factory = mock(SequentialGenerationPhaseFactory.class);
        SequentialPhaseManagerConfiguration configuration = phaseManagerConfiguration()
                .executionConfiguration(executionConfiguration)
                .testMethodConfiguration(testMethodConfiguration)
                .generationPhaseFactory(factory)
                .build();
        
        assertEquals(factory, configuration.getGenerationPhaseFactory());
    }
    
    @Test
    void returnGivenFaultCharacterizationPhaseFactory() {
        final FaultCharacterizationPhaseFactory factory = mock(FaultCharacterizationPhaseFactory.class);
        SequentialPhaseManagerConfiguration configuration = phaseManagerConfiguration()
                .executionConfiguration(executionConfiguration)
                .testMethodConfiguration(testMethodConfiguration)
                .faultCharacterizationPhaseFactory(factory)
                .build();
        
        assertEquals(factory, configuration.getFaultCharacterizationPhaseFactory());
    }
    
    @Test
    void returnGivenClassificationPhaseFactory() {
        final SequentialClassificationPhaseFactory factory = mock(SequentialClassificationPhaseFactory.class);
        SequentialPhaseManagerConfiguration configuration = phaseManagerConfiguration()
                .executionConfiguration(executionConfiguration)
                .testMethodConfiguration(testMethodConfiguration)
                .classificationPhaseFactory(factory)
                .build();
        
        assertEquals(factory, configuration.getClassificationPhaseFactory());
    }
    
    @Test
    void returnGivenConflictDetectorFactory() {
        final ConflictDetectorFactory factory = mock(ConflictDetectorFactory.class);
        SequentialPhaseManagerConfiguration configuration = phaseManagerConfiguration()
                .executionConfiguration(executionConfiguration)
                .testMethodConfiguration(testMethodConfiguration)
                .conflictDetectorFactory(factory)
                .build();
        
        assertEquals(factory, configuration.getConflictDetectorFactory());
    }
    
}