package de.rwth.swc.coffee4j.algorithmic.sequential.manager;

import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.NoOpTestInputPrioritizer;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;
import de.rwth.swc.coffee4j.algorithmic.sequential.report.GenerationReporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SequentialCombinatorialTestConfigurationTest {
    
    private static final FaultCharacterizationAlgorithmFactory FACTORY = Mockito.mock(FaultCharacterizationAlgorithmFactory.class);
    
    private static final TestInputGroupGenerator GENERATOR = Mockito.mock(TestInputGroupGenerator.class);
    
    private static final GenerationReporter REPORTER = Mockito.mock(GenerationReporter.class);
    
    @Test
    void preconditions() {
        assertThrows(NullPointerException.class, () -> new SequentialCombinatorialTestConfiguration(FACTORY, null, null,
                null, REPORTER, null));
    }
    
    @Test
    void optionalNotPresentIfFactoryNull() {
        final SequentialCombinatorialTestConfiguration configuration = new SequentialCombinatorialTestConfiguration(
                null, null, List.of(GENERATOR), null, REPORTER, null);
        
        assertFalse(configuration.getFaultCharacterizationAlgorithmFactory().isPresent());
        assertEquals(List.of(GENERATOR), configuration.getGenerators());
        Assertions.assertEquals(REPORTER, configuration.getGenerationReporter().orElse(null));
    }
    
    @Test
    void optionalNotPresentIfReporterNull() {
        final SequentialCombinatorialTestConfiguration configuration = new SequentialCombinatorialTestConfiguration(
                FACTORY, null, List.of(GENERATOR), null, null, null);
        
        assertFalse(configuration.getGenerationReporter().isPresent());
        assertEquals(List.of(GENERATOR), configuration.getGenerators());
        Assertions.assertEquals(FACTORY, configuration.getFaultCharacterizationAlgorithmFactory().orElse(null));
    }
    
    @Test
    void usesNoOpTestInputPrioritizerAsDefault() {
        final SequentialCombinatorialTestConfiguration configuration = new SequentialCombinatorialTestConfiguration(
                FACTORY, null, List.of(GENERATOR), null, null, null);
        
        assertThat(configuration.getPrioritizer())
                .isInstanceOf(NoOpTestInputPrioritizer.class);
    }
    
    @Test
    void returnsProvidedPrioritizer() {
        final TestInputPrioritizer prioritizer = (testInputs, model) -> new ArrayList<>(testInputs);
    
        final SequentialCombinatorialTestConfiguration configuration = new SequentialCombinatorialTestConfiguration(
                FACTORY, null, List.of(GENERATOR), prioritizer, null, null);
        
        assertEquals(prioritizer, configuration.getPrioritizer());
    }
    
}
