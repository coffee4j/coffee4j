package de.rwth.swc.coffee4j.junit.engine.execution;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.junit.engine.UniqueIdGenerator;
import de.rwth.swc.coffee4j.junit.engine.descriptor.CombinationDescriptor;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.execution.TestInputExecutor;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;

import java.lang.reflect.Method;

public class CombinationExecutor implements TestInputExecutor {
    
    private final ExecutionContext executionContext;
    private final String namePattern;
    private final CombinationBasedMethodInvoker methodInvoker;
    
    public CombinationExecutor(ExecutionContext executionContext) {
        Preconditions.notNull(executionContext, "executionContext must not be null");
        Preconditions.check(executionContext.getMethodDescriptor().isPresent(), "Must have test methodDescriptor");
        Preconditions.check(executionContext.getTestInstance().isPresent(), "Must have test instance");
        
        this.executionContext = executionContext;
        this.namePattern = loadNamePattern(executionContext.getRequiredMethodDescriptor().getMethod());
        methodInvoker = new CombinationBasedMethodInvoker(executionContext.getRequiredTestInstance(),
                executionContext.getRequiredMethodDescriptor().getMethod());
    }
    
    private static String loadNamePattern(Method method) {
        return AnnotationSupport.findAnnotation(method, CombinatorialTest.class)
                .map(CombinatorialTest::name)
                .map(String::trim)
                .orElseThrow(() -> new JUnitException("Could not find required name pattern in annotation"));
    }
    
    @Override
    public TestResult execute(Combination combination) {
        final UniqueId parentId = executionContext.getRequiredMethodDescriptor().getUniqueId();
        final UniqueId combinationId = UniqueIdGenerator.appendIdFromCombination(parentId, combination);
        final String displayName = DisplayNameFormatter.format(namePattern, combination);
        final CombinationDescriptor descriptor = new CombinationDescriptor(
                combinationId, displayName, executionContext.getRequiredMethodDescriptor().getMethod());
        
        executionContext.getRequiredMethodDescriptor().addChild(descriptor);
        executionContext.getExecutionListener().dynamicTestRegistered(descriptor);
        executionContext.getExecutionListener().executionStarted(descriptor);
        
        final LifecycleExecutor lifecycleExecutor = executionContext.getRequiredLifecycleExecutor();
        final Object testInstance = executionContext.getRequiredTestInstance();
        
        lifecycleExecutor.executeBeforeCombination(testInstance, combination);
        
        TestResult testResult;
        TestExecutionResult junitTestResult;
        try {
            methodInvoker.execute(combination);
            testResult = TestResult.success();
            junitTestResult = TestExecutionResult.successful();
        } catch (Coffee4JException exception) {
            throw exception;
        } catch (ErrorConstraintException exception) {
            testResult = TestResult.exceptionalSuccess(exception);
            junitTestResult = TestExecutionResult.successful();
        } catch (AssertionError | Exception exception) {
            testResult = TestResult.failure(exception);
            junitTestResult = TestExecutionResult.failed(exception);
        } catch (Throwable error) {
            throw new Coffee4JException("Couldn't execute test executor", error);
        }
        
        executionContext.getExecutionListener().executionFinished(descriptor, junitTestResult);
        lifecycleExecutor.executeAfterCombination(testInstance, combination);
        
        return testResult;
    }
    
}
