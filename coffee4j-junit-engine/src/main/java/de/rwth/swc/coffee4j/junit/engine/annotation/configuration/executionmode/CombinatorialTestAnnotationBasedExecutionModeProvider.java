package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.executionmode;

import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;

import java.lang.reflect.Method;

/**
 * A provider loading the {@link ExecutionMode} as given in the {@link CombinatorialTest} annotation.
 */
public class CombinatorialTestAnnotationBasedExecutionModeProvider implements ExecutionModeProvider,
        AnnotationConsumer<CombinatorialTest> {
    
    private ExecutionMode executionMode;
    
    @Override
    public void accept(CombinatorialTest combinatorialTest) {
        executionMode = combinatorialTest.executionMode();
    }
    
    @Override
    public ExecutionMode provide(Method method) {
        return executionMode;
    }
    
}
