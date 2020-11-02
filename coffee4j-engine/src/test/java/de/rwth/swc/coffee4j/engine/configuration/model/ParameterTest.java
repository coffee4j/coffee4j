package de.rwth.swc.coffee4j.engine.configuration.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.OptionalDouble;

import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.booleanParameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.enumParameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.Value.weighted;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParameterTest {
    
    @Test
    void argumentsMayNotBeNull() {
        Assertions.assertThrows(NullPointerException.class, () -> new Parameter(null, Arrays.asList(Value.value(0, 1), Value.value(1, 2))));
        Assertions.assertThrows(NullPointerException.class, () -> new Parameter("test", null));
    }
    
    @Test
    void atLeastTwoValuesRequired() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Parameter("a", Collections.emptyList()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Parameter("a", Collections.singletonList(Value.value(0, 1))));
        final Parameter twoValueParameter = new Parameter("a", Arrays.asList(Value.value(0, 1), Value.value(1, 2)));
        assertEquals(2, twoValueParameter.size());
    }
    
    @Test
    void cannotContainSameValueIdTwice() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Parameter("a", Arrays.asList(Value.value(0, 1), Value.value(0, 2))));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Parameter("a", Arrays.asList(Value.value(0, 1), Value.value(1, 2), Value.value(1, 2))));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Parameter("a", Arrays.asList(Value.value(3, 1), Value.value(2, 2), Value.value(1, 3), Value.value(2, 2), Value.value(4, 4))));
    }
    
    @Test
    void valueCannotBeNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Parameter("a", Arrays.asList(null, null)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Parameter("a", Arrays.asList(Value.value(0, 1), null, Value.value(2, 2))));
    }
    
    @Test
    void builderTest() {
        final Parameter parameter = parameter("a").value(1).values(2, 3, 4).value(6).values(8, 9).build();
        assertEquals("a", parameter.getName());
        assertEquals(7, parameter.size());
        assertEquals(Value.value(0, 1), parameter.getValues().get(0));
        assertEquals(Value.value(1, 2), parameter.getValues().get(1));
        assertEquals(Value.value(2, 3), parameter.getValues().get(2));
        assertEquals(Value.value(3, 4), parameter.getValues().get(3));
        assertEquals(Value.value(4, 6), parameter.getValues().get(4));
        assertEquals(Value.value(5, 8), parameter.getValues().get(5));
        assertEquals(Value.value(6, 9), parameter.getValues().get(6));
    }
    
    @Test
    void testBooleanParameterBuilder() {
        final Parameter parameter = booleanParameter("name").build();
        
        assertEquals("name", parameter.getName());
        assertEquals(true, parameter.getValues().get(0).get());
        assertEquals(false, parameter.getValues().get(1).get());
    }
    
    @Test
    void testEnumParameterBuilder() {
        final Parameter parameter = enumParameter("name", SomeEnum.class).build();
        
        assertEquals("name", parameter.getName());
        assertEquals(SomeEnum.FIRST_VALUE, parameter.getValues().get(0).get());
        assertEquals(SomeEnum.SECOND_VALUE, parameter.getValues().get(1).get());
        assertEquals(SomeEnum.THIRD_VALUE, parameter.getValues().get(2).get());
    }
    
    private enum SomeEnum {
        
        FIRST_VALUE,
        SECOND_VALUE,
        THIRD_VALUE
        
    }
    
    @Test
    void handlesValueBuilderInParameterBuilder() {
        final Parameter parameter = parameter("someName")
                .value(weighted("first", 2.0))
                .value("second")
                .values("third", weighted("fourth", 3.0), "fifth")
                .build();
        
        assertEquals(5, parameter.size());
        assertEquals("first", parameter.getValues().get(0).get());
        assertEquals(OptionalDouble.of(2.0), parameter.getValues().get(0).getWeight());
        assertEquals("second", parameter.getValues().get(1).get());
        assertEquals(OptionalDouble.empty(), parameter.getValues().get(1).getWeight());
        assertEquals("third", parameter.getValues().get(2).get());
        assertEquals(OptionalDouble.empty(), parameter.getValues().get(2).getWeight());
        assertEquals("fourth", parameter.getValues().get(3).get());
        assertEquals(OptionalDouble.of(3.0), parameter.getValues().get(3).getWeight());
        assertEquals("fifth", parameter.getValues().get(4).get());
        assertEquals(OptionalDouble.empty(), parameter.getValues().get(4).getWeight());
    }
    
}
