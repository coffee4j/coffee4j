package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.executionmode;

import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombinatorialTestAnnotationBasedExecutionModeProviderTest {
    
    @Test
    void testProvideDefault() throws NoSuchMethodException {
        final ExecutionMode mode = loadExecutionModeFromMethod("testMethodDefault");
        assertEquals(ExecutionMode.EXECUTE_ALL, mode);
    }
    
    @Test
    void testProvideExecuteAll() throws NoSuchMethodException {
        final ExecutionMode mode = loadExecutionModeFromMethod("testMethodExecuteAll");
        assertEquals(ExecutionMode.EXECUTE_ALL, mode);
    }
    
    @Test
    void testProvideFailFast() throws NoSuchMethodException {
        final ExecutionMode mode = loadExecutionModeFromMethod("testMethodFailFast");
        assertEquals(ExecutionMode.FAIL_FAST, mode);
    }
    
    private static ExecutionMode loadExecutionModeFromMethod(String methodName) throws NoSuchMethodException {
        final Method method = TestClass.class.getMethod(methodName);
        final CombinatorialTest annotation = method.getAnnotation(CombinatorialTest.class);
        
        final CombinatorialTestAnnotationBasedExecutionModeProvider provider
                = new CombinatorialTestAnnotationBasedExecutionModeProvider();
        provider.accept(annotation);
        
        return provider.provide(method);
    }
    
    public static class TestClass {
        
        @SuppressWarnings("unused")
        @CombinatorialTest
        public void testMethodDefault() {
            // used as a target for reflection
        }
    
        @SuppressWarnings("unused")
        @CombinatorialTest(executionMode = ExecutionMode.EXECUTE_ALL)
        public void testMethodExecuteAll() {
            // used as a target for reflection
        }
    
        @SuppressWarnings("unused")
        @CombinatorialTest(executionMode = ExecutionMode.FAIL_FAST)
        public void testMethodFailFast() {
            // used as a target for reflection
        }
        
    }

}
