package de.rwth.swc.coffee4j.algorithmic.interleaving.identification.ofot;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.CoverageMap;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.AbstractIdentificationStrategyTest;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.CombinationType;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationConfiguration;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OfotStrategyTest extends AbstractIdentificationStrategyTest {
    @BeforeEach
    void instantiateStrategy() {
        CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3, 3)
                .build();
                
        ConstraintChecker constraintChecker = new MinimalForbiddenTuplesChecker(testModel);
        CoverageMap coverageMap = new CoverageMap(testModel.getParameterSizes(), testModel.getPositiveTestingStrength(), constraintChecker);
        IdentificationConfiguration configuration = IdentificationConfiguration.configuration()
                .testModel(testModel)
                .constraintChecker(constraintChecker)
                .coverageMap(coverageMap)
                .build();
        strategy = OfotStrategy.ofotStrategy().create(configuration);

        failureInducingCombinations = new HashMap<>();
    }

    @Test
    void failureInducingCombinationCorrectlyIdentifiedAfterSecondIteration() {
        failureInducingCombinations.put(new int[]{0,0,0,-1}, new ErrorConstraintException());
        failureInducingCombinations.put(new int[]{1,0,0,-1}, new ErrorConstraintException());

        computeMinimalFailureInducingCombinations(strategy.startIdentification(new int[]{0,0,0,0}, TestResult.failure(new ErrorConstraintException())));
        Map<IntList, CombinationType> fics = strategy.getIdentifiedCombinations();
        assertFalse(fics.isEmpty());

        computeMinimalFailureInducingCombinations(strategy.restartIdentification());
        fics = strategy.getIdentifiedCombinations();
        assertFalse(fics.isEmpty());

        Set<IntList> foundInSecondIteration = new HashSet<>();
        fics.forEach((fic, result) -> foundInSecondIteration.add(fic));
    
        Set<IntList> expected = new HashSet<>();
        expected.add(new IntArrayList(new int[]{0,0,0,-1}));

        assertEquals(expected, foundInSecondIteration);
    }
}
