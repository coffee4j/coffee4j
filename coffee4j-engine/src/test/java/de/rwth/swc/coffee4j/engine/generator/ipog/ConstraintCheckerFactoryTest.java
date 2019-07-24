package de.rwth.swc.coffee4j.engine.generator.ipog;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import de.rwth.swc.coffee4j.engine.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.engine.constraint.ConstraintCheckerFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConstraintCheckerFactoryTest {
    
    private static final TestModel IPM = new TestModel(2, new int[]{2, 2, 2, 2}, Collections.emptyList(), Arrays.asList(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})), new TupleList(2, new int[]{1, 2}, Arrays.asList(new int[]{1, 1}))));
    
    private static final ConstraintCheckerFactory FACTORY = new ConstraintCheckerFactory(IPM);
    
    @Test
    void testCreateHardConstraintsChecker() {
        ConstraintChecker checker = FACTORY.createHardConstraintsChecker();
        
        assertTrue(checker.isValid(new int[]{0, 1, 0, 0}));
        assertFalse(checker.isValid(new int[]{0, 0, 0, 0}));
    }
    
    @Test
    void testCreateHardConstraintsCheckerWithNegation() {
        TupleList tupleList = new TupleList(1, new int[]{0, 1}, Collections.singletonList(new int[]{0, 0}));
        
        ConstraintChecker checker = FACTORY.createHardConstraintsCheckerWithNegation(tupleList);
        
        assertTrue(checker.isValid(new int[]{0, 0, 0, 0}));
        assertFalse(checker.isValid(new int[]{0, 1, 0, 0}));
        assertFalse(checker.isValid(new int[]{0, 1, 1, 0}));
    }
}
