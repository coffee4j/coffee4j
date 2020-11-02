package de.rwth.swc.coffee4j.junit.engine.execution;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.all.AfterClass;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.all.BeforeClass;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.combination.AfterCombination;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.combination.BeforeCombination;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.test.AfterMethod;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.test.BeforeMethod;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class LifecycleExecutor {

    public void executeBeforeClass(Class<?> testClass) {
        executeLifecycleMethod(BeforeClass.class, testClass, null, null);
    }
    
    public void executeAfterClass(Class<?> testClass) {
        executeLifecycleMethod(AfterClass.class, testClass, null, null);
    }
    
    public void executeBeforeMethod(Object testInstance) {
        executeLifecycleMethod(BeforeMethod.class, testInstance.getClass(), testInstance, null);
    }
    
    public void executeAfterMethod(Object testInstance) {
        executeLifecycleMethod(AfterMethod.class, testInstance.getClass(), testInstance, null);
    }
    
    public void executeBeforeCombination(Object testInstance, Combination combination) {
        executeLifecycleMethod(BeforeCombination.class, testInstance.getClass(), testInstance, combination);
    }
    
    public void executeAfterCombination(Object testInstance, Combination combination) {
        executeLifecycleMethod(AfterCombination.class, testInstance.getClass(), testInstance, combination);
    }
    
    private void executeLifecycleMethod(
            Class<? extends Annotation> annotationClass, Class<?> testClass,
            Object testInstance, Combination combination) {
        
        final List<CombinationBasedMethodInvoker> invokers = AnnotationSupport.findAnnotatedMethods(
                testClass, annotationClass, HierarchyTraversalMode.TOP_DOWN).stream()
                        .map(method -> new CombinationBasedMethodInvoker(testInstance, method))
                        .collect(Collectors.toList());
        
        for (CombinationBasedMethodInvoker invoker : invokers) {
            try {
                invoker.execute(combination);
            } catch (Coffee4JException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throw new Coffee4JException(
                        "Could not execute method for lifecycle " + annotationClass.getName(), throwable);
            }
        }
    }

}
