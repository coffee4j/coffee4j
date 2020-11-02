package de.rwth.swc.coffee4j.engine.configuration.model;

import de.rwth.swc.coffee4j.algorithmic.model.SeedMode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.Seed.seed;
import static de.rwth.swc.coffee4j.engine.configuration.model.Value.value;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SeedTest {
    
    @Test
    void combinationForBuilderMayNotBeNull() {
        assertThrows(NullPointerException.class, () -> seed((Combination) null));
    }
    
    @Test
    void checkDefaultValues() {
        final Combination combination = Combination.of(Map.of(
                parameter("first").values(1, 2).build(), value(0, 1)));
        
        final Seed defaultSeed = seed(combination).build(List.of());
        
        assertEquals(combination, defaultSeed.getCombination());
        assertEquals(SeedMode.NON_EXCLUSIVE, defaultSeed.getMode());
        assertEquals(Seed.NO_PRIORITY, defaultSeed.getPriority());
    }
    
    @Test
    void builderOverwriteValues() {
        final Combination combination = Combination.of(Map.of(
                parameter("first").values(1, 2).build(), value(0, 1)));
        
        final Seed first = seed(combination)
                .mode(SeedMode.EXCLUSIVE)
                .priority(2)
                .build(List.of());
        assertEquals(combination, first.getCombination());
        assertEquals(SeedMode.EXCLUSIVE, first.getMode());
        assertEquals(2, first.getPriority());
        
        final Seed second = seed(combination)
                .suspicious()
                .priority(-1)
                .build(List.of());
        assertEquals(combination, second.getCombination());
        assertEquals(SeedMode.EXCLUSIVE, second.getMode());
        assertEquals(-1, second.getPriority());
    }
    
    @Test
    void parametersCanBeNullOrEmptyIfCombinationIsGivenToBuilder() {
        final Combination combination = Combination.of(Map.of(
                parameter("first").values(1, 2).build(), value(0, 1)));
        
        final Seed buildWithNull = seed(combination).build(null);
        final Seed buildWithEmpty = seed(combination).build(List.of());
        
        assertEquals(combination, buildWithNull.getCombination());
        assertEquals(combination, buildWithEmpty.getCombination());
    }
    
    @Test
    void throwsExceptionIfNeededParametersAreNotGivenWhenUsingCombinationBuilder() {
        final List<Parameter> parameters = List.of(
                parameter("first").values(0, 1).build(),
                parameter("second").values(2, 3).build(),
                parameter("third").values(4, 5).build());
        
        assertThrows(NullPointerException.class, () -> seed(entry("fourth", 0)).build(null));
        assertThrows(IllegalArgumentException.class, () -> seed(entry("fourth", 0)).build(List.of()));
        assertThrows(IllegalArgumentException.class, () -> seed(entry("fourth", 0)).build(parameters));
    }
    
    @Test
    void constructsCombinationFromBuilderIfAllParametersAreGiven() {
        final List<Parameter> parameters = List.of(
                parameter("first").values(0, 1).build(),
                parameter("second").values(2, 3).build(),
                parameter("third").values(4, 5).build());
        
        final Seed seed = seed(entry("first", 1), entry("second", 2))
                .suspicious()
                .priority(2)
                .build(parameters);
        
        final Combination expectedCombination = Combination.of(Map.of(
                parameters.get(0), value(1, 1),
                parameters.get(1), value(0, 2)));
        assertEquals(expectedCombination, seed.getCombination());
        assertEquals(SeedMode.EXCLUSIVE, seed.getMode());
        assertEquals(2, seed.getPriority());
    }
    
}
