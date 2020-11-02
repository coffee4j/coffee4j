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
import static org.junit.platform.testkit.engine.EventConditions.displayName;
import static org.junit.platform.testkit.engine.EventConditions.event;
import static org.junit.platform.testkit.engine.EventConditions.finishedSuccessfully;

/**
 * Tests functional-requirement F4 from the thesis "Development of an Automated Combinatorial Testing Framework"
 */
class ArbitraryParameterOrderIT {

    @Test
    void executesWithArbitraryParameterOrder() {
        final EngineTestKit.Builder builder = EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(ArbitraryParameterOrderTestCase.class));
        final EngineExecutionResults executionResults = builder
                .execute();

        final Events tests = executionResults.tests();

        tests.assertThatEvents()
                .haveAtLeastOne(
                        event(
                                displayName("[OS=Linux, Language=en_US, Browser=Edge]"),
                                finishedSuccessfully()
                        )
                );
    }

    static class ArbitraryParameterOrderTestCase {

        private InputParameterModel modelMethod() {
            return RequirementsModel.BROWSER_IPM;
        }

        @CombinatorialTest(name = "[OS={OS}, Language={Language}, Browser={Browser}]", inputParameterModel = "modelMethod")
        @EnableGeneration
        void unixLike(@InputParameter("Language") RequirementsModel.Language language,
                      @InputParameter("OS") String os,
                      @InputParameter("Browser") String browser) {
            assertThat(os).isIn("Linux", "MacOS");
        }
    }
}
