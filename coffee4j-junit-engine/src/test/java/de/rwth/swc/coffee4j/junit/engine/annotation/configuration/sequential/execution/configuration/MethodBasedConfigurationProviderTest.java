package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration;

import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.MockingTest;
import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MethodBasedConfigurationProviderTest implements MockingTest {

    private static final SequentialExecutionConfiguration mockedConfig =
            mock(SequentialExecutionConfiguration.class);
    private static final SequentialExecutionConfiguration.Builder mockedBuilder =
            mock(SequentialExecutionConfiguration.Builder.class);

    @BeforeAll
    static void prepareMock() {
        when(mockedBuilder.build()).thenReturn(mockedConfig);
    }

    @Test
    void classAnnotated() throws NoSuchMethodException {
        final ConfigurationProvider configProvider = new MethodBasedConfigurationProvider();
        final Method configurationTestMethod = this.getClass().getMethod("configurationTestMethod");
        final ConfigurationFromMethod configurationFromMethod =
                findAnnotation(configurationTestMethod, ConfigurationFromMethod.class).get();
    
        final Method corruptedTestMethod = this.getClass().getMethod("corruptedTestMethod");
        final ConfigurationFromMethod corruptedMethod =
                findAnnotation(corruptedTestMethod, ConfigurationFromMethod.class).get();

        @SuppressWarnings("unchecked")
        AnnotationConsumer<ConfigurationFromMethod> asConsumer =
                assertDoesNotThrow(() -> (AnnotationConsumer<ConfigurationFromMethod>) configProvider);

        asConsumer.accept(configurationFromMethod);
        final SequentialExecutionConfiguration loadedConfig =
                configProvider.provide(configurationTestMethod);
        assertEquals(mockedConfig, loadedConfig);

        final Method buildTestMethod = this.getClass().getMethod("buildTestMethod");
        final SequentialExecutionConfiguration loadedConfigFromBuilder =
                configProvider.provide(buildTestMethod);
        assertEquals(mockedConfig, loadedConfigFromBuilder);

        asConsumer.accept(corruptedMethod);
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> configProvider.provide(corruptedTestMethod));
    }
    
    @ConfigurationFromMethod("configurationMethod")
    public void configurationTestMethod() {
    
    }
    
    SequentialExecutionConfiguration configurationMethod() {
        return mockedConfig;
    }
    
    @ConfigurationFromMethod("buildMethod")
    public void buildTestMethod() {
    
    }
    
    SequentialExecutionConfiguration.Builder builderMethod() {
        return mockedBuilder;
    }
    
    @ConfigurationFromMethod("corruptedMethod")
    public void corruptedTestMethod() {
    
    }
    
    SequentialExecutionConfiguration corruptedMethod() {
        return null;
    }

}
