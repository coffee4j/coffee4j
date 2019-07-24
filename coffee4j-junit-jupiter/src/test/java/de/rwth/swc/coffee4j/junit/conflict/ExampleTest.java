package de.rwth.swc.coffee4j.junit.conflict;

import de.rwth.swc.coffee4j.engine.generator.ipog.Ipog;
import de.rwth.swc.coffee4j.engine.generator.ipogneg.IpogNeg;
import de.rwth.swc.coffee4j.junit.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.provider.configuration.diagnosis.EnableConflictDetection;
import de.rwth.swc.coffee4j.junit.provider.configuration.generator.Generator;
import de.rwth.swc.coffee4j.junit.provider.model.ModelFromMethod;
import de.rwth.swc.coffee4j.model.InputParameterModel;

import static de.rwth.swc.coffee4j.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.model.constraints.ConstraintBuilder.constrain;
import static de.rwth.swc.coffee4j.model.constraints.ConstraintStatus.CORRECT;

class ExampleTest {

    @CombinatorialTest
    @ModelFromMethod("model")
    @Generator({Ipog.class, IpogNeg.class})
    /* Enable Constraint Diagnosis but do not skip generation if conflicts were detected */
    @EnableConflictDetection(shouldAbort = false, explainConflicts = true, diagnoseConflicts = true)
    void testExample(String title, String firstName, String givenName) {
        /* Stimulate the System under Test */
    }

    static InputParameterModel model() {
        return inputParameterModel("example")
                .parameters(
                        parameter("Title").values("Mr", "Mrs", "123"),
                        parameter("GivenName").values("John", "Jane", "123"),
                        parameter("FamilyName").values("Doe", "Foo", "123")
                ).errorConstraints(
                        constrain("Title", CORRECT).withName("c1") /* Mark Error-Constraint as Correct */
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
