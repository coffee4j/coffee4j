package de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg.advanced;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveSeed;
import de.rwth.swc.coffee4j.algorithmic.model.SeedMode;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SeedCoverageMapTest {
    
    @Test
    void returnsPrioritizedBeforeUnprioritizedSeeds() {
        final TestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(4)
                .seeds(List.of(
                        new PrimitiveSeed(new int[] {0}, SeedMode.NON_EXCLUSIVE, PrimitiveSeed.NO_PRIORITY),
                        new PrimitiveSeed(new int[] {1}, SeedMode.NON_EXCLUSIVE, 1),
                        new PrimitiveSeed(new int[] {2}, SeedMode.NON_EXCLUSIVE, PrimitiveSeed.NO_PRIORITY),
                        new PrimitiveSeed(new int[] {3}, SeedMode.NON_EXCLUSIVE, 2)))
                .build();
        
        final SeedCoverageMap coverageMap = new SeedCoverageMap(model);
        
        final int[] firstSeed = coverageMap.getMostImportantPartialTestCase();
        assertArrayEquals(new int[] {3}, firstSeed);
        final int[] secondSeed = coverageMap.getMostImportantPartialTestCase();
        assertArrayEquals(new int[] {1}, secondSeed);
        
        final int[] thirdSeed = coverageMap.getMostImportantPartialTestCase();
        final int[] fourthSeed = coverageMap.getMostImportantPartialTestCase();
        
        if (Arrays.equals(new int[] {0}, thirdSeed)) {
            assertArrayEquals(new int[] {2}, fourthSeed);
        } else {
            assertArrayEquals(new int[] {2}, thirdSeed);
            assertArrayEquals(new int[] {0}, fourthSeed);
        }
    }
    
    @Test
    void returnsEmptyTestCaseIfNoSeedIsGiven() {
        final TestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2)
                .build();
    
        final SeedCoverageMap coverageMap = new SeedCoverageMap(model);
        
        final int[] testCase = coverageMap.getMostImportantPartialTestCase();
        
        assertArrayEquals(new int[] {-1}, testCase);
    }
    
    @Test
    void returnsEmptyTestCaseAfterAllSeedsCovered() {
        final TestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2)
                .seeds(List.of(
                        new PrimitiveSeed(new int[] {1}, SeedMode.NON_EXCLUSIVE, 2)))
                .build();
        
        final SeedCoverageMap coverageMap = new SeedCoverageMap(model);
        
        final int[] firstTestCase = coverageMap.getMostImportantPartialTestCase();
        assertArrayEquals(new int[] {1}, firstTestCase);
        final int[] secondTestCase = coverageMap.getMostImportantPartialTestCase();
        assertArrayEquals(new int[] {-1}, secondTestCase);
    }
    
    @Test
    void combinesMultipleSeedsWithOnlyOneSuspicious() {
        final TestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .seeds(List.of(
                        new PrimitiveSeed(new int[] {0, -1}, SeedMode.NON_EXCLUSIVE, 0.5),
                        new PrimitiveSeed(new int[] {-1, 0}, SeedMode.EXCLUSIVE, 2),
                        new PrimitiveSeed(new int[] {-1, 1}, SeedMode.NON_EXCLUSIVE, 1),
                        new PrimitiveSeed(new int[] {1, -1}, SeedMode.EXCLUSIVE, PrimitiveSeed.NO_PRIORITY)))
                .build();
    
        final SeedCoverageMap coverageMap = new SeedCoverageMap(model);
        
        final int[] firstTestCase = coverageMap.getMostImportantPartialTestCase();
        assertArrayEquals(new int[] {0, 0}, firstTestCase);
        final int[] secondTestCase = coverageMap.getMostImportantPartialTestCase();
        assertArrayEquals(new int[] {1, 1}, secondTestCase);
    }
    
    @Test
    void doesNotCombineMultipleSuspiciousSeeds() {
        final TestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .seeds(List.of(
                        new PrimitiveSeed(new int[] {0, -1}, SeedMode.EXCLUSIVE, 1),
                        new PrimitiveSeed(new int[] {-1, 0}, SeedMode.EXCLUSIVE, 2)))
                .build();
    
        final SeedCoverageMap coverageMap = new SeedCoverageMap(model);
        
        final int[] firstSeed = coverageMap.getMostImportantPartialTestCase();
        assertArrayEquals(new int[] {-1, 0}, firstSeed);
        final int[] secondSeed = coverageMap.getMostImportantPartialTestCase();
        assertArrayEquals(new int[] {0, -1}, secondSeed);
    }
    
    @Test
    void respectsConstraintsForCombinationOfSeeds() {
        final TestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .seeds(List.of(
                        new PrimitiveSeed(new int[] {0, -1}, SeedMode.NON_EXCLUSIVE, 1),
                        new PrimitiveSeed(new int[] {-1, 0}, SeedMode.NON_EXCLUSIVE, 2)))
                .exclusionTupleLists(List.of(
                        new TupleList(1, new int[] {0, 1}, List.of(new int[] {0, 0}))))
                .build();
    
        final SeedCoverageMap coverageMap = new SeedCoverageMap(model);
    
        final int[] firstSeed = coverageMap.getMostImportantPartialTestCase();
        assertArrayEquals(new int[] {-1, 0}, firstSeed);
        final int[] secondSeed = coverageMap.getMostImportantPartialTestCase();
        assertArrayEquals(new int[] {0, -1}, secondSeed);
    }
    
}
