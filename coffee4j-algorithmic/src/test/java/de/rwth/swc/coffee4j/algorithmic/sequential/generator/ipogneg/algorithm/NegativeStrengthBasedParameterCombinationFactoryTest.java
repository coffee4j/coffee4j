package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm;

import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NegativeStrengthBasedParameterCombinationFactoryTest {

    @Test
    void testNextParameterOfErrorConstraintWithNoInteractions() {
        final int strengthA = 1;
        final int strengthB = 0;

        final TupleList tupleList = mock(TupleList.class);
        when(tupleList.getInvolvedParameters()).thenReturn(new int[]{0, 1, 2, 3});

        final NegativeStrengthBasedParameterCombinationFactory factory
                = new NegativeStrengthBasedParameterCombinationFactory(tupleList, strengthA);

        final Optional<List<IntSet>> combinations
                = factory.create(new int[]{0, 1}, 2, strengthB);

        assertTrue(combinations.isPresent());
        assertEquals(0, combinations.get().size());
    }

    @Test
    void testNextParameterOfErrorConstraintWithInteractions() {
        final int strengthA = 2;
        final int strengthB = 0;

        final TupleList tupleList = mock(TupleList.class);
        when(tupleList.getInvolvedParameters()).thenReturn(new int[]{0, 1, 2, 3});

        final NegativeStrengthBasedParameterCombinationFactory factory
                = new NegativeStrengthBasedParameterCombinationFactory(tupleList, strengthA);

        final Optional<List<IntSet>> combinations
                = factory.create(new int[]{0, 1}, 2, strengthB);

        assertTrue(combinations.isPresent());
        assertEquals(2, combinations.get().size());
        assertTrue(combinations.get().contains(set(0)));
        assertTrue(combinations.get().contains(set(1)));
    }

    @Test
    void testNextParameterOfErrorConstraintWithMoreInteractions() {
        final int strengthA = 3;
        final int strengthB = 0;

        final TupleList tupleList = mock(TupleList.class);
        when(tupleList.getInvolvedParameters()).thenReturn(new int[]{0, 1, 2, 3});

        final NegativeStrengthBasedParameterCombinationFactory factory
                = new NegativeStrengthBasedParameterCombinationFactory(tupleList, strengthA);

        final Optional<List<IntSet>> combinations
                = factory.create(new int[]{0, 1, 2}, 3, strengthB);

        assertTrue(combinations.isPresent());
        assertEquals(3, combinations.get().size());
        assertTrue(combinations.get().contains(set(0, 1)));
        assertTrue(combinations.get().contains(set(0, 2)));
        assertTrue(combinations.get().contains(set(1, 2)));
    }

    @Test
    void testNextParameterImmediatelyAfterErrorConstraintWithNoInteractions() {
        final int strengthA = 2;
        final int strengthB = 1;

        final TupleList tupleList = mock(TupleList.class);
        when(tupleList.getInvolvedParameters()).thenReturn(new int[]{0, 1, 2, 3});

        final NegativeStrengthBasedParameterCombinationFactory factory
                = new NegativeStrengthBasedParameterCombinationFactory(tupleList, strengthA);

        final Optional<List<IntSet>> combinations
                = factory.create(new int[]{0, 1, 2, 3}, 4, strengthB);

        assertTrue(combinations.isPresent());
        assertEquals(6, combinations.get().size());
        assertTrue(combinations.get().contains(set(0, 1)));
        assertTrue(combinations.get().contains(set(0, 2)));
        assertTrue(combinations.get().contains(set(0, 3)));
        assertTrue(combinations.get().contains(set(1, 2)));
        assertTrue(combinations.get().contains(set(1, 3)));
        assertTrue(combinations.get().contains(set(2, 3)));
    }

    @Test
    void testNextParameterAfterErrorConstraintWithNoInteractions() {
        final int strengthA = 2;
        final int strengthB = 1;

        final TupleList tupleList = mock(TupleList.class);
        when(tupleList.getInvolvedParameters()).thenReturn(new int[]{0, 1, 2, 3});

        final NegativeStrengthBasedParameterCombinationFactory factory
                = new NegativeStrengthBasedParameterCombinationFactory(tupleList, strengthA);

        final Optional<List<IntSet>> combinations
                = factory.create(new int[]{0, 1, 2, 3, 4}, 5, strengthB);

        assertTrue(combinations.isPresent());
        assertEquals(6, combinations.get().size());
        assertTrue(combinations.get().contains(set(0, 1)));
        assertTrue(combinations.get().contains(set(0, 2)));
        assertTrue(combinations.get().contains(set(0, 3)));
        assertTrue(combinations.get().contains(set(1, 2)));
        assertTrue(combinations.get().contains(set(1, 3)));
        assertTrue(combinations.get().contains(set(2, 3)));
    }

    @Test
    void testNextParameterAfterErrorConstraintWithInteractions() {
        final int strengthA = 2;
        final int strengthB = 2;

        final TupleList tupleList = mock(TupleList.class);
        when(tupleList.getInvolvedParameters()).thenReturn(new int[]{0, 1, 2, 3});

        final NegativeStrengthBasedParameterCombinationFactory factory
                = new NegativeStrengthBasedParameterCombinationFactory(tupleList, strengthA);

        final Optional<List<IntSet>> combinations
                = factory.create(new int[]{0, 1, 2, 3, 4, 5}, 6, strengthB);

        assertTrue(combinations.isPresent());
        assertEquals(12, combinations.get().size());
        assertTrue(combinations.get().contains(set(0, 1, 4)));
        assertTrue(combinations.get().contains(set(0, 1, 5)));
        assertTrue(combinations.get().contains(set(0, 2, 4)));
        assertTrue(combinations.get().contains(set(0, 2, 5)));
        assertTrue(combinations.get().contains(set(0, 3, 4)));
        assertTrue(combinations.get().contains(set(0, 3, 5)));
        assertTrue(combinations.get().contains(set(1, 2, 4)));
        assertTrue(combinations.get().contains(set(1, 2, 5)));
        assertTrue(combinations.get().contains(set(1, 3, 4)));
        assertTrue(combinations.get().contains(set(1, 3, 5)));
        assertTrue(combinations.get().contains(set(2, 3, 4)));
        assertTrue(combinations.get().contains(set(2, 3, 5)));
    }

    private IntSet set(int ... values) {
        return new IntArraySet(values);
    }
}
