package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StrengthBasedParameterOrderTest {

    private static final ParameterOrder PARAMETER_ORDER = new StrengthBasedParameterOrder();

    @Test
    void testInitialParametersForSmallerStrength() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 2, 2, 2});
        int strength = 2;
        int[] initialParameters = PARAMETER_ORDER.getInitialParameters(parameters, strength);

        assertEquals(2, initialParameters.length);
        assertArrayEquals(new int[]{0, 1}, initialParameters);
    }

    @Test
    void testInitialParametersForEqualStrength() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 2, 2, 2});
        int strength = 4;
        int[] initialParameters = PARAMETER_ORDER.getInitialParameters(parameters, strength);

        assertEquals(4, initialParameters.length);
        assertArrayEquals(new int[]{0, 1, 2, 3}, initialParameters);
    }

    @Test
    void testInitialParametersForHigherStrength() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 2, 2, 2});
        int strength = 5;
        int[] initialParameters = PARAMETER_ORDER.getInitialParameters(parameters, strength);

        assertEquals(4, initialParameters.length);
        assertArrayEquals(new int[]{0, 1, 2, 3}, initialParameters);
    }

    @Test
    void testRemainingParametersForSmallerStrength() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 2, 2, 2});
        int strength = 2;
        int[] remainingParameters = PARAMETER_ORDER.getRemainingParameters(parameters, strength);
        assertEquals(2, remainingParameters.length);
        assertArrayEquals(new int[]{2, 3}, remainingParameters);
    }

    @Test
    void testRemainingParametersForEqualStrength() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 2, 2, 2});
        int strength = 4;
        int[] remainingParameters = PARAMETER_ORDER.getRemainingParameters(parameters, strength);

        assertEquals(0, remainingParameters.length);
        assertArrayEquals(new int[]{}, remainingParameters);
    }

    @Test
    void testRemainingParametersForHigherStrength() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 2, 2, 2});
        int strength = 5;
        int[] remainingParameters = PARAMETER_ORDER.getRemainingParameters(parameters, strength);

        assertEquals(0, remainingParameters.length);
        assertArrayEquals(new int[]{}, remainingParameters);
    }

    @Test
    void testInitialParametersForIncreasingValueDomains() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 3, 4, 5});
        int strength = 2;

        int[] initialParameters = PARAMETER_ORDER.getInitialParameters(parameters, strength);

        assertArrayEquals(new int[] {3, 2}, initialParameters);
    }

    @Test
    void testRemainingParametersForIncreasingValueDomains() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 3, 4, 5});
        int strength = 2;

        int[] remainingParameters = PARAMETER_ORDER.getRemainingParameters(parameters, strength);

        assertArrayEquals(new int[] {1, 0}, remainingParameters);
    }

    @Test
    void testInitialParametersForDecreasingValueDomains() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{5, 4, 3, 2});
        int strength = 2;

        int[] initialParameters = PARAMETER_ORDER.getInitialParameters(parameters, strength);

        assertArrayEquals(new int[] {0, 1}, initialParameters);
    }

    @Test
    void testRemainingParametersForDecreasingValueDomains() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{5, 4, 3, 2});
        int strength = 2;

        int[] remainingParameters = PARAMETER_ORDER.getRemainingParameters(parameters, strength);

        assertArrayEquals(new int[] {2, 3}, remainingParameters);
    }
}

