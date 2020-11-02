package de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg.advanced;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveStrengthGroup;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MixedStrengthCoverageMapTest {
    
    private static final CompleteTestModel COMPLEX_MODEL = CompleteTestModel.builder()
            .positiveTestingStrength(2)
            .parameterSizes(2, 3, 4, 2, 3, 4)
            .mixedStrengthGroups(Set.of(
                    PrimitiveStrengthGroup.ofStrength(new IntOpenHashSet(new int[] {0, 2, 3, 4}), 3)))
            .exclusionTupleLists(Set.of(
                    new TupleList(1, new int[] {2, 3}, List.of(new int[] {0, 1})),
                    new TupleList(2, new int[] {0, 1}, List.of(new int[] {1, 1}))))
            .build();
    
    @Test
    void checkAfterConstruction() {
        final MixedStrengthCoverageMap coverageMap = new MixedStrengthCoverageMap(CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 3, 4, 5)
                .build());
        
        assertTrue(coverageMap.hasUncoveredCombinations());
        assertEquals(71, coverageMap.getNumberOfUncoveredCombinations(new int[] {-1, -1, -1, -1}));
    }
    
    @Test
    void checkAfterConstructionWithConstraintsAndMixedStrength() {
        final MixedStrengthCoverageMap coverageMap = new MixedStrengthCoverageMap(COMPLEX_MODEL);
        
        assertTrue(coverageMap.hasUncoveredCombinations());
        assertEquals(159, coverageMap.getNumberOfUncoveredCombinations(new int[] {-1, -1, -1, -1, -1, -1}));
    }
    
    @ParameterizedTest
    @MethodSource("checkHasUncoveredCombinations")
    void checkHasUncoveredCombinations(int[] testCase, int expectedNumberOfUncoveredCombinations) {
        final MixedStrengthCoverageMap coverageMap = new MixedStrengthCoverageMap(COMPLEX_MODEL);
        
        assertEquals(expectedNumberOfUncoveredCombinations, coverageMap.getNumberOfUncoveredCombinations(testCase));
    }
    
    private static Stream<Arguments> checkHasUncoveredCombinations() {
        return Stream.of(
                Arguments.of(new int[] {0, -1, -1, -1, -1, -1}, 128),
                Arguments.of(new int[] {1, -1, -1, -1, -1, -1}, 127),
                Arguments.of(new int[] {-1, 0, -1, -1, -1, -1}, 130),
                Arguments.of(new int[] {-1, 1, -1, -1, -1, -1}, 129),
                Arguments.of(new int[] {-1, 2, -1, -1, -1, -1}, 130),
                Arguments.of(new int[] {-1, -1, -1, 0, -1, -1}, 131),
                Arguments.of(new int[] {-1, -1, -1, 1, -1, -1}, 126),
                Arguments.of(new int[] {1, -1, -1, 1, -1, -1}, 101));
    }
    
    @Test
    void coversCombinations() {
        final MixedStrengthCoverageMap coverageMap = new MixedStrengthCoverageMap(COMPLEX_MODEL);
        
        coverageMap.updateSubCombinationCoverage(new int[] {0, 0, 0, 0, 0, 0});
        assertEquals(146, coverageMap.getNumberOfUncoveredCombinations(new int[] {-1, -1, -1, -1, -1, -1}));
        coverageMap.updateSubCombinationCoverage(new int[] {1, 2, 1, 1, 1, 1});
        assertEquals(133, coverageMap.getNumberOfUncoveredCombinations(new int[] {-1, -1, -1, -1, -1, -1}));
        coverageMap.updateSubCombinationCoverage(new int[] {0, 0, 1, 0, 0, 0});
        assertEquals(128, coverageMap.getNumberOfUncoveredCombinations(new int[] {-1, -1, -1, -1, -1, -1}));
        coverageMap.updateSubCombinationCoverage(new int[] {0, 2, 3, 0, 0, 0});
        assertEquals(119, coverageMap.getNumberOfUncoveredCombinations(new int[] {-1, -1, -1, -1, -1, -1}));
    }
    
}
