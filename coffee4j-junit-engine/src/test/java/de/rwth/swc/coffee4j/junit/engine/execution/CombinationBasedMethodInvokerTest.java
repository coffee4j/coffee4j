package de.rwth.swc.coffee4j.junit.engine.execution;

import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.Value.value;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CombinationBasedMethodInvokerTest {
    
    private static List<String> invokedMethods;
    
    @BeforeEach
    void clearList() {
        invokedMethods = new ArrayList<>();
    }
    
    @Test
    void instanceCanOnlyBeNullIfMethodIsStatic() throws NoSuchMethodException {
        final Method staticMethod = getClass().getMethod("staticMethod");
        assertDoesNotThrow(() -> new CombinationBasedMethodInvoker(null, staticMethod));
        
        final Method nonStaticMethod = getClass().getMethod("nonStaticMethod");
        assertThrows(IllegalArgumentException.class, () -> new CombinationBasedMethodInvoker(null, nonStaticMethod));
        assertDoesNotThrow(() -> new CombinationBasedMethodInvoker(this, nonStaticMethod));
    }
    
    @Test
    void canCallNonStaticMethodWithoutParameters() throws Throwable {
        final Method nonStaticMethod = getClass().getMethod("nonStaticMethod");
        final CombinationBasedMethodInvoker invoker = new CombinationBasedMethodInvoker(this, nonStaticMethod);
        
        invoker.execute(null);
        
        assertEquals(1, invokedMethods.size());
        assertEquals("nonStaticMethod", invokedMethods.get(0));
    }
    
    @Test
    void canCallStaticMethodWithoutParameters() throws Throwable {
        final Method staticMethod = getClass().getMethod("staticMethod");
        final CombinationBasedMethodInvoker invoker = new CombinationBasedMethodInvoker(null, staticMethod);
    
        invoker.execute(null);
    
        assertEquals(1, invokedMethods.size());
        assertEquals("staticMethod", invokedMethods.get(0));
    }
    
    @Test
    void canCallStaticMethodWithInstance() throws Throwable {
        final Method staticMethod = getClass().getMethod("staticMethod");
        final CombinationBasedMethodInvoker invoker = new CombinationBasedMethodInvoker(this, staticMethod);
    
        invoker.execute(null);
    
        assertEquals(1, invokedMethods.size());
        assertEquals("staticMethod", invokedMethods.get(0));
    }
    
    @Test
    void resolvesParametersCorrectly() throws Throwable {
        final Method staticMethod = getClass().getMethod("methodWithParameters", String.class, String.class);
        final CombinationBasedMethodInvoker invoker = new CombinationBasedMethodInvoker(this, staticMethod);
        final Combination combination = Combination.of(Map.of(
                parameter("first").values("firstValue", "test").build(), value(0, "firstValue"),
                parameter("second").values("secondValue", "test").build(), value(0, "secondValue")));
        
        invoker.execute(combination);
    
        assertEquals(1, invokedMethods.size());
        assertEquals("methodWithParameters(firstValue, secondValue)", invokedMethods.get(0));
    }
    
    public static void staticMethod() {
        invokedMethods.add("staticMethod");
    }
    
    public void nonStaticMethod() {
        invokedMethods.add("nonStaticMethod");
    }
    
    public void methodWithParameters(@InputParameter("first") String first, @InputParameter("second") String second) {
        invokedMethods.add("methodWithParameters(" + first + ", " + second + ")");
    }
    
}
