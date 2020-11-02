package de.rwth.swc.coffee4j.algorithmic.interleaving.identification.trt;

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

public class TupleRelationshipStrategyTest extends AbstractIdentificationStrategyTest {
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
        strategy = TupleRelationshipStrategy.tupleRelationshipStrategy().create(configuration);

        failureInducingCombinations = new HashMap<>();
    }

    @Test
    void failureInducingCombinationCorrectlyIdentifiedAfterTwoIterations() {
        failureInducingCombinations.put(new int[]{0,0,0,-1}, new ErrorConstraintException());
        failureInducingCombinations.put(new int[]{1,0,0,-1}, new ErrorConstraintException());

        computeMinimalFailureInducingCombinations(strategy.startIdentification(new int[]{0,0,0,0}, TestResult.failure(new ErrorConstraintException("failure!"))));

        Map<IntList, CombinationType> fics = strategy.getIdentifiedCombinations();

        assertFalse(fics.isEmpty());

        Set<IntList> expected = new HashSet<>();
        Set<IntList> found = new HashSet<>();

        fics.forEach((fic, result) -> found.add(fic));
        expected.add(new IntArrayList(new int[]{0,0,0,-1}));
        expected.add(new IntArrayList(new int[]{1,0,0,-1}));

        assertNotEquals(expected, found);
        assertEquals(Collections.singleton(new IntArrayList(new int[]{0, 0, 0, -1})), found);

        computeMinimalFailureInducingCombinations(strategy.startIdentification(new int[]{1,0,0,2}, TestResult.failure(new ErrorConstraintException("failure!"))));

        fics = strategy.getIdentifiedCombinations();

        assertFalse(fics.isEmpty());
        fics.forEach((fic, result) -> found.add(fic));

        assertEquals(expected, found);
    }
}
