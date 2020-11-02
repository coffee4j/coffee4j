package de.rwth.swc.coffee4j.algorithmic.interleaving.generator.aetg;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.CoverageMap;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategy;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Combinator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AetgStrategyTest {
    private CompleteTestModel testModel;
    private ConstraintChecker constraintChecker;
    private CoverageMap coverageMap;
    private TestInputGenerationStrategy strategy;

    @BeforeEach
    void instantiateStrategy() {
        testModel = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3, 3)
                .build();
        constraintChecker = new MinimalForbiddenTuplesChecker(testModel);
        coverageMap = new CoverageMap(testModel.getParameterSizes(), testModel.getPositiveTestingStrength(), constraintChecker);
        TestInputGenerationConfiguration configuration = TestInputGenerationConfiguration.configuration()
                .testModel(testModel)
                .constraintChecker(constraintChecker)
                .coverageMap(coverageMap)
                .build();
        strategy = AetgStrategy.aetgStrategy().create(configuration);
    }

    @Test
    void checkAllCombinationsCovered() {
        List<int[]> generatedTestInputs = new ArrayList<>();

        Optional<int[]> nextTestInput = strategy.generateNextTestInput();

        while (nextTestInput.isPresent()) {
            coverageMap.updateCoverage(nextTestInput.get());
            generatedTestInputs.add(nextTestInput.get());
            nextTestInput = strategy.generateNextTestInput();
        }

        assertTrue(allCombinationsCovered(generatedTestInputs, Combinator.computeCombinations(testModel.getParameterSizes(), testModel.getPositiveTestingStrength())));
    }

    @Test
    void checkAllCombinationsCoveredWhenConstraintsPresent() {
        List<int[]> generatedTestInputs = new ArrayList<>();

        constraintChecker.addConstraint(new int[]{1,-1,1,-1});
        constraintChecker.addConstraint(new int[]{0,0,-1,-1});
        constraintChecker.addConstraint(new int[]{-1,0,0,-1});

        coverageMap.updateCoverage();

        Optional<int[]> nextTestInput = strategy.generateNextTestInput();

        while (nextTestInput.isPresent()) {
            coverageMap.updateCoverage(nextTestInput.get());
            generatedTestInputs.add(nextTestInput.get());
            nextTestInput = strategy.generateNextTestInput();
        }

        Set<int[]> combinations = Combinator.computeCombinations(testModel.getParameterSizes(), testModel.getPositiveTestingStrength());
        combinations.removeIf(combination -> !constraintChecker.isValid(combination));

        assertTrue(allCombinationsCovered(generatedTestInputs, combinations));
    }

    private boolean allCombinationsCovered(List<int[]> generatedTestInputs, Set<int[]> combinationsToBeCovered) {
        nextCombination:
        for (int[] combination : combinationsToBeCovered) {
            for (int[] testInput : generatedTestInputs) {
                if (CombinationUtil.contains(testInput, combination)) {
                    continue nextCombination;
                }
            }

            return false;
        }

        return true;
    }
}
