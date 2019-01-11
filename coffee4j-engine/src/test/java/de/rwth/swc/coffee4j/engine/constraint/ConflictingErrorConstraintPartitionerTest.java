package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.CombinatorialTestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConflictingErrorConstraintPartitionerTest {
    
    @Test
    void testNoConflicts() {
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{2, 2}, Collections.emptyList(), Arrays.asList(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0})), new TupleList(2, new int[]{1}, Arrays.asList(new int[]{1}))));
        
        final TupleList negatedTupleList = model.getErrorTupleLists().get(0);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintPartitioner partitioner = new ConflictingErrorConstraintPartitioner(model, converter.convertForbiddenTuples(model), constraintsWithNegation(negatedTupleList, converter.convertErrorTuples(model)), negatedTupleList);
        
        assertEquals(2, partitioner.getHardConstraints().size());
        assertTrue(partitioner.getIgnoredConstraintIds().isEmpty());
        assertTrue(partitioner.getSoftConstraints().isEmpty());
    }
    
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
    
    @Test
    void testDifferentConflicts() {
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{2, 2}, Collections.emptyList(), Arrays.asList(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})), new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 0})), new TupleList(3, new int[]{1}, Arrays.asList(new int[]{1}))));
        
        final TupleList negatedTupleList = model.getErrorTupleLists().get(0);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintPartitioner partitioner = new ConflictingErrorConstraintPartitioner(model, converter.convertForbiddenTuples(model), constraintsWithNegation(negatedTupleList, converter.convertErrorTuples(model)), negatedTupleList);
        
        assertEquals(1, partitioner.getHardConstraints().size());
        assertTrue(partitioner.getIgnoredConstraintIds().isEmpty());
        assertEquals(2, partitioner.getSoftConstraints().size());
        
        assertTrue(partitioner.getValueBasedConflicts().get(0).contains(2));
        assertTrue(partitioner.getValueBasedConflicts().get(1).contains(3));
    }
    
    @Test
    void testSameConflicts() {
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{2, 2}, Collections.emptyList(), Arrays.asList(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})), new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1}))));
        
        final TupleList negatedTupleList = model.getErrorTupleLists().get(0);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintPartitioner partitioner = new ConflictingErrorConstraintPartitioner(model, converter.convertForbiddenTuples(model), constraintsWithNegation(negatedTupleList, converter.convertErrorTuples(model)), negatedTupleList);
        
        assertEquals(1, partitioner.getHardConstraints().size());
        assertEquals(1, partitioner.getIgnoredConstraintIds().size());
        assertTrue(partitioner.getSoftConstraints().isEmpty());
    }
    
    @Test
    void testRegistrationExample() {
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 2}, Collections.emptyList(), Arrays.asList(new TupleList(1, new int[]{0}, Arrays.asList(new int[]{2})), new TupleList(2, new int[]{1}, Arrays.asList(new int[]{2})), new TupleList(3, new int[]{2}, Arrays.asList(new int[]{1})), new TupleList(4, new int[]{0, 1}, Arrays.asList(new int[]{0, 1})), new TupleList(5, new int[]{1, 2}, Arrays.asList(new int[]{1, 0}))));
        
        final TupleList negatedTupleList = model.getErrorTupleLists().get(0);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintPartitioner partitioner = new ConflictingErrorConstraintPartitioner(model, converter.convertForbiddenTuples(model), constraintsWithNegation(negatedTupleList, converter.convertErrorTuples(model)), negatedTupleList);
        
        assertEquals(5, partitioner.getHardConstraints().size());
    }
    
    @Test
    void testOverConstrainedRegistrationExample() {
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{3, 3, 2}, Collections.emptyList(), Arrays.asList(new TupleList(1, new int[]{0}, Arrays.asList(new int[]{2})), new TupleList(2, new int[]{1}, Arrays.asList(new int[]{2})), new TupleList(3, new int[]{2}, Arrays.asList(new int[]{1})), new TupleList(4, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{0, 2})), new TupleList(5, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{1, 2}))));
        
        final TupleList negatedTupleList = model.getErrorTupleLists().get(1);
        
        final InternalConstraintConverter converter = new InternalConstraintConverter();
        
        final ConflictingErrorConstraintPartitioner partitioner = new ConflictingErrorConstraintPartitioner(model, converter.convertForbiddenTuples(model), constraintsWithNegation(negatedTupleList, converter.convertErrorTuples(model)), negatedTupleList);
        
        assertEquals(4, partitioner.getHardConstraints().size());
        assertEquals(1, partitioner.getIgnoredConstraintIds().size());
    }
}
