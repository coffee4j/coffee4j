package io.github.coffee4j.junit.engine.it;

import io.github.coffee4j.algorithmic.Coffee4JException;
import io.github.coffee4j.junit.engine.CombinatorialTestEngine;
import io.github.coffee4j.junit.engine.annotation.CombinatorialTest;
import io.github.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import io.github.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import io.github.coffee4j.engine.configuration.model.InputParameterModel;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineExecutionResults;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.event;
import static org.junit.platform.testkit.engine.EventConditions.finishedWithFailure;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.cause;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.instanceOf;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.message;


/**
 * Tests functional-requirement F5 from the thesis "Development of an Automated Combinatorial Testing Framework"
 */
class TypeSafeParametersIT {

    @Test
    void checksTypeSafety() {
        final EngineTestKit.Builder builder = EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(ArbitraryParameterOrderTestCase.class));
//  Sollte in Zukunft entfernt werden; vorerst für Verständnis noch drin
//        assertThatExceptionOfType(Coffee4JException.class)
//                .isThrownBy(builder::execute)
//                .withMessageContaining("Parameter \"Browser\" not assignable. " +
//                        "Expected type java.lang.Integer but was java.lang.String");
        EngineExecutionResults results = builder.execute();
        results.allEvents().assertThatEvents()
                .haveAtLeastOne(event(finishedWithFailure(
                        cause(instanceOf(Coffee4JException.class)),
                        cause(message(m -> m.contains("Parameter \"Browser\" not assignable. " +
                                "Expected type java.lang.Integer but was java.lang.String")))
                )));
    }


    static class ArbitraryParameterOrderTestCase {

        private InputParameterModel modelMethod() {
            return RequirementsModel.BROWSER_IPM;
        }

        @CombinatorialTest(inputParameterModel = "modelMethod")
        @EnableGeneration
        void unixLike(@InputParameter("Language") RequirementsModel.Language language,
                      @InputParameter("OS") String os,
                      @InputParameter("Browser") Integer browser) {
            assertThat(os).isIn("Linux", "MacOS");
        }
    }
}
