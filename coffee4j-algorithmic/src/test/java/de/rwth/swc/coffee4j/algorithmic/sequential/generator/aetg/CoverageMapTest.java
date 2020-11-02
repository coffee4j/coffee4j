package de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.constraint.DynamicHardConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.util.ParameterValuePair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoverageMapTest {

    private static final Set<int[]> COMBINATIONS = Set.of(new int[]{1, -1, -1}, new int[]{-1, 2, -1}, new int[]{1, -1, 2}, new int[]{-1, 1, -1}, new int[]{1, 2, 2}, new int[]{2, 2, -1});
    private static final DynamicHardConstraintChecker EMPTY_CHECKER = new DynamicHardConstraintChecker(
            CompleteTestModel.builder()
                    .positiveTestingStrength(2)
                    .parameterSizes(3, 3, 3)
                    .build(),
                    List.of(), List.of());

    @Test
    void doesCorrectlyInitialize() {
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);
        assertTrue(map.hasUncoveredCombinations());
    }

    @Test
    void doesCorrectlyCoverCombinations() {

        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);
        map.updateSubCombinationCoverage(new int[]{1, 2, 1});
        assertEquals(1, map.getNumberOfUncoveredCombinations(new int[]{1, 1, 1}));
        assertTrue(map.hasUncoveredCombinations());
    }

    @Test
    void doesCorrectlyCountCombinationsPartial() {
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);
        assertEquals(3, map.getNumberOfUncoveredCombinations(new int[]{1, -1, 1}));
    }

    @Test
    void doesCorrectlyCountCombinationsComplete() {
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);
        assertEquals(2, map.getNumberOfUncoveredCombinations(new int[]{1, 1, 1}));
    }

    @Test
    void doesCorrectlyFindBestParameter() {
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);

        ParameterValuePair mostCommonValue = map.getMostCommonValue(Set.of(), new IntOpenHashSet());
        assertEquals(0, mostCommonValue.getParameter());
        assertEquals(1, mostCommonValue.getValue());
    }


    @Test
    void doesCorrectlyFindBestParameterAfterRemoval() {
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);
        map.updateSubCombinationCoverage(new int[]{1, 1, 1});
        ParameterValuePair mostCommonValue = map.getMostCommonValue(Set.of(), new IntOpenHashSet());
        assertEquals(1, mostCommonValue.getParameter());
        assertEquals(2, mostCommonValue.getValue());
    }

    @Test
    void doesCorrectlyFindBestParameterForbidden() {
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);
        ParameterValuePair mostCommonValue = map.getMostCommonValue(Set.of(new ParameterValuePair(0, 1)), new IntOpenHashSet());
        assertEquals(1, mostCommonValue.getParameter());
        assertEquals(2, mostCommonValue.getValue());
    }

    @Test
    void doesHandleConstraints() {
        DynamicHardConstraintChecker checker = new DynamicHardConstraintChecker(
                CompleteTestModel.builder()
                        .positiveTestingStrength(2)
                        .parameterSizes(3, 3, 3)
                        .build(),
                List.of(), List.of());
        checker.addConstraint(new int[]{1, -1, -1});
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, checker);

        assertEquals(3, map.getNumberOfUncoveredCombinations());
    }

    @Test
    void doesHandleDynamicConstraints() {
        DynamicHardConstraintChecker checker = new DynamicHardConstraintChecker(
                CompleteTestModel.builder()
                        .positiveTestingStrength(2)
                        .parameterSizes(3, 3, 3)
                        .build(),
                List.of(), List.of());

        CoverageMap map = new CoverageMap(COMBINATIONS, 3, checker);
        assertEquals(6, map.getNumberOfUncoveredCombinations());

        map.addForbiddenCombination(new int[]{1, -1, -1});
        assertEquals(3, map.getNumberOfUncoveredCombinations());
    }

    @Test
    void doesHandleImplicitConstraints() {
        CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 2, 2)
                .build();
        DynamicHardConstraintChecker checker = new DynamicHardConstraintChecker(model, List.of(), List.of());
        CoverageMap map = new CoverageMap(model.getParameterSizes(), 2, checker);
        map.addForbiddenCombination(new int[]{0, 0, -1});
        map.addForbiddenCombination(new int[]{1, -1, 0});
        // only finds [0, -1, 0], because [-1, 0, 0] is implicitly covered
        assertEquals(1, map.getNumberOfUncoveredCombinations(new int[]{0, 0, 0}));
    }
}