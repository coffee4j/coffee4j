package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.CombinatorialTestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.rwth.swc.coffee4j.engine.constraint.ConflictingErrorConstraintPartitionerTest.constraintsWithNegation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConflictingErrorConstraintSearcherTest {
    
    @Test
    void testConflictExplanation() {
        /*
         * This checks a conflict among 2 constraints for the value-pair (1, 0)
         * 1: 1 <=> A
         * 2: 2 <=> B
         */
        
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{2, 1}, new int[]{1, 0}, new int[]{1, 2})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, Arrays.asList(new int[]{2})));
        
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), errorTupleLists);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintSearcher searcher = new ConflictingErrorConstraintSearcher(model, converter.convertForbiddenTuples(model), constraintsWithNegation(errorTupleLists.get(0), converter.convertErrorTuples(model)));
        
        final IntList conflicting = searcher.explainValueBasedConflict(new int[]{0, 1}, new int[]{1, 0});
        
        assertTrue(conflicting.contains(2));
        assertFalse(conflicting.contains(1));
        assertFalse(conflicting.contains(3));
    }
    
    @Test
    void testNoConflictExplanation() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{2, 1}, new int[]{1, 0}, new int[]{1, 2})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, Arrays.asList(new int[]{2})));
        
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), errorTupleLists);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintSearcher searcher = new ConflictingErrorConstraintSearcher(model, converter.convertForbiddenTuples(model), constraintsWithNegation(errorTupleLists.get(0), converter.convertErrorTuples(model)));
        
        final IntList conflicting = searcher.explainValueBasedConflict(new int[]{0, 1}, new int[]{2, 0});
        
        assertTrue(conflicting.isEmpty());
    }
    
    @Test
    void testExplanationIsFreeOfSideEffects() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{2, 1}, new int[]{1, 0}, new int[]{1, 2})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, Arrays.asList(new int[]{2})));
        
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), errorTupleLists);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintSearcher searcher = new ConflictingErrorConstraintSearcher(model, converter.convertForbiddenTuples(model), constraintsWithNegation(errorTupleLists.get(0), converter.convertErrorTuples(model)));
        
        searcher.explainValueBasedConflict(new int[]{0, 1}, new int[]{2, 0});
        
        IntList conflicting = searcher.explainValueBasedConflict(new int[]{0, 1}, new int[]{1, 0});
        
        assertTrue(conflicting.contains(2));
        assertFalse(conflicting.contains(1));
        assertFalse(conflicting.contains(3));
    }
    
    @Test
    void testNoConflictDiagnose() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{2, 1}, new int[]{1, 0}, new int[]{1, 2})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, Arrays.asList(new int[]{2})));
        
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), errorTupleLists);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintSearcher searcher = new ConflictingErrorConstraintSearcher(model, converter.convertForbiddenTuples(model), constraintsWithNegation(errorTupleLists.get(0), converter.convertErrorTuples(model)));
        
        final IntList diagnose = searcher.diagnoseValueBasedConflict(new int[]{0, 1}, new int[]{2, 0});
        
        assertTrue(diagnose.isEmpty());
    }
    
    @Test
    void testConflictDiagnose() {
        /*
         * This checks a conflict among 2 constraints for the value-pair (1, 0)
         * 1: 1 <=> A
         * 2: 2 <=> B
         */
        
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{2, 1}, new int[]{1, 0}, new int[]{1, 2})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, Arrays.asList(new int[]{2})));
        
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), errorTupleLists);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintSearcher searcher = new ConflictingErrorConstraintSearcher(model, converter.convertForbiddenTuples(model), constraintsWithNegation(errorTupleLists.get(0), converter.convertErrorTuples(model)));
        
        final IntList diagnose = searcher.diagnoseValueBasedConflict(new int[]{0, 1}, new int[]{1, 0});
        
        assertTrue(diagnose.contains(2));
        assertFalse(diagnose.contains(1));
        assertFalse(diagnose.contains(3));
    }
    
    @Test
    void testDiagnoseIsFreeOfSideEffects() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{2, 1}, new int[]{1, 0}, new int[]{1, 2})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, Arrays.asList(new int[]{2})));
        
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), errorTupleLists);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintSearcher searcher = new ConflictingErrorConstraintSearcher(model, converter.convertForbiddenTuples(model), constraintsWithNegation(errorTupleLists.get(0), converter.convertErrorTuples(model)));
        
        searcher.diagnoseValueBasedConflict(new int[]{0, 1}, new int[]{2, 0});
        
        IntList diagnose = searcher.diagnoseValueBasedConflict(new int[]{0, 1}, new int[]{1, 0});
        
        assertTrue(diagnose.contains(2));
        assertFalse(diagnose.contains(1));
        assertFalse(diagnose.contains(3));
    }
    
    @Disabled("Seems to fail non-deterministically")
    @Test
    void testDiagnoseOverConstrainedRegistrationExample() {
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 2}, Collections.emptyList(), Arrays.asList(new TupleList(1, new int[]{0}, Arrays.asList(new int[]{2})), new TupleList(2, new int[]{1}, Arrays.asList(new int[]{2})), new TupleList(3, new int[]{2}, Arrays.asList(new int[]{1})), new TupleList(4, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{0, 2})), new TupleList(5, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{1, 2}))));
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintSearcher searcher = new ConflictingErrorConstraintSearcher(model, converter.convertForbiddenTuples(model), constraintsWithNegation(model.getErrorTupleLists().get(1), converter.convertErrorTuples(model)));
        
        IntList diagnose = searcher.diagnoseValueBasedConflict(new int[]{1}, new int[]{2});
        
        assertEquals(1, diagnose.size());
        assertTrue(diagnose.contains(4) || diagnose.contains(5));
    }
    
    @Test
    void testDiagnoseOverConstrainedRegistrationExample2() {
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 2}, Collections.emptyList(), Arrays.asList(new TupleList(1, new int[]{0}, Arrays.asList(new int[]{2})), new TupleList(2, new int[]{1}, Arrays.asList(new int[]{2})), new TupleList(3, new int[]{2}, Arrays.asList(new int[]{1})), new TupleList(4, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{0, 2})), new TupleList(5, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{1, 2}))));
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintSearcher searcher = new ConflictingErrorConstraintSearcher(model, converter.convertForbiddenTuples(model), constraintsWithNegation(model.getErrorTupleLists().get(3), converter.convertErrorTuples(model)));
        
        IntList diagnose = searcher.diagnoseValueBasedConflict(new int[]{0, 1}, new int[]{0, 2});
        
        assertTrue(diagnose.contains(2));
        assertEquals(1, diagnose.size());
    }
}
