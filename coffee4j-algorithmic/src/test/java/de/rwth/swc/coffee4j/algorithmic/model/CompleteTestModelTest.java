package de.rwth.swc.coffee4j.algorithmic.model;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CompleteTestModelTest {
    
    @ParameterizedTest
    @MethodSource
    void preconditions(int strength, int[] parameterSizes, List<TupleList> exclusionTupleLists, List<TupleList> errorTupleLists, Class<? extends Exception> expected) {
        assertThrows(expected, () -> CompleteTestModel.builder()
                .positiveTestingStrength(strength)
                .parameterSizes(parameterSizes)
                .exclusionTupleLists(exclusionTupleLists)
                .errorTupleLists(errorTupleLists)
                .build());
    }
    
    @SuppressWarnings("unused")
    private static Stream<Arguments> preconditions() {
        return Stream.of(
                arguments(1, null, List.of(), List.of(), NullPointerException.class),
                arguments(3, new int[]{2}, List.of(), List.of(), IllegalArgumentException.class),
                arguments(1, new int[]{2, 2}, null, List.of(), NullPointerException.class),
                arguments(1, new int[]{2, 2}, List.of(), null, NullPointerException.class),
                arguments(1, new int[]{1, 2}, List.of(), List.of(), IllegalArgumentException.class),
                arguments(1, new int[]{2, -2}, List.of(), List.of(), IllegalArgumentException.class),
                arguments(1, new int[]{2, 2}, List.of(new TupleList(1, new int[]{-1, 1}, List.of(new int[]{0, 0}))), List.of(), IllegalArgumentException.class),
                arguments(1, new int[]{2, 2}, List.of(new TupleList(1, new int[]{1, 5}, List.of(new int[]{0, 0}))), List.of(), IllegalArgumentException.class),
                arguments(1, new int[]{2, 2}, List.of(), List.of(new TupleList(1, new int[]{1, 5}, List.of(new int[]{0, 0}))), IllegalArgumentException.class),
                arguments(1, new int[]{2}, List.of(new TupleList(1, new int[]{0}, List.of(new int[]{0}))), List.of(new TupleList(1, new int[]{0}, List.of(new int[]{0}))), IllegalArgumentException.class));
    }
    
    @Test
    void constructModel() {
        final List<TupleList> forbiddenTupleLists = List.of(new TupleList(1, new int[]{0}, List.of(new int[]{0})));
        final List<TupleList> errorTupleLists = List.of(new TupleList(2, new int[]{0}, List.of(new int[]{1})));
        final PrimitiveSeed seed = new PrimitiveSeed(new int[]{-1, 2}, SeedMode.NON_EXCLUSIVE, 1);
        final int[] parameterSizes = new int[]{2, 3};
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(parameterSizes)
                .exclusionTupleLists(forbiddenTupleLists)
                .errorTupleLists(errorTupleLists)
                .seeds(List.of(seed))
                .build();
        
        assertEquals(1, model.getPositiveTestingStrength());
        assertEquals(0, model.getNegativeTestingStrength());
        assertArrayEquals(parameterSizes, model.getParameterSizes());
        assertEquals(forbiddenTupleLists, model.getExclusionTupleLists());
        assertEquals(errorTupleLists, model.getErrorTupleLists());
        assertEquals(2, model.getParameterSize(0));
        assertEquals(3, model.getParameterSize(1));
        assertEquals(2, model.getNumberOfParameters());
        assertEquals(1, model.getSeeds().size());
        assertEquals(seed, model.getSeeds().get(0));
    }
    
    @Test
    void manageGroupSeeds() {
        final List<PrimitiveSeed> positiveSeeds = List.of(
                new PrimitiveSeed(new int[] {0, 0}, SeedMode.NON_EXCLUSIVE, PrimitiveSeed.NO_PRIORITY),
                new PrimitiveSeed(new int[] {0, 1}, SeedMode.EXCLUSIVE, 1));
        final List<PrimitiveSeed> firstConstraintSeeds = List.of(
                new PrimitiveSeed(new int[] {1, 0}, SeedMode.NON_EXCLUSIVE, PrimitiveSeed.NO_PRIORITY));
        final List<PrimitiveSeed> secondConstraintSeeds = List.of(
                new PrimitiveSeed(new int[] {1, 1}, SeedMode.EXCLUSIVE, -2));
        
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .negativeTestingStrength(2)
                .parameterSizes(2, 2)
                .seeds(positiveSeeds)
                .seeds(0, firstConstraintSeeds)
                .seeds(2, secondConstraintSeeds)
                .build();
    
        assertEquals(2, model.getSeeds(-1).size());
        assertEquals(1, model.getSeeds(0).size());
        assertEquals(0, model.getSeeds(1).size());
        assertEquals(1, model.getSeeds(2).size());
        assertEquals(positiveSeeds, model.getSeedsMap().get(-1));
        assertEquals(firstConstraintSeeds, model.getSeedsMap().get(0));
        assertEquals(secondConstraintSeeds, model.getSeedsMap().get(2));
        assertEquals(2, model.getSeeds().size());
        assertEquals(positiveSeeds, model.getSeeds(-1));
        assertEquals(firstConstraintSeeds, model.getSeeds(0));
        assertEquals(secondConstraintSeeds, model.getSeeds(2));
    }
    
    @Test
    void manageMixedStrengthGroups() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .negativeTestingStrength(2)
                .parameterSizes(2, 2, 3, 4, 5, 6, 7, 8, 9)
                .mixedStrengthGroups(List.of(
                        PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {1, 3, 5}))))
                .mixedStrengthGroups(1, List.of(
                        PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {1, 3, 5})),
                        PrimitiveStrengthGroup.ofStrength(new IntOpenHashSet(new int[] {1, 3, 5, 6, 7}), 3)))
                .build();
        
        assertEquals(List.of(PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {1, 3, 5}))),
                model.getMixedStrengthGroups());
        assertEquals(List.of(
                PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {1, 3, 5})),
                PrimitiveStrengthGroup.ofStrength(new IntOpenHashSet(new int[] {1, 3, 5, 6, 7}), 3)),
                model.getMixedStrengthGroups(1));
    }
    
    @Test
    void correctlyReportsWeights() {
        final Int2ObjectMap<Int2DoubleMap> someWeights = new Int2ObjectOpenHashMap<>();
        someWeights.put(1, Int2DoubleMaps.singleton(0, 2.0));
        someWeights.put(2, Int2DoubleMaps.singleton(1, 1.0));
        
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 2, 2)
                .weights(someWeights)
                .weight(0, 0, 1.0)
                .weight(0, 1, 2.0)
                .build();
        
        assertEquals(2.0, model.getWeight(1, 0));
        assertEquals(2.0, model.getWeight(1, 0, -1));
        assertEquals(1.0, model.getWeight(2, 1));
        assertEquals(1.0, model.getWeight(2, 1, -1));
        assertEquals(1.0, model.getWeight(0, 0));
        assertEquals(1.0, model.getWeight(0, 0, -1));
        assertEquals(2.0, model.getWeight(0, 1));
        assertEquals(2.0, model.getWeight(0, 1, -1));
        
        assertEquals(0, model.getWeight(1, 1));
        assertEquals(-1.0, model.getWeight(1, 1, -1));
    }
    
}
