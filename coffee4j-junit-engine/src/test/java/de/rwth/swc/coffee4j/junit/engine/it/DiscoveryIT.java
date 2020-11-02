package de.rwth.swc.coffee4j.junit.engine.it;


import de.rwth.swc.coffee4j.junit.engine.CombinatorialTestEngine;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
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
        final Events container = executionResults.containers();
        final Events tests = executionResults.tests();

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

