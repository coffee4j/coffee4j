package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiagnosticConstraintCheckerFactoryTest {

    static final CompleteTestModel MODEL = CompleteTestModel.builder()
            .positiveTestingStrength(2)
            .parameterSizes(3, 3, 3)
            .errorTupleLists(List.of(new TupleList(1, new int[]{0}, List.of(new int[]{2})),
                    new TupleList(2, new int[]{1}, List.of(new int[]{2})),
                    new TupleList(3, new int[]{2}, List.of(new int[]{2})),
                    new TupleList(4, new int[]{0, 1}, List.of(new int[]{0, 1}, new int[]{0, 2})),
                    new TupleList(5, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{1, 2}))))
            .build();

    @Test
    void testCreateConstraintCheckerWithNegationWithoutConflict() {

        final DiagnosticConstraintCheckerFactory factory = new DiagnosticConstraintCheckerFactory();
        final ConstraintChecker checker = factory.createConstraintCheckerWithNegation(MODEL, MODEL.getErrorTupleLists().get(2));

        assertTrue(checker instanceof HardConstraintChecker);
    }

    @Test
    void testCreateConstraintCheckerWithNegationWithConflict() {
        final DiagnosticConstraintCheckerFactory factory = new DiagnosticConstraintCheckerFactory();
        final ConstraintChecker checker = factory.createConstraintCheckerWithNegation(MODEL, MODEL.getErrorTupleLists().get(3));

        assertFalse(checker.isDualValid(new int[]{0, 1}, new int[]{0, 0}));
        assertFalse(checker.isDualValid(new int[]{0, 2}, new int[]{0, 2}));
        assertTrue(checker.isDualValid(new int[]{0, 1}, new int[]{0, 1}));
        assertTrue(checker.isDualValid(new int[]{0, 1}, new int[]{0, 2}));
    }

    @Test
    void testCreateConstraintChecker() {
        assertThrows(UnsupportedOperationException.class,
                () -> new DiagnosticConstraintCheckerFactory().createConstraintChecker(null));
    }
}
