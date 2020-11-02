package de.rwth.swc.coffee4j.junit.engine.annotation.test;

import de.rwth.swc.coffee4j.engine.configuration.execution.TestInputExecutor;
import de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.model.InputParameterModelLoader;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Loads the {@link TestMethodConfiguration} of a {@link CombinatorialTest}
 * by delegating the loading mechanisms to the appropriate loaders
 */
public class TestConfigurationLoader implements Loader<TestMethodConfiguration> {
    
    private final TestInputExecutor testInputExecutor;
    
    public TestConfigurationLoader(TestInputExecutor testInputExecutor) {
        this.testInputExecutor = Objects.requireNonNull(testInputExecutor);
    }
    
    @Override
    public TestMethodConfiguration load(Method method) {
        return TestMethodConfiguration.testMethodConfiguration()
                .inputParameterModel(new InputParameterModelLoader().load(method))
                .testExecutor(testInputExecutor)
                .build();
    }
}
