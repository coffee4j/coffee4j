package de.rwth.swc.coffee4j.junit.engine.execution;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.all.AfterClass;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.all.BeforeClass;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.combination.AfterCombination;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.combination.BeforeCombination;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.test.AfterMethod;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.test.BeforeMethod;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.Value.value;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LifecycleExecutorTest {
    
    private static final Combination combination = Combination.of(Map.of(
            parameter("first").values(1, 2).build(), value(0, 1),
            parameter("second").values(1, 2).build(), value(1, 2)));
    
    private static List<String> executedMethods;
    private static List<Integer> providedValues;
    
    private final LifecycleExecutor lifecycleExecutor = new LifecycleExecutor();
    private final TestClass instance = new TestClass("test");
    
    @BeforeEach
    void clearExecutedMethods() {
        executedMethods = new ArrayList<>();
        providedValues = new ArrayList<>();
    }
    
    @Test
    void executesBeforeClassMethods() {
        lifecycleExecutor.executeBeforeClass(TestClass.class);
        assertThat(executedMethods)
            .containsExactly("beforeClass1", "beforeClass2");
    }
    
    @Test
    void executesAfterClassMethods() {
        lifecycleExecutor.executeAfterClass(TestClass.class);
        assertThat(executedMethods)
                .containsExactly("afterClass1", "afterClass2");
    }
    
    @Test
    void executesBeforeMethodMethods() {
        lifecycleExecutor.executeBeforeMethod(instance);
        assertThat(executedMethods)
                .containsExactly("testbeforeMethod1", "testbeforeMethod2");
    }
    
    @Test
    void executesAfterMethodMethods() {
        lifecycleExecutor.executeAfterMethod(instance);
        assertThat(executedMethods)
                .containsExactly("testafterMethod1", "testafterMethod2");
    }
    
    @Test
    void executesBeforeCombinationMethods() {
        lifecycleExecutor.executeBeforeCombination(instance, combination);
        assertThat(executedMethods)
                .containsExactly("testbeforeCombination1", "testbeforeCombination2");
        assertThat(providedValues)
                .containsExactly(1, 2, 1, 2);
    }
    
    @Test
    void executesAfterCombinationMethods() {
        lifecycleExecutor.executeAfterCombination(instance, combination);
        assertThat(executedMethods)
                .containsExactly("testafterCombination1", "testafterCombination2");
        assertThat(providedValues)
                .containsExactly(1, 2, 1, 2);
    }
    
    @Test
    void catchesAndConvertsExceptionsThrownInMethods() {
        final Exception coffee4JException = assertThrows(Coffee4JException.class,
                () -> lifecycleExecutor.executeAfterMethod(new ThrowingTestClass()));
        assertEquals(coffee4JException.getCause().getClass(), IllegalArgumentException.class);
    }
    
    private static final class ThrowingTestClass {
        
        @AfterMethod
        void afterMethod() {
            throw new IllegalArgumentException("test");
        }
        
    }
    
    private static final class TestClass {
        
        private final String prefix;
        
        private TestClass(String prefix) {
            this.prefix = Objects.requireNonNull(prefix);
        }
        
        @BeforeClass
        static void beforeClass1() {
            executedMethods.add("beforeClass1");
        }
    
        @BeforeClass
        static void beforeClass2() {
            executedMethods.add("beforeClass2");
        }
        
        @AfterClass
        static void afterClass1() {
            executedMethods.add("afterClass1");
        }
    
        @AfterClass
        static void afterClass2() {
            executedMethods.add("afterClass2");
        }
        
        @BeforeMethod
        void beforeMethod1() {
            executedMethods.add(prefix + "beforeMethod1");
        }
    
        @BeforeMethod
        void beforeMethod2() {
            executedMethods.add(prefix + "beforeMethod2");
        }
        
        @AfterMethod
        void afterMethod1() {
            executedMethods.add(prefix + "afterMethod1");
        }
    
        @AfterMethod
        void afterMethod2() {
            executedMethods.add(prefix + "afterMethod2");
        }
        
        @BeforeCombination
        void beforeCombination1(@InputParameter("first") int first, @InputParameter("second") int second) {
            providedValues.add(first);
            providedValues.add(second);
            executedMethods.add(prefix + "beforeCombination1");
        }
    
        @BeforeCombination
        void beforeCombination2(@InputParameter("first") int first, @InputParameter("second") int second) {
            providedValues.add(first);
            providedValues.add(second);
            executedMethods.add(prefix + "beforeCombination2");
        }
        
        @AfterCombination
        void afterCombination1(@InputParameter("first") int first, @InputParameter("second") int second) {
            providedValues.add(first);
            providedValues.add(second);
            executedMethods.add(prefix + "afterCombination1");
        }
    
        @AfterCombination
        void afterCombination2(@InputParameter("first") int first, @InputParameter("second") int second) {
            providedValues.add(first);
            providedValues.add(second);
            executedMethods.add(prefix + "afterCombination2");
        }
        
    }

}
