package de.rwth.swc.coffee4j.engine.generator.ipog;

import de.rwth.swc.coffee4j.engine.CombinatorialTestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import de.rwth.swc.coffee4j.engine.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.engine.util.CombinationUtil;
import de.rwth.swc.coffee4j.engine.constraint.ConstraintCheckerFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class IpogWithConstraintsTest {
    
    @Test
    void checkWithSimpleConstraint() {
        final List<TupleList> forbiddenTupleLists = new ArrayList<>();
        forbiddenTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 1})));
        forbiddenTupleLists.add(new TupleList(2, new int[]{1, 2}, Arrays.asList(new int[]{1, 1})));
        
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{2, 2, 2, 2}, forbiddenTupleLists);
        
        final ConstraintChecker checker = new ConstraintCheckerFactory(model).createHardConstraintsChecker();
        
        final List<int[]> testSuite = new Ipog(IpogConfiguration.ipogConfiguration().model(model).checker(checker).build()).generate();
        
        assertFalse(testSuite.stream().anyMatch((int[] test) -> CombinationUtil.contains(test, new int[]{1, 1, -1, -1})));
        assertFalse(testSuite.stream().anyMatch((int[] test) -> CombinationUtil.contains(test, new int[]{-1, 1, 1, -1})));
    }
    
    @Test
    void checkWithImplicitForbiddenTuple() {
        final List<TupleList> forbiddenTupleLists = new ArrayList<>();
        forbiddenTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})));
        forbiddenTupleLists.add(new TupleList(2, new int[]{1, 2}, Arrays.asList(new int[]{1, 1})));
        
        
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{2, 2, 2, 2}, forbiddenTupleLists);
        
        final ConstraintChecker checker = new ConstraintCheckerFactory(model).createHardConstraintsChecker();
        
        final List<int[]> testSuite = new Ipog(IpogConfiguration.ipogConfiguration().model(model).checker(checker).build()).generate();
        
        assertFalse(testSuite.stream().anyMatch((int[] test) -> CombinationUtil.contains(test, new int[]{0, -1, 1, -1})));
    }
    
    @Test
    void checkWithUnsatisfiableConstraint() {
        final List<TupleList> forbiddenTupleLists = new ArrayList<>();
        forbiddenTupleLists.add(new TupleList(1, new int[]{2}, Arrays.asList(new int[]{0}, new int[]{1})));
        
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{2, 2, 2, 2}, forbiddenTupleLists);
        
        final ConstraintChecker checker = new ConstraintCheckerFactory(model).createHardConstraintsChecker();
        
        final List<int[]> testSuite = new Ipog(IpogConfiguration.ipogConfiguration().model(model).checker(checker).build()).generate();
        
        assertEquals(0, testSuite.size());
    }
}
