package de.rwth.swc.coffee4j.engine.configuration.model;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.rwth.swc.coffee4j.engine.configuration.model.Seed.seed;
import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.StrengthGroup.mixedStrengthGroup;
import static de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintBuilder.constrain;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.testng.Assert.assertThrows;

class InputParameterModelTest {
    
    @ParameterizedTest
    @MethodSource("preconditionViolations")
    void violatesPreconditions(InputParameterModel.Builder modelBuilder) {
        Assertions.assertThrows(IllegalArgumentException.class, modelBuilder::build);
    }
    
    private static Stream<Arguments> preconditionViolations() {
        return Stream.of(
                Arguments.of(inputParameterModel("")),
                Arguments.of(inputParameterModel("").positiveTestingStrength(2)
                        .parameter(parameter("test").values(1, 2))),
                Arguments.of(inputParameterModel("").positiveTestingStrength(-1)
                        .parameter(parameter("test").values(1, 2))),
                Arguments.of(inputParameterModel("").negativeTestingStrength(-1)
                        .parameter(parameter("test").values(1, 2))),
                Arguments.of(inputParameterModel("")
                        .parameter(parameter("test").values(1, 2))
                        .errorConstraint(constrain("test1").by(value -> true))),
                Arguments.of(inputParameterModel("test")
                        .parameter(parameter("test").values(1, 2))
                        .errorConstraint(constrain("test").withName("someName").by((Integer value) -> true))
                        .seed("otherName", seed(entry("test", 2)))));
    }
    
    @Test
    void builder() {
        final InputParameterModel model = inputParameterModel("name")
                .positiveTestingStrength(1)
                .negativeTestingStrength(2)
                .parameters(
                        parameter("param1").values(1, 2, 3),
                        parameter("param2").values(4, 5, 6))
                .parameter(parameter("param3").values(7, 8, 9).build())
                .errorConstraints(
                        constrain("param1").withName("firstConstraint")
                                .by((Integer value) -> true),
                        constrain("param2").withName("secondConstraint")
                                .by((Integer value) -> true))
                .seeds(
                        seed(entry("param1", 1), entry("param2", 5)),
                        seed(entry("param1", 2), entry("param3", 7)))
                .seeds("secondConstraint",
                        seed(entry("param1", 1), entry("param2", 4)),
                        seed(entry("param2", 4), entry("param3", 9)))
                .mixedStrengthGroups(
                        mixedStrengthGroup("param1", "param3").ofHighestStrength())
                .build();
        
        assertEquals("name", model.getName());
        
        assertEquals(1, model.getPositiveTestingStrength());
        assertEquals(2, model.getNegativeTestingStrength());
        
        assertEquals(3, model.size());
        
        assertEquals(1, model.getParameters().get(0).getValues().get(0).get());
        assertEquals(2, model.getParameters().get(0).getValues().get(1).get());
        assertEquals(3, model.getParameters().get(0).getValues().get(2).get());
        assertEquals(4, model.getParameters().get(1).getValues().get(0).get());
        assertEquals(5, model.getParameters().get(1).getValues().get(1).get());
        assertEquals(6, model.getParameters().get(1).getValues().get(2).get());
        assertEquals(7, model.getParameters().get(2).getValues().get(0).get());
        assertEquals(8, model.getParameters().get(2).getValues().get(1).get());
        assertEquals(9, model.getParameters().get(2).getValues().get(2).get());
        
        assertEquals(2, model.getPositiveSeeds().size());
        assertEquals(Set.of("param1", "param2"), model.getPositiveSeeds().get(0).getCombination()
                .getParameterValueMap().keySet().stream()
                .map(Parameter::getName).collect(Collectors.toSet()));
        assertEquals(Set.of("param1", "param3"), model.getPositiveSeeds().get(1).getCombination()
                .getParameterValueMap().keySet().stream()
                .map(Parameter::getName).collect(Collectors.toSet()));
        assertNull(model.getNegativeSeeds().get("firstConstraint"));
        assertEquals(2, model.getNegativeSeeds().get("secondConstraint").size());
        assertEquals(Set.of("param1", "param2"), model.getNegativeSeeds()
                .get("secondConstraint").get(0).getCombination()
                .getParameterValueMap().keySet().stream().map(Parameter::getName).collect(Collectors.toSet()));
        assertEquals(Set.of("param2", "param3"), model.getNegativeSeeds()
                .get("secondConstraint").get(1).getCombination()
                .getParameterValueMap().keySet().stream().map(Parameter::getName).collect(Collectors.toSet()));
        
        assertEquals(1, model.getPositiveMixedStrengthGroups().size());
        assertEquals(Set.of("param1", "param3"), model.getPositiveMixedStrengthGroups().get(0).getParameters().stream()
                .map(Parameter::getName)
                .collect(Collectors.toSet()));
        assertEquals(2, model.getPositiveMixedStrengthGroups().get(0).getStrength());
    }
    
    @Test
    void sameParameterNameCannotAppearTwice() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> inputParameterModel("test").positiveTestingStrength(2).parameters(
                parameter("param1").values(0, 1), parameter("param1").values(0, 1)).build());
    }
    
    @Test
    void cannotReferenceNonExistingParameterInMixedStrengthGrou() {
        assertThrows(Coffee4JException.class, () -> inputParameterModel("test")
                .parameter(parameter("test").values(1, 2))
                .mixedStrengthGroup(mixedStrengthGroup("test1").ofHighestStrength()));
    }
    
}
