package io.github.coffee4j.junit.engine.it;


import io.github.coffee4j.junit.engine.CombinatorialTestEngine;
import io.github.coffee4j.engine.configuration.model.InputParameterModel;
import io.github.coffee4j.engine.configuration.model.Parameter;
import io.github.coffee4j.junit.engine.annotation.CombinatorialTest;
import io.github.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import io.github.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.platform.testkit.engine.Events;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.container;
import static org.junit.platform.testkit.engine.EventConditions.displayName;
import static org.junit.platform.testkit.engine.EventConditions.test;

class DiscoveryIT {

    @Test
    void discoverOneTest() {
        final EngineExecutionResults executionResults = EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(OneMethodTestCase.class))
                .execute();
        final Events container = executionResults.containerEvents();
        final Events tests = executionResults.testEvents();

        container.assertThatEvents()
                .haveAtLeastOne(container("testMethod"))
                .haveAtLeastOne(displayName("testMethod"));

        tests.assertThatEvents()
                .have(test("testMethod"));
        
        tests.assertThatEvents()
                .haveAtLeastOne(test("parameter_uno"));
    }

    static class OneMethodTestCase {

        private static InputParameterModel model() {
            return InputParameterModel.inputParameterModel("dummy_model")
                    .parameter(
                            Parameter.parameter("parameter_uno")
                                    .values("eins", "zwei").build()
                    ).build();
        }

        @CombinatorialTest
        @EnableGeneration
        void testMethod(@InputParameter("parameter_uno") String parameterUno) {
            // no-op
        }
    }
}

