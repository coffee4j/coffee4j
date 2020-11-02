package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinimalForbiddenTupleCheckerFactoryTest {
    @Test
    void testCreateConstraintChecker() {
        final List<TupleList> forbiddenTupleLists = new ArrayList<>();
        forbiddenTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .parameterSizes(2)
                .parameterSizes(2, 2, 2)
                .exclusionTupleLists(forbiddenTupleLists)
                .build();

        final ConstraintChecker solver = new MinimalForbiddenTuplesCheckerFactory().createConstraintChecker(model);
        final ConstraintChecker otherSolver = new MinimalForbiddenTuplesChecker(model);

        assertEquals(otherSolver.isValid(new int[]{1, 0, 1}), solver.isValid(new int[]{1, 0, 1}));
        assertEquals(otherSolver.isValid(new int[]{0, 0, 1}), solver.isValid(new int[]{0, 0, 1}));
    }

    @Test
    void testCreateConstraintCheckerWithNegation() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 2, 2)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConstraintChecker solver = new MinimalForbiddenTuplesCheckerFactory()
                .createConstraintCheckerWithNegation(model, errorTupleLists.get(0));
        final ConstraintChecker otherSolver = new MinimalForbiddenTuplesChecker(model);

        assertEquals(otherSolver.isValid(new int[]{1, 0, 1}), !solver.isValid(new int[]{1, 0, 1}));
        assertEquals(otherSolver.isValid(new int[]{0, 0, 1}), !solver.isValid(new int[]{0, 0, 1}));
    }
}
