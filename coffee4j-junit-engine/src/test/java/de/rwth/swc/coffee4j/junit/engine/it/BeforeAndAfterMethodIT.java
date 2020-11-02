package de.rwth.swc.coffee4j.junit.engine.it;

import de.rwth.swc.coffee4j.junit.engine.CombinatorialTestEngine;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.test.AfterMethod;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.lifecycle.test.BeforeMethod;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.platform.testkit.engine.Events;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class BeforeAndAfterMethodIT {

    private static final List<String> methodsInvoked = new ArrayList<>();

    @Test
    void beforeAndAfterMethod() {
        methodsInvoked.clear();
        
        final EngineExecutionResults executionResults = EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(TestCase.class))
                .execute();

        final Events tests = executionResults.tests();

        tests.assertStatistics(
                stats-> stats
                        .started(2)
                        .finished(2)
        );

        assertThat(methodsInvoked)
                .hasSize(4)
                .containsOnlyOnce("beforeTest", "afterTest")
                .startsWith("beforeTest")
                .contains("testMethod")
                .endsWith("afterTest");
    }

    static class TestCase {

        private static InputParameterModel model() {
            return InputParameterModel.inputParameterModel("dummy_model")
                    .parameter(
                            Parameter.parameter("parameter_uno")
                                    .values("eins", "zwei").build()
                    ).build();
        }

        @BeforeMethod
        static void beforeTest() {
            methodsInvoked.add("beforeTest");
        }

        @CombinatorialTest
        @EnableGeneration
        void testMethod(@InputParameter("parameter_uno") String parameter) {
            methodsInvoked.add("testMethod");
        }

        @AfterMethod
        static void afterTest() {
            methodsInvoked.add("afterTest");
        }

    }
}
