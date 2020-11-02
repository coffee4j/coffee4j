package de.rwth.swc.coffee4j.junit.engine.it;

import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.Ipog;
import de.rwth.swc.coffee4j.engine.process.report.sequential.LoggingSequentialExecutionReporter;
import de.rwth.swc.coffee4j.junit.engine.CombinatorialTestEngine;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.ConfigurationFromMethod;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration.executionConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class ConfigurationMethodIT {

    @Test
    void getConfigurationFromMethod() {
        assertThatCode(() -> EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(ConfigurationMethodTestCase.class))
                .execute())
        .doesNotThrowAnyException();
    }

    @Test
    void buildConfiguration() {
        assertThatCode(() -> EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(BuildingConfigurationTestCase.class))
                .execute())
        .doesNotThrowAnyException();
    }

    /**
     * Demonstrates that a custom configuration can be provided for a {@link CombinatorialTest} using
     * {@link ConfigurationFromMethod}. To see that this really does use the configuration from the method,
     * it only has one generator producing a single test input. The normal default would be IPOG, which would generate
     * two test inputs.
     */
    static class ConfigurationMethodTestCase {
        
        private static final Logger LOG = LoggerFactory.getLogger(ConfigurationMethodTestCase.class);

        @CombinatorialTest(inputParameterModel = "testModel")
        @ConfigurationFromMethod("testConfiguration")
        void test(@InputParameter("param1") String param1) {
            LOG.debug(param1);
        }

        private static InputParameterModel testModel() {
            return inputParameterModel("test").positiveTestingStrength(1).parameter(parameter("param1").values("0", "1")).build();
        }

        private static SequentialExecutionConfiguration testConfiguration() {
            return executionConfiguration()
                    .generator((model, reporter) -> Collections.singletonList(() -> new TestInputGroup(0, List.of(new int[]{0}))))
                    .executionReporter(new LoggingSequentialExecutionReporter())
                    .build();
        }
    }

    static class BuildingConfigurationTestCase {

        @Disabled
        @CombinatorialTest(inputParameterModel = "printer")
        @EnableGeneration(algorithms = Ipog.class)
        void test(@InputParameter("Color") String color, @InputParameter("Format") String format,
                  @InputParameter("Side") String side, @InputParameter("Scale") int scale) {
            assertThat(color).isEqualTo("Color");
            assertThat(scale).isEqualTo(25);
        }

        private static InputParameterModel printer() {
            return inputParameterModel("Printer")
                    .positiveTestingStrength(2)
                    .parameter(parameter("Side").values("One", "Double"))
                    .parameter(parameter("Color").values("Color", "Grayscale", "B&W"))
                    .parameter(parameter("Format").values("A2", "A3", "A4", "A5"))
                    .parameter(parameter("Scale").values(25, 50, 75, 100))
                    .build();
        }
    }
}




