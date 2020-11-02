package de.rwth.swc.coffee4j.junit.engine.annotation.configuration;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.ConfigurationLoader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.ConfigurationProvider;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.ConfigurationSource;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.MockingTest;
import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ConfigurationLoaderTest implements MockingTest {

    private static final SequentialExecutionConfiguration mockedConfig =
            mock(SequentialExecutionConfiguration.class);
    private static boolean initialized = false;

    @Test
    void classAnnotated() throws NoSuchMethodException {
        final Method method = this.getClass().getMethod("classAnnotatedTestMethod");
        final SequentialExecutionConfiguration loadedConfig =
                new ConfigurationLoader().load(method);
        assertEquals(mockedConfig, loadedConfig);
        assertTrue(initialized);
    }

    @Test
    void corruptedProvider() throws NoSuchMethodException {
        final Method method = this.getClass().getMethod("corruptedClassAnnotatedTestMethod");
        Assertions.assertThrows(Coffee4JException.class,
                () -> new ConfigurationLoader().load(method));
    }


    @ConfigurationSource(BasicConfigurationProvider.class)
    public void classAnnotatedTestMethod() {
    
    }

    @ConfigurationSource(CorruptedProvider.class)
    public void corruptedClassAnnotatedTestMethod() {
    
    }

    private static class BasicConfigurationProvider implements ConfigurationProvider,
            AnnotationConsumer<ConfigurationSource> {

        @Override
        public SequentialExecutionConfiguration provide(Method method) {
            return mockedConfig;
        }

        @Override
        public void accept(ConfigurationSource configurationSource) {
            initialized = true;
        }
    }

    private static class CorruptedProvider implements ConfigurationProvider {

        @Override
        public SequentialExecutionConfiguration provide(Method method) {
            return null;
        }

    }
}
