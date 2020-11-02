package de.rwth.swc.coffee4j.engine.configuration.model;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static de.rwth.swc.coffee4j.engine.configuration.model.Combination.combination;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CombinationTest {
    
    @ParameterizedTest
    @MethodSource
    void preconditions(Map<Parameter, Value> parameterValueMap, Class<? extends Exception> expectedException) {
        assertThrows(expectedException, () -> Combination.of(parameterValueMap));
    }
    
    @SuppressWarnings("unused")
    private static Stream<Arguments> preconditions() {
        return Stream.of(
                arguments(null, NullPointerException.class),
                arguments(nullKeyMap(), NullPointerException.class),
                arguments(nullValueMap(), NullPointerException.class),
                arguments(wrongValueMap(), IllegalArgumentException.class));
    }
    
    private static Map<Parameter, Value> nullKeyMap() {
        final Map<Parameter, Value> map = new HashMap<>();
        map.put(null, Value.value(1, 2));
        return map;
    }
    
    private static Map<Parameter, Value> nullValueMap() {
        final Map<Parameter, Value> map = new HashMap<>();
        map.put(parameter("test").values(1, 2).build(), null);
        return map;
    }
    
    private static Map<Parameter, Value> wrongValueMap() {
        final Map<Parameter, Value> map = new HashMap<>();
        map.put(parameter("test").values(1, 2).build(), Value.value(3, 3));
        return map;
    }
    
    @Test
    void valueAccessMethods() {
        final Parameter firstParameter = parameter("param1").values(0, 1).build();
        final Parameter secondParameter = parameter("param2").values("one", "two", "three").build();
        final Parameter thirdParameter = parameter("param3").values(1.1, 2.2, 3.3, 4.4).build();
        final Parameter fourthParameter = parameter("param4").values(1, 2).build();
        final Combination combination = Combination.of(Map.of(firstParameter, Value.value(0, 0),
                secondParameter, Value.value(0, "one"), thirdParameter, Value.value(3, 4.4)));
        
        assertEquals(3, combination.size());
        
        assertEquals(Value.value(0, 0), combination.getValue(firstParameter));
        assertEquals(Value.value(0, "one"), combination.getValue(secondParameter));
        assertEquals(Value.value(3, 4.4), combination.getValue(thirdParameter));
        assertNull(combination.getValue(fourthParameter));
        
        assertEquals(Value.value(0, 0), combination.getValue("param1"));
        assertEquals(Value.value(0, "one"), combination.getValue("param2"));
        assertEquals(Value.value(3, 4.4), combination.getValue("param3"));
        assertNull(combination.getValue("param4"));
        
        assertEquals(0, combination.getRawValue(firstParameter));
        assertEquals("one", combination.getRawValue(secondParameter));
        assertEquals(4.4, combination.getRawValue(thirdParameter));
        assertThrows(IllegalArgumentException.class, () -> combination.getRawValue(fourthParameter));
        
        assertEquals(0, combination.getRawValue("param1"));
        assertEquals("one", combination.getRawValue("param2"));
        assertEquals(4.4, combination.getRawValue("param3"));
        assertThrows(IllegalArgumentException.class, () -> combination.getRawValue("param4"));
    }

    @ParameterizedTest
    @MethodSource
    void containsTests(Combination first, Combination second, boolean shouldContain) {
        assertEquals(shouldContain, first.contains(second));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> containsTests() {
        final Parameter a = parameter("a").values(1, 2, 3).build();
        final Parameter b = parameter("b").values(1, 2, 3).build();
        final Parameter c = parameter("c").values(1, 2, 3).build();
        final Parameter d = parameter("d").values(1, 2, 3).build();

        return Stream.of(
                arguments(createCombination(new Parameter[] {a, b, c, d}, new int[] {0, 0, 0, 0}),
                          createCombination(new Parameter[] {a, b, c, d}, new int[] {0, 0, 0, 0}),
                          true),
                arguments(createCombination(new Parameter[] {a, b, c, d}, new int[] {0, 0, 0, 0}),
                          createCombination(new Parameter[] {a, b}, new int[] {0, 0}),
                          true),
                arguments(createCombination(new Parameter[] {a, b}, new int[] {0, 0}),
                          createCombination(new Parameter[] {a, b, c, d}, new int[] {0, 0, 0, 0}),
                          false),
                arguments(createCombination(new Parameter[] {a, b, c}, new int[] {0, 0, 0}),
                          createCombination(new Parameter[] {a, b, d}, new int[] {0, 0, 0}),
                          false),
                arguments(createCombination(new Parameter[] {a, b, c, d}, new int[] {0, 0, 0, 0}),
                          createCombination(new Parameter[] {a, b, c, d}, new int[] {1, 0, 0, 0}),
                          false)
        );
    }

    private static Combination createCombination(Parameter[] parameters, int[] valueIndices) {
        Preconditions.check(parameters.length == valueIndices.length);

        final Map<Parameter, Value> map = new HashMap<>();

        for(int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final int valueIndex = valueIndices[i];
            final Value value = parameter.getValues().get(valueIndex);

            map.put(parameter, value);
        }

        return Combination.of(map);
    }
    
    @Test
    void builderFailsIfParameterNotGiven() {
        final List<Parameter> parameters = List.of(parameter("test").values(1, 2).build());
        
        assertThrows(IllegalArgumentException.class, () -> combination(entry("test2", 1)).build(parameters));
    }
    
    @Test
    void builderFailsIfValueNotGiven() {
        final List<Parameter> parameters = List.of(parameter("test").values(1, 2).build());
    
        assertThrows(IllegalArgumentException.class, () -> combination(entry("test", 3)).build(parameters));
    }
    
    @Test
    void builderConstructsCombination() {
        final Parameter first = parameter("first").values(1, 2, 3).build();
        final Parameter second = parameter("second").values("1", "2", "3").build();
        final Parameter third = parameter("third").values(1.1, 2.2, 3.3).build();
        final List<Parameter> parameters = List.of(first, second, third);
        final Combination combination = combination(entry("first", 2), entry("third", 3.3))
                .build(parameters);
        
        assertNotNull(combination);
        assertEquals(2, combination.size());
        assertEquals(Set.of(first, third), combination.getParameterValueMap().keySet());
        assertEquals(2, combination.getRawValue(first));
        assertEquals(3.3, combination.getRawValue(third));
    }
    
}
