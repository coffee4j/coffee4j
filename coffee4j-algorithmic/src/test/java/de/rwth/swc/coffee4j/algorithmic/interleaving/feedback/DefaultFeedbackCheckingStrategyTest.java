package de.rwth.swc.coffee4j.algorithmic.interleaving.feedback;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.CoverageMap;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.InvalidAttributeValueException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultFeedbackCheckingStrategyTest {
    private FeedbackCheckingStrategy strategy;
    private Set<int[]> failureInducingCombinations;

    @BeforeEach
    void instantiateStrategy() {
        CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3, 3)
                .build();
        ConstraintChecker constraintChecker = new MinimalForbiddenTuplesChecker(testModel);
        CoverageMap coverageMap = new CoverageMap(testModel.getParameterSizes(), testModel.getPositiveTestingStrength(), constraintChecker);
        FeedbackCheckingConfiguration configuration = FeedbackCheckingConfiguration.configuration()
                .testModel(testModel)
                .constraintChecker(constraintChecker)
                .coverageMap(coverageMap)
                .build();
        strategy = DefaultFeedbackCheckingStrategy.defaultCheckingStrategy().create(configuration);

        failureInducingCombinations = new HashSet<>();
    }

    @Test
    void failureInducingCombinationIsAccepted() {
        boolean isFic = true;
        failureInducingCombinations.add(new int[]{-1,0,-1,-1});

        Optional<int[]> nextTestInput = strategy.startFeedbackChecking(new int[]{-1,0,-1,-1}, new int[]{0,0,0,0});

        while (nextTestInput.isPresent()) {
            if (!CombinationUtil.contains(nextTestInput.get(), failureInducingCombinations.stream().findFirst().get())) {
                isFic = false;
                break;
            }

            nextTestInput = strategy.generateNextTestInputForChecking(nextTestInput.get(), TestResult.failure(new InvalidAttributeValueException()));
        }

        assertTrue(isFic);
    }

    @Test
    void failureInducingCombinationNotAccepted() {
        boolean isFic = true;
        failureInducingCombinations.add(new int[]{0,0,0,-1});
        failureInducingCombinations.add(new int[]{1,0,0,-1});

        Optional<int[]> nextTestInput = strategy.startFeedbackChecking(new int[]{-1,0,0,-1}, new int[]{0,0,0,0});

        while (nextTestInput.isPresent()) {
            if (!CombinationUtil.contains(nextTestInput.get(), failureInducingCombinations.stream().findFirst().get())) {
                isFic = false;
                break;
            }

            nextTestInput = strategy.generateNextTestInputForChecking(nextTestInput.get(), TestResult.failure(new InvalidAttributeValueException()));
        }

        assertFalse(isFic);
    }
}
