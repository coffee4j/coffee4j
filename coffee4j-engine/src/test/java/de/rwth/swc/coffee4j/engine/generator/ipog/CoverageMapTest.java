package de.rwth.swc.coffee4j.engine.generator.ipog;

import de.rwth.swc.coffee4j.engine.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.engine.constraint.NoConstraintChecker;
import de.rwth.swc.coffee4j.engine.util.Combinator;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link CoverageMap}.
 */
class CoverageMapTest {
    
    private static final ConstraintChecker NO_CONSTRAINTS_CHECKER = new NoConstraintChecker();
    
    private static final int FIRST_PARAMETER = 0;
    private static final int SECOND_PARAMETER = 1;
    private static final int FIRST_PARAMETER_SIZE = 1;
    private static final int SECOND_PARAMETER_SIZE = 2;
    
    private static final IntSet FIRST_PARAMETER_COMBINATION = new IntOpenHashSet(Collections.singletonList(FIRST_PARAMETER));
    
    private static final Int2IntMap PARAMETERS = new Int2IntOpenHashMap(new int[]{FIRST_PARAMETER, SECOND_PARAMETER}, new int[]{FIRST_PARAMETER_SIZE, SECOND_PARAMETER_SIZE});
    
    @Test
    void combinationsAndParametersCannotBeNull() {
        assertThrows(NullPointerException.class, () -> new CoverageMap(null, 0, new Int2IntOpenHashMap(), NO_CONSTRAINTS_CHECKER));
        assertThrows(NullPointerException.class, () -> new CoverageMap(new ArrayList<>(), 0, null, NO_CONSTRAINTS_CHECKER));
    }
    
    @Test
    void hasUncoveredCombinationsAtBeginning() {
        CoverageMap coverageMap = new CoverageMap(Collections.singletonList(FIRST_PARAMETER_COMBINATION), SECOND_PARAMETER, PARAMETERS, NO_CONSTRAINTS_CHECKER);
        assertTrue(coverageMap.hasUncoveredCombinations());
        int[] expectedCombination = new int[]{0, 0};
        Optional<int[]> uncoveredCombination = coverageMap.getUncoveredCombination();
        assertTrue(uncoveredCombination.isPresent());
        assertArrayEquals(expectedCombination, uncoveredCombination.orElse(new int[0]));
    }
    
    @Test
    void hasNoUncoveredCombinationsIfAllCombinationsCovered() {
        CoverageMap coverageMap = noUncoveredCoverageMap();
        assertFalse(coverageMap.hasUncoveredCombinations());
        Optional<int[]> uncoveredCombination = coverageMap.getUncoveredCombination();
        assertFalse(uncoveredCombination.isPresent());
    }
    
    private CoverageMap noUncoveredCoverageMap() {
        CoverageMap coverageMap = new CoverageMap(Collections.singletonList(FIRST_PARAMETER_COMBINATION), SECOND_PARAMETER, PARAMETERS, NO_CONSTRAINTS_CHECKER);
        coverageMap.markAsCovered(new int[]{0, 0});
        coverageMap.markAsCovered(new int[]{0, 1});
        coverageMap.markAsCovered(new int[]{0, 2});
        coverageMap.markAsCovered(new int[]{1, 0});
        coverageMap.markAsCovered(new int[]{1, 1});
        coverageMap.markAsCovered(new int[]{1, 2});
        return coverageMap;
    }
    
    @Test
    void markedAsCoveredNoLongerReturnedAsUncovered() {
        CoverageMap coverageMap = new CoverageMap(Collections.singletonList(FIRST_PARAMETER_COMBINATION), SECOND_PARAMETER, PARAMETERS, NO_CONSTRAINTS_CHECKER);
        coverageMap.markAsCovered(new int[]{0, 0});
        assertFalse(Arrays.equals(new int[]{0, 0}, coverageMap.getUncoveredCombination().orElse(new int[0])));
    }
    
    @Test
    void gainsZeroIfAllCombinationsCovered() {
        CoverageMap coverageMap = noUncoveredCoverageMap();
        int[] partialCombination = new int[]{0, -1};
        int[] gains = coverageMap.computeGainsOfFixedParameter(partialCombination);
        assertEquals(2, gains.length);
        assertEquals(0, gains[0]);
        assertEquals(0, gains[1]);
    }
    
    @Test
    void computeGainsIfSomeCombinationsCovered() {
        CoverageMap coverageMap = new CoverageMap(Collections.singletonList(FIRST_PARAMETER_COMBINATION), SECOND_PARAMETER, PARAMETERS, NO_CONSTRAINTS_CHECKER);
        coverageMap.markAsCovered(new int[]{0, 0});
        int[] gains = coverageMap.computeGainsOfFixedParameter(new int[]{0, -1});
        assertEquals(2, gains.length);
        assertEquals(0, gains[0]);
        assertEquals(1, gains[1]);
    }
    
    @Test
    void uncoveredCombinationsWithNegativeParameterIndices() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2}, new int[]{2, 2, 2});
        
        int[] oldParameters = new int[]{0, 1};
        int[] negativeParameters = {0};
        int strength = 1;
        
        List<IntSet> parameterCombinations = Combinator.computeNegativeParameterCombinations(oldParameters, negativeParameters, strength - 1);
        
        CoverageMap map = new CoverageMap(parameterCombinations, 2, parameters, new NoConstraintChecker());
        map.markAsCovered(new int[]{0, 0, 0});
        map.markAsCovered(new int[]{0, 0, 1});
        map.markAsCovered(new int[]{1, 0, 0});
        map.markAsCovered(new int[]{1, 0, 1});
        
        assertFalse(map.hasUncoveredCombinations());
    }
    
    @Test
    void ensuresCoverageOfFixedParameterEvenWithNoParameterCombinations() {
        final Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{4, 4, 4, 4});
        final CoverageMap coverageMap = new CoverageMap(Collections.emptySet(), 2, parameters, new NoConstraintChecker());
        
        assertTrue(coverageMap.hasUncoveredCombinations());
        coverageMap.markAsCovered(new int[]{-1, -1, 0, -1});
        assertTrue(coverageMap.hasUncoveredCombinations());
        coverageMap.markAsCovered(new int[]{-1, -1, 1, -1});
        assertTrue(coverageMap.hasUncoveredCombinations());
        coverageMap.markAsCovered(new int[]{-1, -1, 2, -1});
        assertTrue(coverageMap.hasUncoveredCombinations());
        coverageMap.markAsCovered(new int[]{-1, -1, 3, -1});
        assertFalse(coverageMap.hasUncoveredCombinations());
    }
    
    @Test
    void markAsCoveredThrowsNoExceptionIfFixedParameterNotSet() {
        final Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{4, 4, 4, 4});
        final CoverageMap coverageMap = new CoverageMap(Collections.emptySet(), 2, parameters, new NoConstraintChecker());
        assertArrayEquals(new int[]{1, 1, 1, 1}, coverageMap.computeGainsOfFixedParameter(new int[]{-1, -1, -1, -1}));
        coverageMap.markAsCovered(new int[]{-1, -1, -1, -1});
        assertArrayEquals(new int[]{1, 1, 1, 1}, coverageMap.computeGainsOfFixedParameter(new int[]{-1, -1, -1, -1}));
    }
    
}
