package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm;

import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NegativeStrengthBasedParameterOrderTest {

    @Test
    void testInitialParametersForSingleNegativeParameter() {
        ParameterOrder parameterOrder = negativityAwareParameterOrder(1, new int[]{0});
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 2, 2, 2});
        int[] initialParameters = parameterOrder.getInitialParameters(parameters, 0);

        assertEquals(1, initialParameters.length);
        assertArrayEquals(new int[]{0}, initialParameters);
    }

    @Test
    void testInitialParametersForTwoNegativeParameters() {
        ParameterOrder parameterOrder = negativityAwareParameterOrder(1, new int[]{1, 2});
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 2, 2, 2});
        int[] initialParameters = parameterOrder.getInitialParameters(parameters, 0);

        assertEquals(2, initialParameters.length);
        assertArrayEquals(new int[]{1, 2}, initialParameters);
    }

    @Test
    void testInitialParametersForAllNegativeParameters() {
        ParameterOrder parameterOrder = negativityAwareParameterOrder(1, new int[]{0, 1, 2, 3});
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 2, 2, 2});
        int[] initialParameters = parameterOrder.getInitialParameters(parameters, 0);

        assertEquals(4, initialParameters.length);
        assertArrayEquals(new int[]{0, 1, 2, 3}, initialParameters);
    }

    @Test
    void testRemainingParametersForSingleNegativeParameter() {
        ParameterOrder parameterOrder = negativityAwareParameterOrder(1, new int[]{0});
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 2, 2, 2});
        int[] remainingParameters = parameterOrder.getRemainingParameters(parameters, 0);

        assertEquals(3, remainingParameters.length);
        assertArrayEquals(new int[]{1, 2, 3}, remainingParameters);
    }

    @Test
    void testRemainingParametersForTwoNegativeParameter() {
        ParameterOrder parameterOrder = negativityAwareParameterOrder(1, new int[]{1, 3});
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 2, 2, 2});
        int[] remainingParameters = parameterOrder.getRemainingParameters(parameters, 0);

        assertEquals(2, remainingParameters.length);
        assertArrayEquals(new int[]{0, 2}, remainingParameters);
    }

    @Test
    void testRemainingParametersForAllNegativeParameter() {
        ParameterOrder parameterOrder = negativityAwareParameterOrder(1, new int[]{0, 1, 2, 3});
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 2, 2, 2});
        int[] remainingParameters = parameterOrder.getRemainingParameters(parameters, 0);

        assertEquals(0, remainingParameters.length);
        assertArrayEquals(new int[]{}, remainingParameters);
    }

    @Test
    void testInitialParametersForDecreasingValueDomains() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 3, 4, 5});
        ParameterOrder parameterOrder = negativityAwareParameterOrder(1, new int[]{0, 1});
        int strength = 2;

        int[] initialParameters = parameterOrder.getInitialParameters(parameters, strength);

        assertArrayEquals(new int[] {1, 0}, initialParameters);
    }

    @Test
    void testRemainingParametersForDecreasingValueDomains() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 3, 4, 5});
        ParameterOrder parameterOrder = negativityAwareParameterOrder(1, new int[]{0, 1});
        int strength = 2;

        int[] remainingParameters = parameterOrder.getRemainingParameters(parameters, strength);

        assertArrayEquals(new int[] {3, 2}, remainingParameters);
    }

    @Test
    void testInitialParametersForDecreasingValueDomainsAndLowerStrength() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 3, 4, 5});
        ParameterOrder parameterOrder = negativityAwareParameterOrder(1, new int[]{0, 1}, 1);
        int strength = 2;

        int[] initialParameters = parameterOrder.getInitialParameters(parameters, strength);

        assertArrayEquals(new int[] {1}, initialParameters);
    }

    @Test
    void testRemainingParametersForDecreasingValueDomainsAndLowerStrength() {
        Int2IntMap parameters = new Int2IntArrayMap(new int[]{0, 1, 2, 3}, new int[]{2, 3, 4, 5});
        ParameterOrder parameterOrder = negativityAwareParameterOrder(1, new int[]{0, 1}, 1);
        int strength = 2;

        int[] remainingParameters = parameterOrder.getRemainingParameters(parameters, strength);

        assertArrayEquals(new int[] {0, 3, 2}, remainingParameters);
    }

    private ParameterOrder negativityAwareParameterOrder(int id, int[] negativeParameters) {
        return negativityAwareParameterOrder(id, negativeParameters, negativeParameters.length);
    }

    private ParameterOrder negativityAwareParameterOrder(int id, int[] negativeParameters, int strengthA) {
        final TupleList forbiddenTuples = new TupleList(id, negativeParameters, Collections.singletonList(negativeParameters));

        return new NegativeStrengthBasedParameterOrder(forbiddenTuples, strengthA);
    }
}

