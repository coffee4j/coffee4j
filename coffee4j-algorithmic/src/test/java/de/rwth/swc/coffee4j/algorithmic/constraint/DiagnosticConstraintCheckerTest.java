package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper.wrap;
import static org.junit.jupiter.api.Assertions.*;

class DiagnosticConstraintCheckerTest {

    @Test
    void testDiagnosticConstraintChecker() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 2, 2)
                .errorTupleLists(List.of(
                        new TupleList(1, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{0, 1})),
                        new TupleList(2, new int[]{0, 1}, List.of(new int[]{0, 0})),
                        new TupleList(3, new int[]{2},    List.of(new int[]{1}))))
                .build();

        final List<Constraint> hardConstraints = Arrays.asList(
                model.getErrorConstraints().get(0),
                model.getErrorConstraints().get(2));

        final List<Constraint> softConstraints = Collections.singletonList(
                model.getErrorConstraints().get(1));

        final Object2IntMap<IntArrayWrapper> thresholds = new Object2IntArrayMap<>();
        thresholds.put(wrap(0, 0), 1);
        thresholds.put(wrap(1, 1), 0);

        final ConstraintChecker checker = new DiagnosticConstraintChecker(
                model, model.getErrorTupleLists().get(0), hardConstraints, softConstraints, thresholds);

        assertTrue(checker.isDualValid(new int[]{0, 1}, new int[] {0, 0}));
        assertTrue(checker.isDualValid(new int[]{0, 1}, new int[] {1, 1}));
        assertFalse(checker.isDualValid(new int[]{0, 1}, new int[] {1, 0}));
        assertFalse(checker.isDualValid(new int[]{0, 1}, new int[] {0, 1}));
    }

    @Test
    void testDiagnosticConstraintCheckerWithExample1() { /* not over-constrained */
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(List.of(
                        new TupleList(1, new int[]{0}, List.of(new int[]{2})),
                        new TupleList(2, new int[]{1}, List.of(new int[]{2})),
                        new TupleList(3, new int[]{2}, List.of(new int[]{0}, new int[]{1})), /* negated */
                        new TupleList(4, new int[]{0, 1}, List.of(new int[]{0, 1}, new int[]{0, 2})),
                        new TupleList(5, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{1, 2}))))
                .build();

        final List<Constraint> hardConstraints = Arrays.asList(
                model.getErrorConstraints().get(0),
                model.getErrorConstraints().get(1),
                model.getErrorConstraints().get(2),
                model.getErrorConstraints().get(3),
                model.getErrorConstraints().get(4));

        final List<Constraint> softConstraints = Collections.emptyList();

        final Object2IntMap<IntArrayWrapper> thresholds = new Object2IntArrayMap<>();
        thresholds.put(wrap(2), 0);

        final ConstraintChecker checker = new DiagnosticConstraintChecker(
                model, model.getErrorTupleLists().get(2), hardConstraints, softConstraints, thresholds);

        assertTrue(checker.isDualValid(new int[]{2}, new int[] {2}));
        assertFalse(checker.isDualValid(new int[]{2}, new int[] {0}));
        assertFalse(checker.isDualValid(new int[]{2}, new int[] {1}));
        assertTrue(checker.isDualValid(new int[]{0, 1, 2}, new int[] {0, 0, 2}));
        assertFalse(checker.isDualValid(new int[]{0, 1, 2}, new int[] {0, 1, 2}));
        assertTrue(checker.isDualValid(new int[]{0, 1}, new int[] {0, 0}));
        assertTrue(checker.isDualValid(new int[]{0, 1}, new int[] {1, 1}));
        assertFalse(checker.isDualValid(new int[]{0, 1}, new int[] {1, 0}));
        assertFalse(checker.isDualValid(new int[]{0, 1}, new int[] {0, 1}));
    }

    @Test
    void testDiagnosticConstraintCheckerWithExample2() { /* implicit conflict */
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(List.of(
                        new TupleList(1, new int[]{0}, List.of(new int[]{0}, new int[]{1})), /* negated */
                        new TupleList(2, new int[]{1}, List.of(new int[]{2})),
                        new TupleList(3, new int[]{2}, List.of(new int[]{2})),
                        new TupleList(4, new int[]{0, 1}, List.of(new int[]{0, 1}, new int[]{0, 2})),
                        new TupleList(5, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{1, 2}))))
                .build();

        final List<Constraint> hardConstraints = List.of(
                model.getErrorConstraints().get(0),
                model.getErrorConstraints().get(1),
                model.getErrorConstraints().get(2));

        final List<Constraint> softConstraints = List.of(
                model.getErrorConstraints().get(3),
                model.getErrorConstraints().get(4));

        final Object2IntMap<IntArrayWrapper> thresholds = new Object2IntArrayMap<>();
        thresholds.put(wrap(2), 1);

        final ConstraintChecker checker = new DiagnosticConstraintChecker(
                model, model.getErrorTupleLists().get(0), hardConstraints, softConstraints, thresholds);

        assertFalse(checker.isDualValid(new int[]{0, 1}, new int[] {0, 0}));
        assertFalse(checker.isDualValid(new int[]{0, 1}, new int[] {1, 1}));
        assertFalse(checker.isDualValid(new int[]{0, 1}, new int[] {0, 1}));
        assertFalse(checker.isDualValid(new int[]{0, 1}, new int[] {1, 0}));
        assertTrue(checker.isDualValid(new int[]{0, 1}, new int[] {2, 0}));
        assertTrue(checker.isDualValid(new int[]{0, 1}, new int[] {2, 1}));
    }

    @Test
    void testDiagnosticConstraintCheckerWithExample3() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(
                        List.of(new TupleList(1, new int[]{0}, List.of(new int[]{2})),
                        new TupleList(2, new int[]{1}, List.of(new int[]{2})),
                        new TupleList(3, new int[]{2}, List.of(new int[]{2})),
                        new TupleList(4, new int[]{0, 1}, List.of(new int[]{0, 0}, new int[]{1, 1})), /* negated */
                        new TupleList(5, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{1, 2}))))
                .build();

        final List<Constraint> hardConstraints = List.of(
                model.getErrorConstraints().get(0),
                model.getErrorConstraints().get(2),
                model.getErrorConstraints().get(3),
                model.getErrorConstraints().get(4));

        final List<Constraint> softConstraints = List.of(
                model.getErrorConstraints().get(1));

        final Object2IntMap<IntArrayWrapper> thresholds = new Object2IntArrayMap<>();
        thresholds.put(wrap(0, 1), 0);
        thresholds.put(wrap(0, 2), 1);

        final ConstraintChecker checker = new DiagnosticConstraintChecker(
                model, model.getErrorTupleLists().get(3), hardConstraints, softConstraints, thresholds);

        assertFalse(checker.isDualValid(new int[]{0, 1}, new int[] {0, 0}));
        assertFalse(checker.isDualValid(new int[]{0, 1}, new int[] {1, 1}));
        assertTrue(checker.isDualValid(new int[]{0, 1}, new int[] {0, 1}));
        assertTrue(checker.isDualValid(new int[]{0, 1}, new int[] {0, 2}));
        assertTrue(checker.isDualValid(new int[]{0, 1, 2}, new int[] {0, 1, 0}));
        assertFalse(checker.isDualValid(new int[]{0, 1, 2}, new int[] {0, 1, 2}));
        assertTrue(checker.isDualValid(new int[]{0, 1, 2}, new int[] {0, 2, 0}));
        assertFalse(checker.isDualValid(new int[]{0, 1, 2}, new int[] {0, 2, 2}));
    }
}
