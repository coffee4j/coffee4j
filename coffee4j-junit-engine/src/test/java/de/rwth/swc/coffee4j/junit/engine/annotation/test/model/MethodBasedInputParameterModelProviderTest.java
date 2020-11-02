package de.rwth.swc.coffee4j.junit.engine.annotation.test.model;

import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.junit.engine.annotation.MockingTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;

import static de.rwth.swc.coffee4j.junit.engine.annotation.test.model.MethodBasedInputParameterModelProvider.extractModelFromMethod;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MethodBasedInputParameterModelProviderTest implements MockingTest {

    private static final InputParameterModel mockedModel = mock(InputParameterModel.class);
    private static final InputParameterModel.Builder mockedBuilder = mock(InputParameterModel.Builder.class);

    @BeforeAll
    static void prepareMock() {
        when(mockedBuilder.build()).thenReturn(mockedModel);
    }

    @Test
    void staticMethodExtraction() {
        final Method modelMethod = ReflectionSupport.findMethod(TestCase.class, "getModelStatic").get();
        final InputParameterModel loadedModel = extractModelFromMethod(modelMethod);
        assertEquals(mockedModel, loadedModel);

        final Method builderMethod = ReflectionSupport.findMethod(TestCase.class, "getBuilderStatic").get();
        final InputParameterModel loadedModelFromBuilder = extractModelFromMethod(builderMethod);
        assertEquals(mockedModel, loadedModelFromBuilder);
    }

    @Test
    void methodExtraction() {
        final Method modelMethod = ReflectionSupport.findMethod(TestCase.class, "getModel").get();
        final InputParameterModel loadedModel = extractModelFromMethod(modelMethod);
        assertEquals(mockedModel, loadedModel);

        final Method builderMethod = ReflectionSupport.findMethod(TestCase.class, "getBuilder").get();
        final InputParameterModel loadedModelFromBuilder = extractModelFromMethod(builderMethod);
        assertEquals(mockedModel, loadedModelFromBuilder);
    }

    static class TestCase {

        private static InputParameterModel getModelStatic() {
            return mockedModel;
        }

        private static InputParameterModel.Builder getBuilderStatic() {
            return mockedBuilder;
        }

        private InputParameterModel getModel() {
            return mockedModel;
        }

        private InputParameterModel.Builder getBuilder() {
            return mockedBuilder;
        }
    }
}
