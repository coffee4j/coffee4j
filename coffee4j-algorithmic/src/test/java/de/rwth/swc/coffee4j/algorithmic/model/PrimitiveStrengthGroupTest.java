package de.rwth.swc.coffee4j.algorithmic.model;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrimitiveStrengthGroupTest {
    
    @Test
    void parametersCannotBeNull() {
        assertThrows(NullPointerException.class, () -> PrimitiveStrengthGroup.ofHighestStrength(null));
        assertThrows(NullPointerException.class, () -> PrimitiveStrengthGroup.ofStrength(null, 1));
    }
    
    @Test
    void strengthMustBeValid() {
        final IntSet parameters = new IntOpenHashSet(new int[] {1, 2});
        assertThrows(IllegalArgumentException.class, () -> PrimitiveStrengthGroup.ofStrength(parameters, -1));
        assertThrows(IllegalArgumentException.class, () -> PrimitiveStrengthGroup.ofStrength(parameters, -3));
    }
    
    @Test
    void highestStrengthSetStrengthToNumberOfParameters() {
        final IntSet twoParameters = new IntOpenHashSet(new int[] {1, 2});
        final IntSet sevenParameters = new IntOpenHashSet(new int[] {1, 2, 3, 4, 5, 6, 7});
        
        final PrimitiveStrengthGroup first = PrimitiveStrengthGroup.ofHighestStrength(twoParameters);
        final PrimitiveStrengthGroup second = PrimitiveStrengthGroup.ofHighestStrength(sevenParameters);
        
        assertEquals(2, first.getStrength());
        assertEquals(7, second.getStrength());
    }
    
    @Test
    void correctlyConfiguresGivenStrength() {
        final IntSet parameters = new IntOpenHashSet(new int[] {1, 2, 3, 4, 5, 6});
        final PrimitiveStrengthGroup group = PrimitiveStrengthGroup.ofStrength(parameters, 3);
        
        assertEquals(3, group.getStrength());
        assertEquals(parameters, group.getParameters());
    }
    
    @Test
    void givesOnlyOneSubGroupForHighestStrength() {
        final IntSet parameters = new IntOpenHashSet(new int[] {1, 2, 3, 4, 5, 6});
        final PrimitiveStrengthGroup group = PrimitiveStrengthGroup.ofHighestStrength(parameters);
        final List<IntSet> allSubGroups = group.getAllSubGroups();
        
        assertEquals(List.of(parameters), allSubGroups);
    }
    
    @Test
    void givesAllPossibleSubGroupsIfStrengthIsLower() {
        final IntSet parameters = new IntOpenHashSet(new int[] {1, 2, 3});
        final PrimitiveStrengthGroup group = PrimitiveStrengthGroup.ofStrength(parameters, 2);
        final List<IntSet> allSubGroups = group.getAllSubGroups();
        
        assertThat(allSubGroups)
                .containsExactlyInAnyOrder(
                        new IntOpenHashSet(new int[] {1, 2}),
                        new IntOpenHashSet(new int[] {1, 3}),
                        new IntOpenHashSet(new int[] {2, 3}));
    }
    
}
