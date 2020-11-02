package de.rwth.swc.coffee4j.junit.engine.annotation.util;

import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.junit.engine.annotation.MockingTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.commons.support.HierarchyTraversalMode.TOP_DOWN;
import static org.junit.platform.commons.support.ReflectionSupport.findMethods;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReflectionUtilsTest implements MockingTest {

    private final static String firstParamName = "paramDos";
    private final static String secondParamName = "paramUno";

    @Test
    void getInputForMethod() {
        final List<Method> methods = findMethods(TestCase.class, method -> true, TOP_DOWN);
        assertThat(methods)
                .hasSize(1);
        final Method method = methods.get(0);

        Combination combo = mock(Combination.class);
        String firstParamValue = "firstParamValue";
        String secondParamValue = "secondParamValue";
        when(combo.getRawValue(firstParamName)).thenReturn(firstParamValue);
        when(combo.getRawValue(secondParamName)).thenReturn(secondParamValue);

        Object[] extractedInput = ReflectionUtils.getInputForMethod(combo, method);

        assertThat(extractedInput)
                .containsExactly(firstParamValue, secondParamValue);
    }

    @Test
    void singleParameterNameExtraction() {
        final List<Method> methods = findMethods(TestCase.class, method -> true, TOP_DOWN);
        assertThat(methods)
                .hasSize(1);
        final Method method = methods.get(0);
        final Parameter[] parameters = method.getParameters();
        assertThat(parameters)
                .hasSize(2);
        final Parameter parameter = parameters[0];

        String extractedName = ReflectionUtils.extractMethodParameterName(parameter);
        assertThat(extractedName)
                .isEqualTo(firstParamName);
    }

    @Test
    void multipleParameterNamesExtraction() {
        final List<Method> methods = findMethods(TestCase.class, method -> true, TOP_DOWN);
        assertThat(methods)
                .hasSize(1);
        final Method method = methods.get(0);

        List<String> extractedNames = ReflectionUtils.extractMethodParameterNames(method);
        assertThat(extractedNames)
                .containsExactly(firstParamName, secondParamName);
    }

    static class TestCase {

        void someMethod(@InputParameter(firstParamName) String parameterDos,
                        @InputParameter(secondParamName) String parameterUno) {}
    }
    
}
