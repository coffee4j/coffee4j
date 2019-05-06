package de.rwth.swc.coffee4j.engine.constraint.diagnosis;

import de.rwth.swc.coffee4j.engine.CombinatorialTestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import de.rwth.swc.coffee4j.engine.constraint.InternalConstraint;
import de.rwth.swc.coffee4j.engine.constraint.InternalConstraintConverter;
import de.rwth.swc.coffee4j.engine.constraint.NegatingInternalConstraint;
import it.unimi.dsi.fastutil.ints.IntList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConflictingErrorConstraintSearcherTest {

    static List<InternalConstraint> constraintsWithNegation(TupleList tupleList, List<InternalConstraint> errorConstraints) {
        final List<InternalConstraint> constraintsWithNegation = new ArrayList<>(errorConstraints.size());

        for (InternalConstraint constraint : errorConstraints) {
            if (constraint.getId() == tupleList.getId()) {
                constraintsWithNegation.add(new NegatingInternalConstraint(constraint));
            } else {
                constraintsWithNegation.add(constraint);
            }
        }

        return constraintsWithNegation;
    }

    static void assertContainsExactly(IntList list, Integer ... expected) {
        assertEquals(expected.length, list.size());
        assertTrue(list.containsAll(Arrays.asList(expected)));
    }

    @Test
    void testMinimalConflictExplanation() {
        /*
         * This checks a conflict among 2 constraints for the value-pair (1, 0)
         * 1: 1 <=> A
         * 2: 2 <=> B
         */
        
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{2, 1}, new int[]{1, 0}, new int[]{1, 2})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, Collections.singletonList(new int[]{2})));
        
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), errorTupleLists);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintSearcher searcher = new ConflictingErrorConstraintSearcher(
                model,
                converter.convertForbiddenTuples(model),
                constraintsWithNegation(errorTupleLists.get(0), converter.convertErrorTuples(model))
        );
        
        final IntList conflict = searcher.findAndExplainConflict(new int[]{0, 1}, new int[]{1, 0});
        assertContainsExactly(conflict, 2);
    }
    
    @Test
    void testNoConflictExplanation() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{2, 1}, new int[]{1, 0}, new int[]{1, 2})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, Collections.singletonList(new int[]{2})));
        
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), errorTupleLists);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintSearcher searcher = new ConflictingErrorConstraintSearcher(model, converter.convertForbiddenTuples(model), constraintsWithNegation(errorTupleLists.get(0), converter.convertErrorTuples(model)));
        
        final IntList conflicting = searcher.findAndExplainConflict(new int[]{0, 1}, new int[]{2, 0});

        assertNull(conflicting);
    }

    @Test
    void testCTA2019Example() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, Collections.singletonList(new int[]{2})));                           // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, Collections.singletonList(new int[]{2})));                           // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, Collections.singletonList(new int[]{1})));                           // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{0, 2})));    // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{1, 2})));    // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]

        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 2}, Collections.emptyList(), errorTupleLists);

        IntList conflict;

        conflict = buildSearch(errorTupleLists, 1, model).findAndExplainConflict(new int[]{0}, new int[]{2});
        assertNull(conflict); // no conflict for [Title:123]

        conflict = buildSearch(errorTupleLists, 2, model).findAndExplainConflict(new int[]{1}, new int[]{2});
        assertContainsExactly(conflict, 4, 1, 5);   // conflict for [GivenName:123] with [Title:123], [Title:Mr,GivenName:123], [Title:Mrs,GivenName:123]

        conflict = buildSearch(errorTupleLists, 3, model).findAndExplainConflict(new int[]{2}, new int[]{1});
        assertNull(conflict); // no conflict for [FamilyName:123]

        conflict = buildSearch(errorTupleLists, 4, model).findAndExplainConflict(new int[]{0, 1}, new int[]{0, 1});
        assertNull(conflict);                             // no conflict for [Title:Mr,GivenName:Jane]

        conflict = buildSearch(errorTupleLists, 4, model).findAndExplainConflict(new int[]{0, 1}, new int[]{0, 2});
        assertContainsExactly(conflict, 2);     // conflict for [Title:Mr,GivenName:123] with [GivenName:123]

        conflict = buildSearch(errorTupleLists, 5, model).findAndExplainConflict(new int[]{0, 1}, new int[]{1, 0});
        assertNull(conflict);                             // no conflict for [Title:Mr,GivenName:Jane]

        conflict = buildSearch(errorTupleLists, 5, model).findAndExplainConflict(new int[]{0, 1}, new int[]{1, 2});
        assertContainsExactly(conflict, 2);     // conflict for [Title:Mr,GivenName:123] with [GivenName:123]
    }

    @Test
    void testCTA2019ExampleWithCorrectConstraints() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, Collections.singletonList(new int[]{2}), true));                           // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, Collections.singletonList(new int[]{2}), true));                           // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, Collections.singletonList(new int[]{1}), true));                           // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{0, 2})));    // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{1, 2})));    // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]

        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 2}, Collections.emptyList(), errorTupleLists);

        IntList conflict;

        conflict = buildSearch(errorTupleLists, 1, model).findAndExplainConflict(new int[]{0}, new int[]{2});

        assertNull(conflict); // no conflict for [Title:123]

        conflict = buildSearch(errorTupleLists, 2, model).findAndExplainConflict(new int[]{1}, new int[]{2});
        assertContainsExactly(conflict, 4, 5); // conflict for [GivenName:123] with [Title:123], [Title:Mr,GivenName:123], [Title:Mrs,GivenName:123]

        conflict = buildSearch(errorTupleLists, 3, model).findAndExplainConflict(new int[]{2}, new int[]{1});
        assertNull(conflict); // no conflict for [FamilyName:123]

        conflict = buildSearch(errorTupleLists, 4, model).findAndExplainConflict(new int[]{0, 1}, new int[]{0, 1});
        assertNull(conflict);                        // no conflict for [Title:Mr,GivenName:Jane]

        conflict = buildSearch(errorTupleLists, 4, model).findAndExplainConflict(new int[]{0, 1}, new int[]{0, 2});
        assertContainsExactly(conflict, 4); // conflict for [Title:Mr,GivenName:123] with [GivenName:123]

        conflict = buildSearch(errorTupleLists, 5, model).findAndExplainConflict(new int[]{0, 1}, new int[]{1, 0});
        assertNull(conflict);                         // no conflict for [Title:Mr,GivenName:Jane]

        conflict = buildSearch(errorTupleLists, 5, model).findAndExplainConflict(new int[]{0, 1}, new int[]{1, 2});
        assertContainsExactly(conflict, 5); // conflict for [Title:Mr,GivenName:123] with [GivenName:123]
    }

    @Test
    void testCTA2019ExampleWithInconsistentCorrectConstraints() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, Collections.singletonList(new int[]{2}), true));                           // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, Collections.singletonList(new int[]{2})));                           // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, Collections.singletonList(new int[]{1})));                           // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{0, 2}), true));    // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{1, 2}), true));    // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]

        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 2}, Collections.emptyList(), errorTupleLists);

        IntList conflict;

        conflict = buildSearch(errorTupleLists, 1, model).findAndExplainConflict(new int[]{0}, new int[]{2});
        assertNull(conflict); // no conflict for [Title:123]

        conflict = buildSearch(errorTupleLists, 2, model).findAndExplainConflict(new int[]{1}, new int[]{2});
        assertContainsExactly(conflict, 2);   // inconsistent, therefore return negated error-constraint

        conflict = buildSearch(errorTupleLists, 3, model).findAndExplainConflict(new int[]{2}, new int[]{1});
        assertNull(conflict); // no conflict for [FamilyName:123]

        conflict = buildSearch(errorTupleLists, 4, model).findAndExplainConflict(new int[]{0, 1}, new int[]{0, 1});
        assertNull(conflict); // no conflict for [Title:Mr,GivenName:Jane]

        conflict = buildSearch(errorTupleLists, 4, model).findAndExplainConflict(new int[]{0, 1}, new int[]{0, 2});
        assertContainsExactly(conflict, 2); // conflict for [Title:Mr,GivenName:123] with [GivenName:123]

        conflict = buildSearch(errorTupleLists, 5, model).findAndExplainConflict(new int[]{0, 1}, new int[]{1, 0});
        assertNull(conflict); // no conflict for [Title:Mr,GivenName:Jane]

        conflict = buildSearch(errorTupleLists, 5, model).findAndExplainConflict(new int[]{0, 1}, new int[]{1, 2});
        assertContainsExactly(conflict, 2); // conflict for [Title:Mr,GivenName:123] with [GivenName:123]
    }

    private ConflictingErrorConstraintSearcher buildSearch(List<TupleList> errorTupleLists, int id, CombinatorialTestModel model) {
        final InternalConstraintConverter converter = new InternalConstraintConverter();

        final TupleList selected = errorTupleLists.stream().filter(t -> t.getId() == id).findFirst().orElseThrow();

        return new ConflictingErrorConstraintSearcher(
                model,
                converter.convertForbiddenTuples(model),
                constraintsWithNegation(selected, converter.convertErrorTuples(model))
        );
    }

    @Test
    void testExplanationIsFreeOfSideEffects() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{2, 1}, new int[]{1, 0}, new int[]{1, 2})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, Collections.singletonList(new int[]{2})));
        
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), errorTupleLists);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintSearcher searcher = new ConflictingErrorConstraintSearcher(model, converter.convertForbiddenTuples(model), constraintsWithNegation(errorTupleLists.get(0), converter.convertErrorTuples(model)));
        
        searcher.findAndExplainConflict(new int[]{0, 1}, new int[]{2, 0});

        IntList conflict = searcher.findAndExplainConflict(new int[]{0, 1}, new int[]{1, 0});

        assertEquals(1, conflict.size());
        assertTrue(conflict.contains(2));
        assertFalse(conflict.contains(1));
        assertFalse(conflict.contains(3));
    }
}
