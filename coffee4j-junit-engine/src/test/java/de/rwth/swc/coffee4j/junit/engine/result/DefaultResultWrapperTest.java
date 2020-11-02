package de.rwth.swc.coffee4j.junit.engine.result;

import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.interleaving.EnableInterleavingConstraintGeneration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultResultWrapperTest {
    
    @Test
    void testResultRapper() throws NoSuchMethodException {
        DefaultResultWrapper wrapper1 = new DefaultResultWrapper(true);
        DefaultResultWrapper wrapper2 = new DefaultResultWrapper(this.getClass().getMethod("someMethod"));
        
        ExecutionResult result1 = wrapper1.runTestFunction(() -> someFunction(0));
        ExecutionResult result2 = wrapper2.runTestFunction(() -> someFunction(1));
        
        assertTrue(result1 instanceof ExceptionResult);
        assertTrue(result2 instanceof ValueResult);
    }
    
    boolean someFunction(int x) throws IllegalArgumentException {
        if (x == 0) {
            throw new IllegalArgumentException();
        }
        
        return true;
    }
    
    @EnableInterleavingConstraintGeneration
    public void someMethod() {
    }
    
}
