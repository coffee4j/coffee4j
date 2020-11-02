package de.rwth.swc.coffee4j.engine.configuration.model;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.StrengthGroup;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.StrengthGroup.mixedStrengthGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StrengthGroupTest {
    
    private static final List<Parameter> PARAMETERS = List.of(
            parameter("first").values(1, 2).build(),
            parameter("second").values(3, 4).build(),
            parameter("third").values(5, 6).build(),
            parameter("fourth").values(7, 8).build(),
            parameter("fifth").values(9, 10).build()
    );
    
    @Test
    void mustIncludeAtLeastOneParameter() {
        assertThrows(IllegalArgumentException.class, () -> mixedStrengthGroup().build(PARAMETERS));
    }
    
    @Test
    void strengthMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> mixedStrengthGroup("first", "second")
                .ofStrength(-1).build(PARAMETERS));
    }
    
    @Test
    void strengthMustNotBeHigherThanNumberOfParameters() {
        assertThrows(IllegalArgumentException.class, () -> mixedStrengthGroup("first", "second")
                .ofStrength(5).build(PARAMETERS));
    }
    
    @Test
    void parameterNamesMustAppearInGivenParameters() {
        assertThrows(Coffee4JException.class, () -> mixedStrengthGroup("first", "test")
            .ofHighestStrength().build(PARAMETERS));
    }
    
    @Test
    void createsCorrectGroupWithGivenStrength() {
        final StrengthGroup group = mixedStrengthGroup("first", "third", "fifth")
                .ofStrength(2)
                .build(PARAMETERS);
        
        assertNotNull(group);
        assertEquals(2, group.getStrength());
        assertEquals(Set.of(PARAMETERS.get(0), PARAMETERS.get(2), PARAMETERS.get(4)), group.getParameters());
    }
    
    @Test
    void highestStrengthIsNumberOfParameters() {
        assertEquals(1, mixedStrengthGroup("first")
                .ofHighestStrength().build(PARAMETERS).getStrength());
        assertEquals(2, mixedStrengthGroup("first", "third")
                .ofHighestStrength().build(PARAMETERS).getStrength());
        assertEquals(5, mixedStrengthGroup("first", "third", "second", "fifth", "fourth")
                .ofHighestStrength().build(PARAMETERS).getStrength());
    }
    
}
