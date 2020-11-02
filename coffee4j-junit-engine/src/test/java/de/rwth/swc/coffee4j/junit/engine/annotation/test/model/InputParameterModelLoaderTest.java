package de.rwth.swc.coffee4j.junit.engine.annotation.test.model;

import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.MockingTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class InputParameterModelLoaderTest implements MockingTest {

    private static final InputParameterModel mockedModel = mock(InputParameterModel.class);
    private static boolean providerInitialized;

    @BeforeEach
    void resetInitialization() {
        providerInitialized = false;
    }

    @Test
    void methodAnnotated() throws NoSuchMethodException {
        final Method method = this.getClass().getMethod("testMethod");
        final InputParameterModel loadedModel = new InputParameterModelLoader().load(method);
        assertEquals(mockedModel, loadedModel);
        assertTrue(providerInitialized, "Model Provider was not initialized.");
    }
    
    @InputParameterModelSource(BasicModelProvider.class)
    public void testMethod() {
    }

    static class BasicModelProvider implements InputParameterModelProvider, AnnotationConsumer<InputParameterModelSource> {

        @Override
        public InputParameterModel provide(Method method) {
            return mockedModel;
        }

        @Override
        public void accept(InputParameterModelSource inputParameterModelSource) {
            providerInitialized = true;
        }
    }

}
