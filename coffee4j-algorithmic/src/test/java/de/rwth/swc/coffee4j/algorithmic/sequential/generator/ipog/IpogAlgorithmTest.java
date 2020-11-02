package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.HardConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.GroupSpecificTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveSeed;
import de.rwth.swc.coffee4j.algorithmic.model.SeedMode;
import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveStrengthGroup;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.AlgorithmTestUtil;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IpogAlgorithmTest {
    
    @Test
    void oneParameterTwoValueModel() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2)
                .build();
        
        final List<int[]> testSuite = new IpogAlgorithm(model).generate();
        
        assertEquals(2, testSuite.size());
        assertEquals(1, testSuite.get(0).length);
        assertEquals(1, testSuite.get(1).length);
        assertEquals(0, testSuite.get(0)[0]);
        assertEquals(1, testSuite.get(1)[0]);
    }
    
    @Test
    void testSuiteIsEmptyForTestingStrengthZero() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(0)
                .parameterSizes(2, 3, 4)
                .build();
        
        final List<int[]> testSuite = new IpogAlgorithm(model).generate();
        
        assertTrue(testSuite.isEmpty());
    }
    
    @Test
    void itShouldCoverEachValueOnceForStrengthOneWithMultipleParameters() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(4, 4, 4, 4)
                .build();
        
        final List<int[]> testSuite = new IpogAlgorithm(model).generate();
        final List<int[]> expectedTestInputs = Arrays.asList(new int[]{0, 0, 0, 0}, new int[]{1, 1, 1, 1}, new int[]{2, 2, 2, 2}, new int[]{3, 3, 3, 3});
        
        Assertions.assertEquals(IntArrayWrapper.wrapToSet(expectedTestInputs), IntArrayWrapper.wrapToSet(testSuite));
    }
    
    @Test
    void itShouldGenerateCartesianProductIfStrengthIsNumberOfParameters() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(7)
                .parameterSizes(5, 5, 5, 5, 5, 5, 5)
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(model).generate();
        final Int2IntMap parameterMap = new Int2IntOpenHashMap(new int[]{0, 1, 2, 3, 4, 5, 6}, new int[]{5, 5, 5, 5, 5, 5, 5});

        assertEquals(IntArrayWrapper.wrapToSet(Combinator.computeCartesianProduct(parameterMap, 7)), IntArrayWrapper.wrapToSet(testSuite));
    }
    
    @Test
    void itShouldGenerateAllNeededTestInputsIfSmallerStrength() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3, 3)
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(model).generate();

        AlgorithmTestUtil.verifyAllCombinationsPresent(testSuite, model.getParameterSizes(), 2);
    }

    @Test
    void itShouldCoverAllCombinationsIfParametersHaveDifferentSizes() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 5, 3, 2, 4)
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(model).generate();
        
        AlgorithmTestUtil.verifyAllCombinationsPresent(testSuite, model.getParameterSizes(), 2);
    }
    
    @Test
    void itShouldIncludeSeedTestCases() {
        final List<PrimitiveSeed> seeds = List.of(
                new PrimitiveSeed(new int[]{1, 2, 3, 1, 2}, SeedMode.NON_EXCLUSIVE, PrimitiveSeed.NO_PRIORITY),
                new PrimitiveSeed(new int[]{-1, -1, 0, 1, 2}, SeedMode.NON_EXCLUSIVE, PrimitiveSeed.NO_PRIORITY),
                new PrimitiveSeed(new int[]{1, 2, 3, 0, 1}, SeedMode.NON_EXCLUSIVE, PrimitiveSeed.NO_PRIORITY),
                new PrimitiveSeed(new int[]{0, 1, 2, -1, -1}, SeedMode.NON_EXCLUSIVE, PrimitiveSeed.NO_PRIORITY));
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 3, 4, 2, 3)
                .seeds(seeds)
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(model).generate();
        
        AlgorithmTestUtil.verifyAllSeedsPresent(seeds.stream()
                .map(PrimitiveSeed::getCombination)
                .collect(Collectors.toList()), testSuite);
    }
    
    @Test
    void itShouldNotIncludeSeedTestsViolatingConstraints() {
        final TupleList constraint = new TupleList(1, new int[]{0, 1}, List.of(
                new int[]{0, 0},
                new int[]{0, 1},
                new int[]{1, 1},
                new int[]{2, 1}));
        final List<PrimitiveSeed> seeds = List.of(
                new PrimitiveSeed(new int[] {0, 0}, SeedMode.NON_EXCLUSIVE, PrimitiveSeed.NO_PRIORITY),
                new PrimitiveSeed(new int[] {-1, 1}, SeedMode.NON_EXCLUSIVE, PrimitiveSeed.NO_PRIORITY),
                new PrimitiveSeed(new int[] {-1, 2}, SeedMode.NON_EXCLUSIVE, PrimitiveSeed.NO_PRIORITY));
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(3, 3)
                .exclusionTupleLists(List.of(constraint))
                .seeds(seeds)
                .build();
        
        final ConstraintChecker checker = new HardConstraintCheckerFactory().createConstraintChecker(model);
        final TestModel groupModel = GroupSpecificTestModel.positive(model, checker);
        
        final List<int[]> testSuite = new IpogAlgorithm(groupModel).generate();
        
        assertFalse(testSuite.stream()
                .anyMatch(testCase -> CombinationUtil.contains(testCase, seeds.get(0).getCombination())));
        assertFalse(testSuite.stream()
                .anyMatch(testCase -> CombinationUtil.contains(testCase, seeds.get(1).getCombination())));
        assertTrue(testSuite.stream()
                .anyMatch(testCase -> CombinationUtil.contains(testCase, seeds.get(2).getCombination())));
    }
    
    @Test
    void itShouldTestParametersAtHigherMixedStrengthIfStrengthIsZero() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(0)
                .parameterSizes(4, 4, 4, 4, 4)
                .mixedStrengthGroups(Set.of(
                        PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {2, 3}))))
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(model).generate();

        assertFalse(testSuite.isEmpty());
        AlgorithmTestUtil.verifyAllSeedsPresent(List.of(
                new int[] {-1, -1, 0, 0, -1}, new int[] {-1, -1, 0, 1, -1}, new int[] {-1, -1, 0, 2, -1},
                new int[] {-1, -1, 0, 3, -1}, new int[] {-1, -1, 1, 0, -1}, new int[] {-1, -1, 1, 1, -1},
                new int[] {-1, -1, 1, 2, -1}, new int[] {-1, -1, 1, 3, -1}, new int[] {-1, -1, 2, 0, -1},
                new int[] {-1, -1, 2, 1, -1}, new int[] {-1, -1, 2, 2, -1}, new int[] {-1, -1, 2, 3, -1},
                new int[] {-1, -1, 3, 0, -1}, new int[] {-1, -1, 3, 1, -1}, new int[] {-1, -1, 3, 2, -1},
                new int[] {-1, -1, 3, 3, -1}), testSuite);
    }
    
    @Test
    void itShouldTestMultipleIndependentGroupsAtHigherMixedStrengthIfStrengthIsOne() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2, 2, 2, 2)
                .mixedStrengthGroups(Set.of(
                        PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {2, 3})),
                        PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {0, 4}))))
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(model).generate();
    
        assertFalse(testSuite.isEmpty());
        AlgorithmTestUtil.verifyAllSeedsPresent(List.of(
                new int[] {-1, -1, 0, 0, -1}, new int[] {-1, -1, 0, 1, -1}, new int[] {-1, -1, 1, 0, -1},
                new int[] {-1, -1, 1, 1, -1}, new int[] {0, -1, -1, -1, 0}, new int[] {0, -1, -1, -1, 1},
                new int[] {1, -1, -1, -1, 0}, new int[] {1, -1, -1, -1, 1}), testSuite);
    }
    
    @Test
    void itShouldTestMultipleIndependentGroupsAtHigherMixedStrengthIfStrengthIsZero() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(0)
                .parameterSizes(2, 2, 2, 2, 2)
                .mixedStrengthGroups(Set.of(
                        PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {2, 3})),
                        PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {0, 4}))))
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(model).generate();
        
        assertFalse(testSuite.isEmpty());
        AlgorithmTestUtil.verifyAllSeedsPresent(List.of(
                new int[] {-1, -1, 0, 0, -1}, new int[] {-1, -1, 0, 1, -1}, new int[] {-1, -1, 1, 0, -1},
                new int[] {-1, -1, 1, 1, -1}, new int[] {0, -1, -1, -1, 0}, new int[] {0, -1, -1, -1, 1},
                new int[] {1, -1, -1, -1, 0}, new int[] {1, -1, -1, -1, 1}), testSuite);
    }
    
    @Test
    void itShouldTestOverlappingMixedStrengthGroupToHighestStrengthGroup() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2, 2, 2, 2)
                .mixedStrengthGroups(Set.of(
                        PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {2, 3})),
                        PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {2, 3, 4})),
                        PrimitiveStrengthGroup.ofStrength(new IntOpenHashSet(new int[] {2, 3, 4}), 2)))
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(model).generate();
    
        assertFalse(testSuite.isEmpty());
        AlgorithmTestUtil.verifyAllSeedsPresent(List.of(
                new int[] {-1, -1, 0, 0, 0}, new int[] {-1, -1, 0, 0, 1}, new int[] {-1, -1, 0, 1, 0},
                new int[] {-1, -1, 0, 1, 1}, new int[] {-1, -1, 1, 0, 0}, new int[] {-1, -1, 1, 0, 1},
                new int[] {-1, -1, 1, 1, 0}, new int[] {-1, -1, 1, 1, 1}), testSuite);
    }
    
    @Test
    void itShouldTestMultipleOverlappingMixedStrengthGroupStrengthGroup() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2, 2, 2, 2)
                .mixedStrengthGroups(Set.of(
                        PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {2, 3})),
                        PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {3, 4}))))
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(model).generate();
        
        assertFalse(testSuite.isEmpty());
        AlgorithmTestUtil.verifyAllSeedsPresent(List.of(
                new int[] {-1, -1, 0, 0, -1}, new int[] {-1, -1, 0, 1, -1}, new int[] {-1, -1, 1, 0, -1},
                new int[] {-1, -1, 1, 1, -1}, new int[] {-1, -1, -1, 0, 0}, new int[] {-1, -1, -1, 0, 1},
                new int[] {-1, -1, -1, 1, 0}, new int[] {-1, -1, -1, 1, 1}), testSuite);
    }

}
