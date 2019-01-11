package de.rwth.swc.coffee4j.engine.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ArrayUtilTest {
    
    @ParameterizedTest
    @MethodSource
    void exclusion(int[] elements, int[] excluded, int[] expectedResult) {
        assertArrayEquals(expectedResult, ArrayUtil.exclude(elements, excluded));
    }
    
    private static Stream<Arguments> exclusion() {
        return Stream.of(arguments(new int[0], new int[0], new int[0]), arguments(new int[0], new int[]{0}, new int[0]), arguments(new int[]{0, 1}, new int[0], new int[]{0, 1}), arguments(new int[]{0}, new int[]{0}, new int[0]), arguments(new int[]{0, 1}, new int[]{1}, new int[]{0}), arguments(new int[]{0, 1, 2, 3, 2, 2, 4, 1}, new int[]{0, 0, 2}, new int[]{1, 3, 4, 1})
        
        );
    }
    
    @ParameterizedTest
    @MethodSource
    void contains(int[] elements, int element, boolean shouldContain) {
        assertEquals(shouldContain, ArrayUtil.contains(elements, element));
    }
    
    private static Stream<Arguments> contains() {
        return Stream.of(arguments(new int[0], 0, false), arguments(new int[]{0}, 1, false), arguments(new int[]{0}, 0, true), arguments(new int[]{1, 2, 4, 4, 3, 2}, 4, true));
    }
    
}
