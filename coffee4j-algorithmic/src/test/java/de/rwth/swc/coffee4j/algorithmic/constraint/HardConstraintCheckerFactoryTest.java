package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HardConstraintCheckerFactoryTest {

    @Test
    void testCreateConstraintChecker() {
        final List<TupleList> exclusionTupleLists = List.of(
                new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 2, 2)
                .exclusionTupleLists(exclusionTupleLists)
                .build();

        final ConstraintChecker solver = new HardConstraintCheckerFactory().createConstraintChecker(model);
        final ConstraintChecker otherSolver = new HardConstraintChecker(model,
                model.getExclusionConstraints(),
                model.getErrorConstraints());

        assertEquals(otherSolver.isValid(new int[]{1, 0, 1}), solver.isValid(new int[]{1, 0, 1}));
        assertEquals(otherSolver.isValid(new int[]{0, 0, 1}), solver.isValid(new int[]{0, 0, 1}));
    }

    @Test
    void testCreateConstraintCheckerWithNegation() {
        final List<TupleList> errorTupleLists = List.of(
                new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 2, 2)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConstraintChecker solver = new HardConstraintCheckerFactory()
                .createConstraintCheckerWithNegation(model, errorTupleLists.get(0));
        final ConstraintChecker otherSolver = new HardConstraintChecker(model,
                model.getExclusionConstraints(),
                model.getErrorConstraints());

        assertEquals(otherSolver.isValid(new int[]{1, 0, 1}), !solver.isValid(new int[]{1, 0, 1}));
        assertEquals(otherSolver.isValid(new int[]{0, 0, 1}), !solver.isValid(new int[]{0, 0, 1}));
    }

    private static final CompleteTestModel MODEL = CompleteTestModel.builder()
            .positiveTestingStrength(2)
            .negativeTestingStrength(2)
            .parameterSizes(2, 2, 2, 2)
            .errorTupleLists(List.of(new TupleList(1, new int[]{0, 1}, List.of(new int[]{0, 0}, new int[]{1, 1})),
                    new TupleList(2, new int[]{1, 2}, List.of(new int[]{1, 1}))))
            .build();

    private static final ConstraintCheckerFactory FACTORY = new HardConstraintCheckerFactory();

    @Test
    void testCreateHardConstraintsChecker() {
        ConstraintChecker checker = FACTORY.createConstraintChecker(MODEL);

        assertTrue(checker.isValid(new int[]{0, 1, 0, 0}));
        assertFalse(checker.isValid(new int[]{0, 0, 0, 0}));
    }

    @Test
    void testCreateHardConstraintsCheckerWithNegation() {
        TupleList tupleList = new TupleList(1, new int[]{0, 1}, Collections.singletonList(new int[]{0, 0}));

        ConstraintChecker checker = FACTORY.createConstraintCheckerWithNegation(MODEL, tupleList);

        assertTrue(checker.isValid(new int[]{0, 0, 0, 0}));
        assertFalse(checker.isValid(new int[]{0, 1, 0, 0}));
        assertFalse(checker.isValid(new int[]{0, 1, 1, 0}));
    }
}
