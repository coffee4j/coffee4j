package de.rwth.swc.coffee4j.engine.configuration;

import de.rwth.swc.coffee4j.engine.MockingTest;
import de.rwth.swc.coffee4j.engine.configuration.execution.TestInputExecutor;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TestMethodConfigurationTest implements MockingTest {

    private static final InputParameterModel model = mock(InputParameterModel.class);
    private static final TestInputExecutor executor = mock(TestInputExecutor.class);

    private static TestMethodConfiguration configuration;

    @BeforeAll
    static void prepareTests() {
        configuration = TestMethodConfiguration.testMethodConfiguration()
                .inputParameterModel(model)
                .testExecutor(executor)
                .build();
    }

    @Test
    void setsInputParameterModel() {
        assertThat(configuration.getInputParameterModel())
                .isEqualTo(model);
    }
    
    @Test
    void setsTestInputExecutors() {
        assertThat(configuration.getTestInputExecutor())
                .isEqualTo(executor);
    }

}