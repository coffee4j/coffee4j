package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerUtil.errorConstraintsWithNegation;
import static org.junit.jupiter.api.Assertions.*;

class SoftConstraintCheckerFactoryTest {

    private static final CompleteTestModel MODEL = CompleteTestModel.builder()
            .positiveTestingStrength(2)
            .parameterSizes(2, 2, 2)
            .errorTupleLists(List.of(new TupleList(1, new int[]{0, 1},   Arrays.asList(new int[]{0, 0}, new int[]{1, 1})),
                    new TupleList(2, new int[]{0, 1},   Arrays.asList(new int[]{0, 0}, new int[]{1, 1})),
                    new TupleList(3, new int[]{2},      Collections.singletonList(new int[]{1}))))
            .build();


    @Test
    void testCreateConstraintChecker() {
        assertThrows(UnsupportedOperationException.class,
                () -> new SoftConstraintCheckerFactory().createConstraintChecker(null));
    }

    @Test
    void testCreateConstraintCheckerWithNegationAndConflict() {
        final SoftConstraintCheckerFactory factory = new SoftConstraintCheckerFactory();

        final ConstraintChecker checker = factory.createConstraintCheckerWithNegation(MODEL,
                MODEL.getErrorTupleLists().get(0));
        final ConstraintChecker otherSolver = new SoftConstraintChecker(MODEL,
                MODEL.getExclusionConstraints(),
                errorConstraintsWithNegation(MODEL, MODEL.getErrorTupleLists().get(0)),
                1);

        assertEquals(otherSolver.isValid(new int[]{1, 1, 0}), checker.isValid(new int[]{1, 1, 0}));
        assertEquals(otherSolver.isValid(new int[]{0, 0, 0}), checker.isValid(new int[]{0, 0, 0}));
    }

    @Test
    void testCreateConstraintCheckerWithNegationAndNoConflict() {
        final SoftConstraintCheckerFactory factory = new SoftConstraintCheckerFactory();

        final ConstraintChecker checker = factory.createConstraintCheckerWithNegation(MODEL,
                MODEL.getErrorTupleLists().get(2));

        assertTrue(checker instanceof HardConstraintChecker);
    }
}
