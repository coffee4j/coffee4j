package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinimalForbiddenTupleCheckerTest {

    @Test
    void checkIsValid() {
        final List<TupleList> exclusionTupleLists = new ArrayList<>();
        exclusionTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 2, 2)
                .exclusionTupleLists(exclusionTupleLists)
                .build();

        final ConstraintChecker checker = new MinimalForbiddenTuplesChecker(model);

        assertTrue(checker.isValid(new int[]{1, 0, 1}));
        assertFalse(checker.isValid(new int[]{0, 0, 1}));
    }

    @Test
    void checkIsValidWithAdditionalParameter() {
        final List<TupleList> exclusionTupleLists = List.of(
                new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})));
    
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 2, 2)
                .exclusionTupleLists(exclusionTupleLists)
                .build();

        final ConstraintChecker checker = new MinimalForbiddenTuplesChecker(model);

        assertTrue(checker.isExtensionValid(new int[]{1, 0}, 2, 0));
        assertFalse(checker.isExtensionValid(new int[]{0, 0}, 2, 0));
    }

    @Test
    void checkMinimalForbiddenTuple() {
        final List<TupleList> exclusionTupleLists = new ArrayList<>();
        exclusionTupleLists.add(new TupleList(1, new int[]{0, 2}, List.of(new int[]{0, 0})));
        exclusionTupleLists.add(new TupleList(2, new int[]{1, 2}, List.of(new int[]{0, 1})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .parameterSizes(2, 2, 2)
                .exclusionTupleLists(exclusionTupleLists)
                .build();

        final ConstraintChecker checker = new MinimalForbiddenTuplesChecker(model);

        assertFalse(checker.isValid(new int[]{0, 0, -1}));
    }
}
