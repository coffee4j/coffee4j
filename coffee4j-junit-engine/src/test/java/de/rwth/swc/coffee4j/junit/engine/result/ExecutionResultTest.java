package de.rwth.swc.coffee4j.junit.engine.result;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutionResultTest {
    @Test
    void testEqualMethods() throws ErrorConstraintException {
        ExceptionalValueResult generatingExceptionalValueResult1 = new ExceptionalValueResult("FAILURE GENERATION", true);
        ExceptionalValueResult generatingExceptionalValueResult2 = new ExceptionalValueResult("FAILURE GENERATION", true);
        ExceptionalValueResult generatingExceptionalValueResult3 = new ExceptionalValueResult("FAILURE", true);
        ExceptionalValueResult exceptionalValueResult1 = new ExceptionalValueResult("FAILURE", false);
        ExceptionalValueResult exceptionalValueResult2 = new ExceptionalValueResult("FAILURE", false);
        ExceptionalValueResult exceptionalValueResult3 = new ExceptionalValueResult("FAILURE1", false);
        
        ExceptionResult generatingExceptionResult1 = new ExceptionResult(new NullPointerException(), true);
        ExceptionResult generatingExceptionResult2 = new ExceptionResult(new NullPointerException(), true);
        ExceptionResult exceptionResult1 = new ExceptionResult(new NullPointerException(), false);
        ExceptionResult exceptionResult2 = new ExceptionResult(new NullPointerException(), false);
        
        ValueResult result1 = new ValueResult("SUCCESS");
        ValueResult result2 = new ValueResult("SUCCESS");
        ValueResult result3 = new ValueResult("SUCCESS1");
        
        assertThrows(ErrorConstraintException.class, () -> generatingExceptionalValueResult1.equals(generatingExceptionalValueResult2));
        assertThrows(ErrorConstraintException.class, () -> generatingExceptionResult1.equals(generatingExceptionResult2));
    
        assertEquals(exceptionalValueResult1, exceptionalValueResult2);
        assertEquals(exceptionResult1, exceptionResult2);
        assertEquals(result1, result2);
    
        assertEquals(generatingExceptionalValueResult1, generatingExceptionalValueResult1);
        assertEquals(exceptionalValueResult1, exceptionalValueResult1);
    
        assertEquals(generatingExceptionResult1, generatingExceptionResult1);
        assertEquals(exceptionResult1, exceptionResult1);
    
        assertEquals(result1, result1);
        assertEquals(result2, result2);
    
        assertNotEquals(generatingExceptionalValueResult1, exceptionalValueResult1);
        assertNotEquals(exceptionalValueResult2, generatingExceptionalValueResult2);
        assertNotEquals(result1, exceptionalValueResult1);
    
        assertNotEquals(generatingExceptionalValueResult1, generatingExceptionalValueResult3);
        assertNotEquals(exceptionalValueResult1, exceptionalValueResult3);
        assertNotEquals(result1, result3);
    }
}
