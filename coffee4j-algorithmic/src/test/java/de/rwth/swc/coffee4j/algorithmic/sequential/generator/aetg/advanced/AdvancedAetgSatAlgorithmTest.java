package de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg.advanced;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.DynamicHardConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.GroupSpecificTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveSeed;
import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveStrengthGroup;
import de.rwth.swc.coffee4j.algorithmic.model.SeedMode;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg.AetgSatConfiguration;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static de.rwth.swc.coffee4j.algorithmic.sequential.generator.AlgorithmTestUtil.verifyAllSeedsPresent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedAetgSatAlgorithmTest {

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
        
        final List<int[]> testSuite = new AdvancedAetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration()
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

        final List<int[]> testSuite = new AdvancedAetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration()
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

        final List<int[]> testSuite = new AdvancedAetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration()
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

        final List<int[]> testSuite = new AdvancedAetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration()
                .model(groupModel)
                .build())
                .generate();

        verifyAllCombinationsPresent(testSuite, groupModel.getParameterSizes(), 2);
    }
    
    @Test
    void itShouldCoverMixedStrengthGroups() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 5, 3, 2, 4)
                .mixedStrengthGroups(Set.of(
                        PrimitiveStrengthGroup.ofStrength(new IntOpenHashSet(new int[] {0, 2, 3, 4}), 3)))
                .build();
        final TestModel groupModel = constructPositiveGroupModel(model);
    
        final List<int[]> testSuite = new AdvancedAetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration()
                .model(groupModel)
                .build())
                .generate();
        
        verifyAllCombinationsPresent(testSuite, groupModel.getParameterSizes(), 2);
        verifyAllSeedsPresent(Set.of(
                new int[] {0, -1, 0, 0, -1}, new int[] {0, -1, 0, 1, -1}, new int[] {0, -1, 1, 0, -1},
                new int[] {0, -1, 1, 1, -1}, new int[] {0, -1, 2, 0, -1}, new int[] {0, -1, 2, 1, -1},
                new int[] {1, -1, 0, 0, -1}, new int[] {1, -1, 0, 1, -1}, new int[] {1, -1, 1, 0, -1},
                new int[] {1, -1, 1, 1, -1}, new int[] {1, -1, 2, 0, -1}, new int[] {1, -1, 2, 1, -1},
                
                new int[] {0, -1, 0, -1, 0}, new int[] {0, -1, 0, -1, 1}, new int[] {0, -1, 0, -1, 2},
                new int[] {0, -1, 0, -1, 3}, new int[] {0, -1, 1, -1, 0}, new int[] {0, -1, 1, -1, 1},
                new int[] {0, -1, 1, -1, 2}, new int[] {0, -1, 1, -1, 3}, new int[] {0, -1, 2, -1, 0},
                new int[] {0, -1, 2, -1, 1}, new int[] {0, -1, 2, -1, 2}, new int[] {0, -1, 2, -1, 3},
                new int[] {1, -1, 0, -1, 0}, new int[] {1, -1, 0, -1, 1}, new int[] {1, -1, 0, -1, 2},
                new int[] {1, -1, 0, -1, 3}, new int[] {1, -1, 1, -1, 0}, new int[] {1, -1, 1, -1, 1},
                new int[] {1, -1, 1, -1, 2}, new int[] {1, -1, 1, -1, 3}, new int[] {1, -1, 2, -1, 0},
                new int[] {1, -1, 2, -1, 1}, new int[] {1, -1, 2, -1, 2}, new int[] {1, -1, 2, -1, 3},
                
                new int[] {0, -1, -1, 0, 0}, new int[] {0, -1, -1, 0, 1}, new int[] {0, -1, -1, 0, 2},
                new int[] {0, -1, -1, 0, 3}, new int[] {0, -1, -1, 1, 0}, new int[] {0, -1, -1, 1, 1},
                new int[] {0, -1, -1, 1, 2}, new int[] {0, -1, -1, 1, 3}, new int[] {1, -1, -1, 0, 0},
                new int[] {1, -1, -1, 0, 1}, new int[] {1, -1, -1, 0, 2}, new int[] {1, -1, -1, 0, 3},
                new int[] {1, -1, -1, 1, 0}, new int[] {1, -1, -1, 1, 1}, new int[] {1, -1, -1, 1, 2},
                new int[] {1, -1, -1, 1, 3},
                
                new int[] {-1, -1, 0, 0, 0}, new int[] {-1, -1, 0, 0, 1}, new int[] {-1, -1, 0, 0, 2},
                new int[] {-1, -1, 0, 0, 3}, new int[] {-1, -1, 0, 1, 0}, new int[] {-1, -1, 0, 1, 1},
                new int[] {-1, -1, 0, 1, 2}, new int[] {-1, -1, 0, 1, 3}, new int[] {-1, -1, 1, 0, 0},
                new int[] {-1, -1, 1, 0, 1}, new int[] {-1, -1, 1, 0, 2}, new int[] {-1, -1, 1, 0, 3},
                new int[] {-1, -1, 1, 1, 0}, new int[] {-1, -1, 1, 1, 1}, new int[] {-1, -1, 1, 1, 2},
                new int[] {-1, -1, 1, 1, 3}, new int[] {-1, -1, 2, 0, 0}, new int[] {-1, -1, 2, 0, 1},
                new int[] {-1, -1, 2, 0, 2}, new int[] {-1, -1, 2, 0, 3}, new int[] {-1, -1, 2, 1, 0},
                new int[] {-1, -1, 2, 1, 1}, new int[] {-1, -1, 2, 1, 2}, new int[] {-1, -1, 2, 1, 3}
        ), testSuite);
    }
    
    @Test
    void itShouldRespectSeeds() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2, 2, 2)
                .seeds(List.of(
                        new PrimitiveSeed(new int[] {0, -1, -1, -1}, SeedMode.NON_EXCLUSIVE, 0.5),
                        new PrimitiveSeed(new int[] {-1, 0, -1, -1}, SeedMode.EXCLUSIVE, 2),
                        new PrimitiveSeed(new int[] {-1, 1, -1, -1}, SeedMode.NON_EXCLUSIVE, 1),
                        new PrimitiveSeed(new int[] {1, -1, -1, -1}, SeedMode.EXCLUSIVE, PrimitiveSeed.NO_PRIORITY)))
                .build();
        final TestModel groupModel = constructPositiveGroupModel(model);
    
        final List<int[]> testSuite = new AdvancedAetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration()
                .model(groupModel)
                .build())
                .generate();
        
        verifyAllCombinationsPresent(testSuite, groupModel.getParameterSizes(), 1);
        assertTrue(CombinationUtil.contains(testSuite.get(0), new int[] {0, -1, -1, -1}));
        assertTrue(CombinationUtil.contains(testSuite.get(0), new int[] {-1, 0, -1, -1}));
        assertTrue(CombinationUtil.contains(testSuite.get(1), new int[] {-1, 1, -1, -1}));
        assertTrue(CombinationUtil.contains(testSuite.get(1), new int[] {1, -1, -1, -1}));
    }

}
