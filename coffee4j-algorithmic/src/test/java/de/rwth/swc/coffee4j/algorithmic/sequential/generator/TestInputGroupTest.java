package de.rwth.swc.coffee4j.algorithmic.sequential.generator;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TestInputGroupTest {
    
    @Test
    void preconditions() {
        Assertions.assertThrows(NullPointerException.class, () -> new TestInputGroup(null, Collections.emptyList()));
        Assertions.assertThrows(NullPointerException.class, () -> new TestInputGroup(null, Collections.emptyList(), null));
        
        Assertions.assertThrows(NullPointerException.class, () -> new TestInputGroup("", null));
        Assertions.assertThrows(NullPointerException.class, () -> new TestInputGroup("", null, null));
    }
    
    @Test
    void noFaultCharacterizationConfigurationWithSmallConstructorOrNull() {
        assertFalse(new TestInputGroup("", Collections.emptyList(), null).getFaultCharacterizationConfiguration().isPresent());
        assertFalse(new TestInputGroup("", Collections.emptyList()).getFaultCharacterizationConfiguration().isPresent());
    }
    
    @Test
    void correctInformationStorage() {
        final int[] firstCombination = new int[]{0, 1, 2};
        final int[] secondCombination = new int[]{3, 2, 1};
        final FaultCharacterizationConfiguration configuration = new FaultCharacterizationConfiguration(
                CompleteTestModel.builder()
                        .positiveTestingStrength(2)
                        .parameterSizes(3, 3, 3)
                        .build(),
                Mockito.mock(Reporter.class));
        final TestInputGroup group = new TestInputGroup("test", Arrays.asList(firstCombination, secondCombination), configuration);
        
        assertEquals(2, group.getTestInputs().size());
        assertArrayEquals(firstCombination, group.getTestInputs().get(0));
        assertArrayEquals(secondCombination, group.getTestInputs().get(1));
        assertEquals("test", group.getIdentifier());
        assertTrue(group.getFaultCharacterizationConfiguration().isPresent());
        assertEquals(configuration, group.getFaultCharacterizationConfiguration().get());
    }
    
}
