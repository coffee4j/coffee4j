package de.rwth.swc.coffee4j.engine.converter.model;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveStrengthGroup;
import de.rwth.swc.coffee4j.algorithmic.model.SeedMode;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.Constraint;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil.NO_VALUE;
import static de.rwth.swc.coffee4j.engine.configuration.model.Seed.seed;
import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.Seed.suspiciousSeed;
import static de.rwth.swc.coffee4j.engine.configuration.model.StrengthGroup.mixedStrengthGroup;
import static de.rwth.swc.coffee4j.engine.configuration.model.Value.weighted;
import static de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintBuilder.constrain;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexBasedModelConverterTest {
    
    @ParameterizedTest
    @MethodSource
    void parameterModelAndConstraintsConversion(InputParameterModel.Builder modelBuilder) {
        final InputParameterModel model = modelBuilder.build();
        final IndexBasedModelConverter converter = new IndexBasedModelConverter(model);
        
        assertEquals(model, converter.getModel());
        verifyAllParameterConversions(model, converter);
        verifyAllConstraintsConvertedCorrectly(model, converter);
        verifyCombinatorialTestModel(model, converter.getConvertedModel(), converter);
    }
    
    @SuppressWarnings("unused")
    private static Stream<Arguments> parameterModelAndConstraintsConversion() {
        return Stream.of(
                Arguments.arguments(inputParameterModel("name")
                        .positiveTestingStrength(1)
                        .parameters(
                                parameter("param").values(0, 1))),
                Arguments.arguments(inputParameterModel("name")
                        .positiveTestingStrength(2)
                        .parameters(
                                parameter("param1").values(0, 1),
                                parameter("param2").values("one", "two", "three"),
                                parameter("param3").values(1.1, 2.2, 3.3, 4.4))),
                Arguments.arguments(inputParameterModel("name")
                        .positiveTestingStrength(1)
                        .parameter(parameter("param").values(0, 1, 2))
                        .errorConstraint(
                                constrain("param").by((Integer param) -> param != 1))
                        .exclusionConstraint(
                                constrain("param").by((Integer param) -> param != 0))),
                Arguments.arguments(inputParameterModel("name")
                        .positiveTestingStrength(1)
                        .parameters(
                                parameter("param1").values(0, 1),
                                parameter("param2").values(0, 1, 2),
                                parameter("param3").values(0, 2, 3))
                        .errorConstraint(
                                constrain("param2", "param3").by((Integer param2, Integer param3) -> !param2.equals(param3)))
                        .exclusionConstraint(
                                constrain("param1", "param3").by((Integer param1, Integer param2) -> !param1.equals(param2)))),
                Arguments.arguments(inputParameterModel("name")
                        .positiveTestingStrength(1)
                        .parameters(
                                parameter("first").values(1, 2, 3),
                                parameter("second").values(4, 5, 6),
                                parameter("third").values("seven", "eight", "nine"))
                        .seeds(
                                seed(entry("first", 2), entry("third", "eight")),
                                seed(entry("second", 6)))));
    }
    
    private void verifyAllParameterConversions(InputParameterModel model, ModelConverter converter) {
        for (int parameterId = 0; parameterId < model.size(); parameterId++) {
            final Parameter parameter = model.getParameters().get(parameterId);
            assertEquals(parameterId, converter.convertParameter(parameter));
            assertEquals(parameter, converter.convertParameter(parameterId));
            
            for (int valueId = 0; valueId < parameter.size(); valueId++) {
                final Value value = parameter.getValues().get(valueId);
                assertEquals(valueId, converter.convertValue(parameter, value));
                assertEquals(value, converter.convertValue(parameterId, valueId));
            }
        }
    }
    
    private void verifyAllConstraintsConvertedCorrectly(InputParameterModel model, ModelConverter converter) {
        final List<Constraint> allConstraints = new ArrayList<>(model.getExclusionConstraints());
        allConstraints.addAll(model.getErrorConstraints());
        
        for (Constraint constraint : allConstraints) {
            final TupleList convertedConstraint = converter.convertConstraint(constraint);
            assertEquals(constraint, converter.convertConstraint(convertedConstraint));
            assertEquals(constraint.getParameterNames().size(), convertedConstraint.getInvolvedParameters().length);
            
            for (int i = 0; i < convertedConstraint.getInvolvedParameters().length; i++) {
                final Parameter involvedParameter = converter.convertParameter(convertedConstraint.getInvolvedParameters()[i]);
                assertTrue(constraint.getParameterNames().contains(involvedParameter.getName()));
            }
        }
    }
    
    private void verifyCombinatorialTestModel(InputParameterModel model, CompleteTestModel convertedModel,
            ModelConverter converter) {
        assertEquals(model.getPositiveTestingStrength(), convertedModel.getPositiveTestingStrength());
        assertEquals(model.size(), convertedModel.getNumberOfParameters());
        assertEquals(model.size(), convertedModel.getParameterSizes().length);
        assertEquals(model.getPositiveSeeds().size(), convertedModel.getSeeds().size());
        
        for (int parameterId = 0; parameterId < model.size(); parameterId++) {
            final Parameter parameter = model.getParameters().get(parameterId);
            assertEquals(parameter.size(), convertedModel.getParameterSize(parameterId));
            assertEquals(parameter.size(), convertedModel.getParameterSizes()[parameterId]);
        }
        
        for (int i = 0; i < model.getPositiveSeeds().size(); i++) {
            final Combination seed = model.getPositiveSeeds().get(i).getCombination();
            final int[] convertedSeed = convertedModel.getSeeds().get(i).getCombination();
            
            assertArrayEquals(convertedSeed, converter.convertCombination(seed));
            assertEquals(seed, converter.convertCombination(convertedSeed));
        }
    }
    
    @Test
    void combinationConversion() {
        final InputParameterModel model = inputParameterModel("name").positiveTestingStrength(2).parameters(
                parameter("param1").values(0, 1), parameter("param2").values("one", "two", "three"), parameter("param3").values(1.1, 2.2, 3.3, 4.4)).build();
        final ModelConverter converter = new IndexBasedModelConverter(model);
        
        int[] combination = new int[]{NO_VALUE, NO_VALUE, NO_VALUE};
        Combination convertedCombination = converter.convertCombination(combination);
        assertEquals(0, convertedCombination.size());
        assertEquals(Collections.<Parameter, Value>emptyMap(), convertedCombination.getParameterValueMap());
        assertArrayEquals(combination, converter.convertCombination(convertedCombination));
        
        combination = new int[]{NO_VALUE, 1, NO_VALUE};
        convertedCombination = converter.convertCombination(combination);
        assertEquals(1, convertedCombination.size());
        assertEquals(Value.value(1, "two"), convertedCombination.getValue(model.getParameters().get(1)));
        assertArrayEquals(combination, converter.convertCombination(convertedCombination));
    }
    
    @Test
    void shouldNotAllowEmptyValidValueDomainForConstraint() {
        final InputParameterModel model = inputParameterModel("name")
                .positiveTestingStrength(1)
                .parameter(parameter("param1").values(0, 1))
                .errorConstraint(constrain("param1").by(value -> false))
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> new IndexBasedModelConverter(model));
    }
    
    @Test
    void shouldNotAllowEmptyInvalidValueDomainForConstraint() {
        final InputParameterModel model = inputParameterModel("name")
                .positiveTestingStrength(1)
                .parameter(parameter("param1").values(0, 1))
                .errorConstraint(constrain("param1").by(value -> true))
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> new IndexBasedModelConverter(model));
    }
    
    @Test
    void shouldConvertSeedsCorrectly() {
        final InputParameterModel inputParameterModel = inputParameterModel("name")
                .parameters(
                        parameter("first").values(0, 1),
                        parameter("second").values(0, 1))
                .errorConstraints(
                        constrain("first").withName("firstConstraint")
                                .by((Integer value) -> value != 1),
                        constrain("second").withName("secondConstraint")
                                .by((Integer value) -> value != 0))
                .seeds(seed(entry("first", 0)))
                .seeds("secondConstraint",
                        suspiciousSeed(entry("first", 0), entry("second", 0)).priority(2))
                .build();
        final ModelConverter modelConverter = new IndexBasedModelConverter(inputParameterModel);
        final CompleteTestModel convertedModel = modelConverter.getConvertedModel();
        
        assertEquals(1, convertedModel.getSeeds(-1).size());
        assertEquals(0, convertedModel.getSeeds(1).size());
        assertEquals(1, convertedModel.getSeeds(2).size());
        assertArrayEquals(new int[] {0, -1}, convertedModel.getSeeds(-1).get(0).getCombination());
        assertEquals(SeedMode.NON_EXCLUSIVE, convertedModel.getSeeds(-1).get(0).getMode());
        assertEquals(0, convertedModel.getSeeds(-1).get(0).getPriority());
        assertArrayEquals(new int[] {0, 0}, convertedModel.getSeeds(2).get(0).getCombination());
        assertEquals(SeedMode.EXCLUSIVE, convertedModel.getSeeds(2).get(0).getMode());
        assertEquals(2, convertedModel.getSeeds(2).get(0).getPriority());
    }
    
    @Test
    void shouldConvertMixedStrengthsCorrectly() {
        final InputParameterModel model = inputParameterModel("name")
                .positiveTestingStrength(1)
                .parameters(
                        parameter("first").values(0, 1),
                        parameter("second").values(2, 3),
                        parameter("third").values(4, 5),
                        parameter("fourth").values(6, 7),
                        parameter("fifth").values(8, 9))
                .mixedStrengthGroups(
                        mixedStrengthGroup("first", "second").ofHighestStrength(),
                        mixedStrengthGroup("first", "third", "fourth", "fifth").ofStrength(3))
                .build();
    
        final ModelConverter modelConverter = new IndexBasedModelConverter(model);
        final CompleteTestModel convertedModel = modelConverter.getConvertedModel();
    
        assertEquals(1, convertedModel.getMixedStrengthGroupsMap().size());
        assertEquals(2, convertedModel.getMixedStrengthGroups().size());
        assertEquals(PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {0, 1})),
                convertedModel.getMixedStrengthGroups().get(0));
        assertEquals(PrimitiveStrengthGroup.ofStrength(new IntOpenHashSet(new int[] {0, 2, 3, 4}), 3),
                convertedModel.getMixedStrengthGroups().get(1));
    }
    
    @Test
    void shouldCreateMixedStrengthGroupsForErrorConstraints() {
        final InputParameterModel model = inputParameterModel("name")
                .parameters(
                        parameter("first").values(0, 1),
                        parameter("second").values(2, 3),
                        parameter("third").values(4, 5))
                .mixedStrengthGroup(mixedStrengthGroup("first", "second").ofHighestStrength())
                .errorConstraints(
                        constrain("first", "second").by((Integer first, Integer second) -> first != 0),
                        constrain("first", "third").by((Integer first, Integer third) -> first != 0))
                .build();
    
        final ModelConverter modelConverter = new IndexBasedModelConverter(model);
        final CompleteTestModel convertedModel = modelConverter.getConvertedModel();
        
        assertEquals(3, convertedModel.getMixedStrengthGroupsMap().size());
        assertEquals(1, convertedModel.getMixedStrengthGroups().size());
        assertEquals(1, convertedModel.getMixedStrengthGroups(-1).size());
        assertEquals(PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {0, 1})),
                convertedModel.getMixedStrengthGroups(-1).get(0));
        assertEquals(1, convertedModel.getMixedStrengthGroups(1).size());
        assertEquals(PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {0, 1})),
                convertedModel.getMixedStrengthGroups(1).get(0));
        assertEquals(1, convertedModel.getMixedStrengthGroups(2).size());
        assertEquals(PrimitiveStrengthGroup.ofHighestStrength(new IntOpenHashSet(new int[] {0, 2})),
                convertedModel.getMixedStrengthGroups(2).get(0));
    }
    
    @Test
    void shouldConvertWeights() {
        final InputParameterModel model = inputParameterModel("name")
                .parameters(
                        parameter("first").values(0, weighted(1, 2.0), weighted(2, 3.0)),
                        parameter("second").values(weighted(0, 1.0), 1),
                        parameter("third").values(0, 1))
                .build();
        
        final ModelConverter modelConverter = new IndexBasedModelConverter(model);
        final CompleteTestModel convertedModel = modelConverter.getConvertedModel();
        
        assertEquals(-1, convertedModel.getWeight(0, 0, -1));
        assertEquals(2.0, convertedModel.getWeight(0, 1, -1));
        assertEquals(3.0, convertedModel.getWeight(0, 2, -1));
        assertEquals(1.0, convertedModel.getWeight(1, 0, -1));
        assertEquals(-1, convertedModel.getWeight(1, 1, -1));
        assertEquals(-1, convertedModel.getWeight(2, 0, -1));
        assertEquals(-1, convertedModel.getWeight(2, 1, -1));
    }
    
}
