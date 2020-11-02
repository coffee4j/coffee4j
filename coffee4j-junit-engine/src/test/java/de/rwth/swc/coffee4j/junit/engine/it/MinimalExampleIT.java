package de.rwth.swc.coffee4j.junit.engine.it;

import de.rwth.swc.coffee4j.junit.engine.CombinatorialTestEngine;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.platform.testkit.engine.Events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.event;
import static org.junit.platform.testkit.engine.EventConditions.finishedSuccessfully;
import static org.junit.platform.testkit.engine.EventConditions.finishedWithFailure;
import static org.junit.platform.testkit.engine.EventConditions.test;

class MinimalExampleIT {

    @Test
    void executesMultipleFunctions() {
        final EngineExecutionResults executionResults = EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(MinimalExampleTestCase.class))
                .execute();

        final Events tests = executionResults.tests();

        tests.assertThatEvents()
                .haveExactly(1,
                        event(
                                test("Chrome"),
                                test("de_DE"),
                                test("Windows"),
                                finishedWithFailure()
                        ))
                .haveExactly(1,
                        event(
                                test("Edge"),
                                test("en_US"),
                                test("Linux"),
                                finishedSuccessfully()
                        ))
                .haveExactly(1,
                        event(
                                test("Safari"),
                                test("fr_BE"),
                                test("MacOS"),
                                finishedSuccessfully()
                        ))
                .haveExactly(1,
                        event(
                                test("Opera"),
                                test("de_DE"),
                                test("Windows"),
                                finishedWithFailure()
                        ));
    }

    static class MinimalExampleTestCase {

        private InputParameterModel modelMethod() {
            return RequirementsModel.BROWSER_IPM;
        }

        @CombinatorialTest(inputParameterModel = "modelMethod")
        @EnableGeneration
        void unixLike(@InputParameter("OS") String os,
                      @InputParameter("Language") RequirementsModel.Language language,
                      @InputParameter("Browser") String browser) {
            assertThat(os).isIn("Linux", "MacOS");
        }
    }
}
