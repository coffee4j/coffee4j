package de.rwth.swc.coffee4j.junit.engine.execution;

import de.rwth.swc.coffee4j.junit.engine.descriptor.CombinatorialTestMethodDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.EngineExecutionListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ExecutionContextTest {
    
    private static final EngineExecutionListener ENGINE_LISTENER = mock(EngineExecutionListener.class);
    
    @Test
    void throwsExceptionIfRequiredVariableIsNull() {
        final ExecutionContext context = ExecutionContext.fromExecutionListener(ENGINE_LISTENER);
        
        assertThrows(NullPointerException.class, context::getRequiredLifecycleExecutor);
        assertThrows(NullPointerException.class, context::getRequiredMethodDescriptor);
        assertThrows(NullPointerException.class, context::getRequiredTestInstance);
        
        assertTrue(!context.getLifecycleExecutor().isPresent());
        assertTrue(!context.getMethodDescriptor().isPresent());
        assertTrue(!context.getTestInstance().isPresent());
    }
    
    @Test
    void doesNotThrowIfRequiredVariableIsNotNull() {
        final LifecycleExecutor lifecycleExecutor = new LifecycleExecutor();
        final CombinatorialTestMethodDescriptor combinatorialTestMethodDescriptor
                = mock(CombinatorialTestMethodDescriptor.class);
        final Object testInstance = new Object();
        final ExecutionContext context = ExecutionContext.fromExecutionListener(ENGINE_LISTENER)
                .withLifecycleExecutor(lifecycleExecutor)
                .withMethodDescriptor(combinatorialTestMethodDescriptor)
                .withTestInstance(testInstance);
        
        assertEquals(ENGINE_LISTENER, context.getExecutionListener());
        
        assertEquals(lifecycleExecutor, context.getRequiredLifecycleExecutor());
        Assertions.assertEquals(combinatorialTestMethodDescriptor, context.getRequiredMethodDescriptor());
        assertEquals(testInstance, context.getRequiredTestInstance());
        
        assertEquals(lifecycleExecutor, context.getLifecycleExecutor().orElse(null));
        Assertions.assertEquals(combinatorialTestMethodDescriptor, context.getMethodDescriptor().orElse(null));
        assertEquals(testInstance, context.getTestInstance().orElse(null));
    }
    
}
