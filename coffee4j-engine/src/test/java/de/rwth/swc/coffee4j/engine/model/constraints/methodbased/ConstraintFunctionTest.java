package de.rwth.swc.coffee4j.engine.model.constraints.methodbased;

import de.rwth.swc.coffee4j.engine.configuration.model.constraints.methodbased.ConstraintFunction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

interface ConstraintFunctionTest {
    
    ConstraintFunction getFunction();
    
    List<?> getTooFewValues();
    
    List<?> getTooManyValues();
    
    List<?> getValuesEvaluatingToTrue();
    
    List<?> getValuesEvaluatingToFalse();
    
    List<?> getValuesOfWrongType();
    
    @Test
    default void preconditions() {
        final ConstraintFunction constraintFunction = getFunction();
        
        assertThrows(NullPointerException.class, () -> constraintFunction.check(null));
        assertThrows(IllegalArgumentException.class, () -> constraintFunction.check(getTooFewValues()));
        assertThrows(IllegalArgumentException.class, () -> constraintFunction.check(getTooManyValues()));
    }
    
    @Test
    default void correctlyEvaluatesAndCastsObjects() {
        final ConstraintFunction constraintFunction = getFunction();
        
        assertTrue(constraintFunction.check(getValuesEvaluatingToTrue()));
        assertFalse(constraintFunction.check(getValuesEvaluatingToFalse()));
    }
    
    @Test
    default void throwsExceptionIfValueCannotBeCast() {
        final ConstraintFunction constraintFunction = getFunction();
        
        assertThrows(ClassCastException.class, () -> constraintFunction.check(getValuesOfWrongType()));
    }
    
}
