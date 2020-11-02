package de.rwth.swc.coffee4j.junit.engine.it;

import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.Ipog;
import de.rwth.swc.coffee4j.junit.engine.CombinatorialTestEngine;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.diagnosis.EnableConflictDetection;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintBuilder.constrain;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class ExampleIT {

    @Test
    void executeExample() {
        assertThatCode(() -> EngineTestKit
                .engine(CombinatorialTestEngine.ENGINE_ID)
                .selectors(selectClass(ExampleTestCase.class))
                .execute())
        .doesNotThrowAnyException();
    }

    static class ExampleTestCase {

        @CombinatorialTest
        @EnableGeneration(algorithms = Ipog.class)
        //@ConfigureIpogNeg(constraintCheckerFactory = DiagnosticConstraintCheckerFactory.class)
        //  Enable Constraint Diagnosis but do not skip generation if conflicts were detected
        @EnableConflictDetection(shouldAbort = false, explainConflicts = true, diagnoseConflicts = true)
        void testExample(@InputParameter("Title") String title,
                         @InputParameter("GivenName") String firstName,
                         @InputParameter("FamilyName") String givenName) {
            /* Stimulate the System under Test */
        }

        static InputParameterModel model() {
            return inputParameterModel("example")
                    .parameters(
                            parameter("Title").values("Mr", "Mrs", "123"),
                            parameter("GivenName").values("John", "Jane", "123"),
                            parameter("FamilyName").values("Doe", "Foo", "123")
                    ).errorConstraints(
                            constrain("Title" /*, CORRECT*/).withName("c1") /* Mark Error-Constraint as Correct */
                                    .by((String title) -> !title.equals("123")),
                            constrain("GivenName").withName("c2")
                                    .by((String givenName) -> !givenName.equals("123")),
                            constrain("FamilyName").withName("c3")
                                    .by((String familyName) -> !familyName.equals("123")),
                            constrain("Title", "GivenName").withName("c4")
                                    .by((String title, String givenName) ->
                                            !(title.equals("Mrs") && givenName.equals("John"))
                                                    && !(title.equals("Mrs") && givenName.equals("123"))
                                    ),
                            constrain("Title", "GivenName").withName("c5")
                                    .by((String title, String givenName) ->
                                            !(title.equals("Mr") && givenName.equals("Jane"))
                                                    && !(title.equals("Mr") && givenName.equals("123"))
                                    )
                    )
                    .build();
        }
    }
}

