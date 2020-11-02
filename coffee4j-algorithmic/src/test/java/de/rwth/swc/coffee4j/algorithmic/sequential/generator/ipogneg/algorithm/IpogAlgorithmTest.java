package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.HardConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.constraint.NoConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.model.*;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.AlgorithmTestUtil;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class IpogAlgorithmTest {

    @Test
    void oneParameterTwoValueModel() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .parameterSizes(2)
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(
                IpogAlgorithmConfiguration.ipogConfiguration()
                        .testModel(model)
                        .constraintChecker(new NoConstraintChecker())
                        .testingStrength(1)
                        .build()
        ).generate();

        assertEquals(2, testSuite.size());
        assertEquals(1, testSuite.get(0).length);
        assertEquals(1, testSuite.get(1).length);
        assertEquals(0, testSuite.get(0)[0]);
        assertEquals(1, testSuite.get(1)[0]);
    }

    @Test
    void itShouldCoverEachValueOnceForStrengthOneWithMultipleParameters() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .parameterSizes(4, 4, 4, 4)
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(
                IpogAlgorithmConfiguration.ipogConfiguration()
                        .testModel(model)
                        .constraintChecker(new NoConstraintChecker())
                        .testingStrength(1)
                        .build()
        ).generate();

        final List<int[]> expectedTestInputs = Arrays.asList(
                new int[]{0, 0, 0, 0},
                new int[]{1, 1, 1, 1},
                new int[]{2, 2, 2, 2},
                new int[]{3, 3, 3, 3});

        assertEquals(IntArrayWrapper.wrapToSet(expectedTestInputs), IntArrayWrapper.wrapToSet(testSuite));
    }

    @Test
    void itShouldGenerateCartesianProductIfStrengthIsNumberOfParameters() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .parameterSizes(5, 5, 5, 5, 5, 5, 5)
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(
                IpogAlgorithmConfiguration.ipogConfiguration()
                        .testModel(model)
                        .constraintChecker(new NoConstraintChecker())
                        .testingStrength(7)
                        .build()
        ).generate();

        final Int2IntMap parameterMap = new Int2IntOpenHashMap(
                new int[]{0, 1, 2, 3, 4, 5, 6},
                new int[]{5, 5, 5, 5, 5, 5, 5});

        assertEquals(IntArrayWrapper.wrapToSet(Combinator.computeCartesianProduct(parameterMap, 7)), IntArrayWrapper.wrapToSet(testSuite));
    }

    @Test
    void itShouldGenerateAllNeededTestInputsIfSmallerStrength() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .parameterSizes(3, 3, 3, 3)
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(
                IpogAlgorithmConfiguration.ipogConfiguration()
                        .testModel(model)
                        .constraintChecker(new NoConstraintChecker())
                        .testingStrength(2)
                        .build()
        ).generate();

        AlgorithmTestUtil.verifyAllCombinationsPresent(testSuite, model.getParameterSizes(), 2);
    }

    @Test
    void itShouldCoverAllCombinationsIfParametersHaveDifferentSizes() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .parameterSizes(2, 5, 3, 2, 4)
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(
                IpogAlgorithmConfiguration.ipogConfiguration()
                        .testModel(model)
                        .constraintChecker(new NoConstraintChecker())
                        .testingStrength(2)
                        .build()
        ).generate();

        AlgorithmTestUtil.verifyAllCombinationsPresent(testSuite, model.getParameterSizes(), 2);
    }

    @Test
    void itShouldIncludeSeedTestCases() {
        final List<int[]> seeds = List.of(
                new int[]{1, 2, 3, 1, 2},
                new int[]{-1, -1, 0, 1, 2},
                new int[]{1, 2, 3, 0, 1},
                new int[]{0, 1, 2, -1, -1});
        final List<PrimitiveSeed> primitiveSeeds = seeds.stream()
                .map(seed -> new PrimitiveSeed(seed, SeedMode.NON_EXCLUSIVE, PrimitiveSeed.NO_PRIORITY))
                .collect(Collectors.toList());

        final CompleteTestModel model = CompleteTestModel.builder()
                .parameterSizes(2, 3, 4, 2, 3)
                .seeds(new Int2ObjectOpenHashMap<>(Map.of(-1, primitiveSeeds)))
                .build();

        final List<int[]> testSuite = new IpogAlgorithm(
                IpogAlgorithmConfiguration.ipogConfiguration()
                        .testModel(model)
                        .constraintChecker(new NoConstraintChecker())
                        .testingStrength(2)
                        .build()
        ).generate();

        AlgorithmTestUtil.verifyAllSeedsPresent(seeds, testSuite);
    }

    @Test
    void itShouldNotIncludeSeedTestsViolatingConstraints() {
        final TupleList constraint = new TupleList(1, new int[]{0, 1}, List.of(new int[]{0, 0}, new int[]{0, 1}, new int[]{1, 1}, new int[]{2, 1}));
        final List<int[]> seeds = List.of(new int[]{0, 0}, new int[]{-1, 1}, new int[]{-1, 2});

        new Int2ObjectOpenHashMap<>(Map.of(-1, seeds));

        final CompleteTestModel model = CompleteTestModel.builder()
                .parameterSizes(3, 3)
                .exclusionTupleLists(List.of(constraint))
                .build();

        final ConstraintChecker checker = new HardConstraintCheckerFactory().createConstraintChecker(model);

        final List<int[]> testSuite = new IpogAlgorithm(
                IpogAlgorithmConfiguration.ipogConfiguration()
                        .testModel(model)
                        .constraintChecker(checker)
                        .testingStrength(2)
                        .build()
        ).generate();

        assertFalse(testSuite.stream()
                .anyMatch(testCase -> CombinationUtil.contains(testCase, seeds.get(0))));
        assertFalse(testSuite.stream()
                .anyMatch(testCase -> CombinationUtil.contains(testCase, seeds.get(1))));
        assertTrue(testSuite.stream()
                .anyMatch(testCase -> CombinationUtil.contains(testCase, seeds.get(2))));
    }
}

