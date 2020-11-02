package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.GroupSpecificTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.HardConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class IpogAlgorithmWithConstraintsTest {
    
    @Test
    void checkWithSimpleConstraint() {
        final List<TupleList> exclusionTupleLists = new ArrayList<>();
        exclusionTupleLists.add(new TupleList(1, new int[]{0, 1}, List.of(new int[]{1, 1})));
        exclusionTupleLists.add(new TupleList(2, new int[]{1, 2}, List.of(new int[]{1, 1})));
        
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 2, 2, 2)
                .exclusionTupleLists(exclusionTupleLists)
                .build();
        
        final ConstraintChecker checker = new HardConstraintCheckerFactory().createConstraintChecker(model);
        
        final TestModel groupModel = GroupSpecificTestModel.positive(model, checker);
        final List<int[]> testSuite = new IpogAlgorithm(groupModel).generate();
        
        assertFalse(testSuite.stream().anyMatch((int[] test) -> CombinationUtil.contains(test, new int[]{1, 1, -1, -1})));
        assertFalse(testSuite.stream().anyMatch((int[] test) -> CombinationUtil.contains(test, new int[]{-1, 1, 1, -1})));
    }
    
    @Test
    void checkWithImplicitForbiddenTuple() {
        final List<TupleList> exclusionTupleLists = new ArrayList<>();
        exclusionTupleLists.add(new TupleList(1, new int[]{0, 1}, List.of(new int[]{0, 0}, new int[]{1, 1})));
        exclusionTupleLists.add(new TupleList(2, new int[]{1, 2}, List.of(new int[]{1, 1})));
        
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 2, 2, 2)
                .exclusionTupleLists(exclusionTupleLists)
                .build();

        final ConstraintChecker checker = new HardConstraintCheckerFactory().createConstraintChecker(model);
    
        final TestModel groupModel = GroupSpecificTestModel.positive(model, checker);
        final List<int[]> testSuite = new IpogAlgorithm(groupModel).generate();
        
        assertFalse(testSuite.stream().anyMatch((int[] test) -> CombinationUtil.contains(test, new int[]{0, -1, 1, -1})));
    }
    
    @Test
    void checkWithUnsatisfiableConstraint() {
        final List<TupleList> exclusionTupleLists = new ArrayList<>();
        exclusionTupleLists.add(new TupleList(1, new int[]{2}, List.of(new int[]{0}, new int[]{1})));
        
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(2, 2, 2, 2)
                .exclusionTupleLists(exclusionTupleLists)
                .build();

        final ConstraintChecker checker = new HardConstraintCheckerFactory().createConstraintChecker(model);
    
        final TestModel groupModel = GroupSpecificTestModel.positive(model, checker);
        final List<int[]> testSuite = new IpogAlgorithm(groupModel).generate();
        
        assertEquals(0, testSuite.size());
    }
}
