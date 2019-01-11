package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.CombinatorialTestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdaptiveConstraintCheckerTest {
    
    @Test
    void testFirstNegatedTuple() {
        Model model = createModel();
        
        model.arithm((IntVar) ChocoSolverUtil.findVariable(model, 1).get(), "=", 0).post();
        model.arithm((IntVar) ChocoSolverUtil.findVariable(model, 2).get(), "=", 0).post();
        
        assertTrue(model.getSolver().solve());
    }
    
    @Test
    void testSecondNegatedTuple() {
        Model model = createModel();
        
        model.arithm((IntVar) ChocoSolverUtil.findVariable(model, 1).get(), "=", 1).post();
        model.arithm((IntVar) ChocoSolverUtil.findVariable(model, 2).get(), "=", 1).post();
        
        assertTrue(model.getSolver().solve());
    }
    
    @Test
    void testOtherTuple() {
        Model model = createModel();
        
        model.arithm((IntVar) ChocoSolverUtil.findVariable(model, 1).get(), "=", 0).post();
        model.arithm((IntVar) ChocoSolverUtil.findVariable(model, 2).get(), "=", 1).post();
        
        assertFalse(model.getSolver().solve());
    }
    
    private Model createModel() {
        Model model = new Model();
        IntVar var1 = model.intVar("1", 0, 1);
        IntVar var2 = model.intVar("2", 0, 1);
        
        model.or(model.and(model.arithm(var1, "=", 0), model.arithm(var2, "=", 0)), model.and(model.arithm(var1, "=", 1), model.arithm(var2, "=", 1))).getOpposite().getOpposite().post();
        
        BoolVar b1 = model.or(model.and(model.arithm(var1, "=", 0), model.arithm(var2, "=", 0))).getOpposite().reify();
        
        BoolVar b2 = model.or(model.and(model.arithm(var2, "=", 1))).getOpposite().reify();
        
        model.ifThen(model.and(model.arithm(var1, "=", 0), model.arithm(var2, "=", 0)), model.and(b2));
        
        model.ifThen(model.and(model.arithm(var1, "=", 1), model.arithm(var2, "=", 1)), model.and(b1));
        
        model.ifThen(model.or(model.and(model.arithm(var1, "=", 0), model.arithm(var2, "=", 0)), model.and(model.arithm(var1, "=", 1), model.arithm(var2, "=", 1))).getOpposite(), model.and(b1, b2));
        
        return model;
    }
    
    @Test
    void testNoConflicts() {
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{2, 2}, Collections.emptyList(), Arrays.asList(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0})), new TupleList(2, new int[]{1}, Arrays.asList(new int[]{1}))));
        
        ConstraintChecker checker = new ConstraintCheckerFactory(model).createAdaptiveConstraintsCheckerWithNegation(model.getErrorTupleLists().get(0));
        
        assertTrue(checker.isValid(new int[]{0, 0}));
    }
    
    @Test
    void testDifferentConflicts() {
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{2, 2}, Collections.emptyList(), Arrays.asList(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})), new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 0})), new TupleList(3, new int[]{1}, Arrays.asList(new int[]{1}))));
        
        ConstraintChecker checker = new ConstraintCheckerFactory(model).createAdaptiveConstraintsCheckerWithNegation(model.getErrorTupleLists().get(0));
        
        assertTrue(checker.isValid(new int[]{0, 0}));
        assertTrue(checker.isValid(new int[]{1, 1}));
        assertFalse(checker.isValid(new int[]{1, 0}));
        assertFalse(checker.isValid(new int[]{0, 1}));
    }
    
    @Test
    void testSameConflicts() {
        final CombinatorialTestModel model = new CombinatorialTestModel(2, new int[]{2, 2}, Collections.emptyList(), Arrays.asList(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})), new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1}))));
        
        ConstraintChecker checker = new ConstraintCheckerFactory(model).createAdaptiveConstraintsCheckerWithNegation(model.getErrorTupleLists().get(0));
        
        assertTrue(checker.isValid(new int[]{0, 0}));
        assertTrue(checker.isValid(new int[]{1, 1}));
        assertFalse(checker.isValid(new int[]{1, 0}));
    }
}
