package de.rwth.swc.coffee4j.junit.engine.execution;

import de.rwth.swc.coffee4j.junit.engine.descriptor.CombinatorialTestMethodDescriptor;
import org.junit.platform.engine.EngineExecutionListener;

import java.util.Objects;
import java.util.Optional;

public class ExecutionContext {
    
    private final EngineExecutionListener executionListener;
    private final LifecycleExecutor lifecycleExecutor;
    private final Object testInstance;
    private final CombinatorialTestMethodDescriptor methodDescriptor;
    
    private ExecutionContext(EngineExecutionListener executionListener, LifecycleExecutor lifecycleExecutor,
            Object testInstance, CombinatorialTestMethodDescriptor methodDescriptor) {
        
        this.executionListener = Objects.requireNonNull(executionListener);
        this.testInstance = testInstance;
        this.methodDescriptor = methodDescriptor;
        this.lifecycleExecutor = lifecycleExecutor;
    }
    
    public static ExecutionContext fromExecutionListener(EngineExecutionListener executionListener) {
        return new ExecutionContext(executionListener, null, null, null);
    }
    
    public EngineExecutionListener getExecutionListener() {
        return executionListener;
    }
    
    public Optional<CombinatorialTestMethodDescriptor> getMethodDescriptor() {
        return Optional.ofNullable(methodDescriptor);
    }
    
    public CombinatorialTestMethodDescriptor getRequiredMethodDescriptor() {
        if (methodDescriptor == null) {
            throw new NullPointerException("methodDescriptor required");
        }
        
        return methodDescriptor;
    }
    
    public ExecutionContext withMethodDescriptor(CombinatorialTestMethodDescriptor methodDescriptor) {
        return new ExecutionContext(executionListener, lifecycleExecutor, testInstance, methodDescriptor);
    }
    
    public Optional<Object> getTestInstance() {
        return Optional.ofNullable(testInstance);
    }
    
    public Object getRequiredTestInstance() {
        if (testInstance == null) {
            throw new NullPointerException("testInstance is required");
        }
        
        return testInstance;
    }
    
    public ExecutionContext withTestInstance(Object testInstance) {
        return new ExecutionContext(executionListener, lifecycleExecutor, testInstance, methodDescriptor);
    }
    
    public Optional<LifecycleExecutor> getLifecycleExecutor() {
        return Optional.ofNullable(lifecycleExecutor);
    }
    
    public LifecycleExecutor getRequiredLifecycleExecutor() {
        if (lifecycleExecutor == null) {
            throw new NullPointerException("lifecycleExecutor is required");
        }
        
        return lifecycleExecutor;
    }
    
    public ExecutionContext withLifecycleExecutor(LifecycleExecutor lifecycleExecutor) {
        return new ExecutionContext(executionListener, lifecycleExecutor, testInstance, methodDescriptor);
    }
    
}
