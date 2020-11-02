package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.executionmode;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExecutionModeLoaderTest {
    
    @Test
    void loadsFromCombinatorialTestAnnotationIfNoProvider() throws NoSuchMethodException {
        final ExecutionMode mode = loadExecutionMode("testMethodDefaultFromAnnotation");
        assertEquals(ExecutionMode.FAIL_FAST, mode);
    }
    
    @Test
    void loadsFromNormalSourceAnnotationWithCustomProvider() throws NoSuchMethodException {
        final ExecutionMode mode = loadExecutionMode("testMethodCustomProvider");
        assertEquals(ExecutionMode.EXECUTE_ALL, mode);
    }
    
    @Test
    void loadsFromCustomSource() throws NoSuchMethodException {
        final ExecutionMode mode = loadExecutionMode("testMethodCustomSource");
        assertEquals(ExecutionMode.EXECUTE_ALL, mode);
    }
    
    @Test
    void throwsExceptionIfNoModeProvided() {
        assertThrows(Coffee4JException.class, () -> loadExecutionMode("testMethodModeNull"));
    }
    
    private static ExecutionMode loadExecutionMode(String methodName) throws NoSuchMethodException {
        final Method method = TestClass.class.getMethod(methodName);
        final ExecutionModeLoader loader = new ExecutionModeLoader();
        
        return loader.load(method);
    }
    
    public static class TestClass {
        
        @CombinatorialTest(executionMode = ExecutionMode.FAIL_FAST)
        public void testMethodDefaultFromAnnotation() {
            // only used as a target for reflection
        }
        
        @CombinatorialTest(executionMode = ExecutionMode.FAIL_FAST)
        @ExecutionModeSource(ExecuteAllProvider.class)
        public void testMethodCustomProvider() {
            // only used as a target for reflection
        }
    
        @CombinatorialTest(executionMode = ExecutionMode.FAIL_FAST)
        @ExecuteAll
        public void testMethodCustomSource() {
            // only used as a target for reflection
        }
        
        @CombinatorialTest(executionMode = ExecutionMode.FAIL_FAST)
        @ExecutionModeSource(NoExecutionModeProvider.class)
        public void testMethodModeNull() {
            // only used as a target for reflection
        }
        
        public static class ExecuteAllProvider implements ExecutionModeProvider {
    
            @Override
            public ExecutionMode provide(Method method) {
                return ExecutionMode.EXECUTE_ALL;
            }
    
        }
        
        public static class NoExecutionModeProvider implements ExecutionModeProvider {
    
            @Override
            public ExecutionMode provide(Method method) {
                return null;
            }
    
        }
    
        @Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
        @Retention(RetentionPolicy.RUNTIME)
        @ExecutionModeSource(ExecuteAllProvider.class)
        public @interface ExecuteAll {
        }
        
    }
    
}
