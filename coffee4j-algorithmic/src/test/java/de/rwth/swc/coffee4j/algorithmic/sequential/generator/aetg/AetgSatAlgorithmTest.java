package de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.DynamicHardConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.GroupSpecificTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AetgSatAlgorithmTest {

    private static void verifyAllCombinationsPresent(List<int[]> testSuite, int[] parameterSizes, int strength) {
        final List<IntSet> parameterCombinations = Combinator.computeParameterCombinations(IntStream.range(0, parameterSizes.length).toArray(), strength);

        for (IntSet parameterCombination : parameterCombinations) {
            final List<int[]> combinations = computeCartesianProduct(parameterCombination, parameterSizes);

            for (int[] combination : combinations) {
                assertTrue(containsCombination(testSuite, combination), () -> "" + Arrays.toString(combination) + " missing.");
            }
        }
    }

    private static List<int[]> computeCartesianProduct(IntSet parameterCombination, int[] parameterSizes) {
        final Int2IntMap parameterSizeMap = new Int2IntOpenHashMap(parameterSizes.length);

        for (int parameter : parameterCombination) {
            parameterSizeMap.put(parameter, parameterSizes[parameter]);
        }

        return Combinator.computeCartesianProduct(parameterSizeMap, parameterSizes.length);
    }

    private static boolean containsCombination(List<int[]> testSuite, int[] combination) {
        for (int[] testInput : testSuite) {
            if (CombinationUtil.contains(testInput, combination)) {
                return true;
            }
        }
        return false;
    }

    @Test
    void oneParameterTwoValueModel() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2)
                .build();
        final TestModel groupModel = constructPositiveGroupModel(model);
        
        final List<int[]> testSuite = new AetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration()
                .model(groupModel)
                .build())
                .generate();

        assertEquals(2, testSuite.size());
        assertEquals(1, testSuite.get(0).length);
        assertEquals(1, testSuite.get(1).length);
    }
    
    private static TestModel constructPositiveGroupModel(CompleteTestModel completeTestModel) {
        final ConstraintChecker constraintChecker = new DynamicHardConstraintChecker(completeTestModel,
                completeTestModel.getExclusionConstraints(), completeTestModel.getErrorConstraints());
    
        return GroupSpecificTestModel.positive(completeTestModel, constraintChecker);
    }

    @Test
    void itShouldCoverEachValueOnceForStrengthOneWithMultipleParameters() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(4, 4, 4, 4)
                .build();
        final TestModel groupModel = constructPositiveGroupModel(model);

        final List<int[]> testSuite = new AetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration()
                .model(groupModel)
                .build())
                .generate();

        verifyAllCombinationsPresent(testSuite, groupModel.getParameterSizes(), 1);
    }

    @Test
    void itShouldGenerateAllNeededTestInputsIfSmallerStrength() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3, 3)
                .build();
        final TestModel groupModel = constructPositiveGroupModel(model);

        final List<int[]> testSuite = new AetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration()
                .model(groupModel)
                .build())
                .generate();

        verifyAllCombinationsPresent(testSuite, groupModel.getParameterSizes(), 2);
    }

    @Test
    void itShouldCoverAllCombinationsIfParametersHaveDifferentSizes() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 5, 3, 2, 4)
                .build();
        final TestModel groupModel = constructPositiveGroupModel(model);

        final List<int[]> testSuite = new AetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration()
                .model(groupModel)
                .build())
                .generate();

        verifyAllCombinationsPresent(testSuite, groupModel.getParameterSizes(), 2);
    }

}
