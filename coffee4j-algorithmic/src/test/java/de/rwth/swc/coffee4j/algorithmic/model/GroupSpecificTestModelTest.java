package de.rwth.swc.coffee4j.algorithmic.model;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.NoConstraintChecker;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GroupSpecificTestModelTest {
    
    private static final CompleteTestModel DELEGATE = CompleteTestModel.builder()
            .positiveTestingStrength(2)
            .negativeTestingStrength(3)
            .parameterSizes(2, 3, 4, 5)
            .weight(0, 1, 2.0)
            .weight(1, 2, 3.0)
            .seeds(List.of(new PrimitiveSeed(new int[] {0, 0}, SeedMode.NON_EXCLUSIVE, 1)))
            .seeds(1, List.of(new PrimitiveSeed(new int[] {1, 1}, SeedMode.EXCLUSIVE, -2)))
            .mixedStrengthGroups(Set.of(PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {1, 3, 5}))))
            .mixedStrengthGroups(1, Set.of(
                    PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {1, 2, 5}))))
            .build();
    
    private static final ConstraintChecker CONSTRAINT_CHECKER = Mockito.mock(ConstraintChecker.class);
    
    @Test
    void testPreconditions() {
        assertThrows(NullPointerException.class, () -> GroupSpecificTestModel.positive(null, null));
        assertThrows(NullPointerException.class, () -> GroupSpecificTestModel.negative(0, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> GroupSpecificTestModel.negative(-3, DELEGATE, CONSTRAINT_CHECKER));
    }
    
    @Test
    void usesNoConstraintCheckerIfNoneIsGiven() {
        final GroupSpecificTestModel model = GroupSpecificTestModel.positive(DELEGATE, null);
        assertThat(model.getConstraintChecker())
                .isInstanceOf(NoConstraintChecker.class);
    }
    
    @Test
    void correctlyConstructsPositiveGroup() {
        final GroupSpecificTestModel model = GroupSpecificTestModel.positive(DELEGATE, CONSTRAINT_CHECKER);
        
        assertEquals(2, model.getDefaultTestingStrength());
        assertArrayEquals(DELEGATE.getParameterSizes(), model.getParameterSizes());
        assertEquals(1, model.getSeeds().size());
        assertEquals(List.of(new PrimitiveSeed(new int[] {0, 0}, SeedMode.NON_EXCLUSIVE, 1)), model.getSeeds());
        assertEquals(1, model.getMixedStrengthGroups().size());
        assertEquals(new IntOpenHashSet(new int[] {1, 3, 5}), model.getMixedStrengthGroups().get(0).getParameters());
        assertEquals(CONSTRAINT_CHECKER, model.getConstraintChecker());
    }
    
    @Test
    void correctlyConstructsNegativeGroup() {
        final GroupSpecificTestModel model = GroupSpecificTestModel.negative(1, DELEGATE, CONSTRAINT_CHECKER);
        
        assertEquals(3, model.getDefaultTestingStrength());
        assertArrayEquals(DELEGATE.getParameterSizes(), model.getParameterSizes());
        assertEquals(1, model.getSeeds().size());
        assertEquals(List.of(new PrimitiveSeed(new int[] {1, 1}, SeedMode.EXCLUSIVE, -2)), model.getSeeds());
        assertEquals(1, model.getMixedStrengthGroups().size());
        assertEquals(new IntOpenHashSet(new int[] {1, 2, 5}), model.getMixedStrengthGroups().get(0).getParameters());
        assertEquals(CONSTRAINT_CHECKER, model.getConstraintChecker());
    }
    
    @Test
    void correctlyDealsWithNoSeeds() {
        final GroupSpecificTestModel model = GroupSpecificTestModel.negative(2, DELEGATE, CONSTRAINT_CHECKER);
        
        assertEquals(List.of(), model.getSeeds());
    }
    
    @Test
    void correctlyDealsWithNoMixedStrengthGroups() {
        final GroupSpecificTestModel model = GroupSpecificTestModel.negative(2, DELEGATE, CONSTRAINT_CHECKER);
        
        assertEquals(List.of(), model.getMixedStrengthGroups());
    }
    
    @Test
    void correctlyDelegatesWeightRequests() {
        final GroupSpecificTestModel model = GroupSpecificTestModel.positive(DELEGATE, CONSTRAINT_CHECKER);
        
        assertEquals(2.0, model.getWeight(0, 1));
        assertEquals(3.0, model.getWeight(1, 2));
        assertEquals(0, model.getWeight(0, 0));
        assertEquals(-1, model.getWeight(0, 0, -1));
    }
    
}
