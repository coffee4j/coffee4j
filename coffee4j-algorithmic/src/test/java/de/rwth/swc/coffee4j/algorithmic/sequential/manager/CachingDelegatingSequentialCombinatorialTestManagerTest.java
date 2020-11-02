package de.rwth.swc.coffee4j.algorithmic.sequential.manager;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class CachingDelegatingSequentialCombinatorialTestManagerTest {
    
    private static final int[] FIRST_TEST_INPUT = new int[]{0, 0};
    private static final int[] SECOND_TEST_INPUT = new int[]{0, 1};
    private static final int[] THIRD_TEST_INPUT = new int[]{1, 0};
    private static final int[] FOURTH_TEST_INPUT = new int[]{1, 1};
    private static final int[] FIFTH_TEST_INPUT = new int[]{2, 2};
    
    private static final List<int[]> ALL_TEST_INPUTS = Arrays.asList(FIRST_TEST_INPUT, SECOND_TEST_INPUT, THIRD_TEST_INPUT, FOURTH_TEST_INPUT, FIFTH_TEST_INPUT);
    
    @Test
    void preconditions() {
        assertThrows(NullPointerException.class, () -> new CachingDelegatingSequentialCombinatorialTestManager(null, Mockito.mock(SequentialCombinatorialTestManager.class)));
        assertThrows(NullPointerException.class, () -> new CachingDelegatingSequentialCombinatorialTestManager(Mockito.mock(TestResultCache.class), null));
    }
    
    @Test
    void returnsAllTestInputsOfInitialGenerationIfNoneInCache() {
        final TestResultCache cache = Mockito.mock(TestResultCache.class);
        when(cache.containsResultFor(any())).thenReturn(false);
        final SequentialCombinatorialTestManager generator = Mockito.mock(SequentialCombinatorialTestManager.class);
        when(generator.generateInitialTests()).thenReturn(ALL_TEST_INPUTS);
        final CachingDelegatingSequentialCombinatorialTestManager cachingGenerator = new CachingDelegatingSequentialCombinatorialTestManager(cache, generator);
        
        final List<int[]> calculatedTestInputs = cachingGenerator.generateInitialTests();
        
        Assertions.assertEquals(IntArrayWrapper.wrapToSet(ALL_TEST_INPUTS), IntArrayWrapper.wrapToSet(calculatedTestInputs));
        verify(generator, times(1)).generateInitialTests();
        verifyNoMoreInteractions(generator);
    }
    
    @Test
    void returnsAllTestInputsExceptCachedInInitialGeneration() {
        final Exception exception = new IllegalArgumentException();
        final TestResultCache cache = Mockito.mock(TestResultCache.class);
        when(cache.containsResultFor(any())).thenReturn(false);
        when(cache.containsResultFor(IntArrayWrapper.wrap(SECOND_TEST_INPUT))).thenReturn(true);
        when(cache.containsResultFor(IntArrayWrapper.wrap(THIRD_TEST_INPUT))).thenReturn(true);
        when(cache.containsResultFor(IntArrayWrapper.wrap(FOURTH_TEST_INPUT))).thenReturn(true);
        when(cache.getResultFor(IntArrayWrapper.wrap(SECOND_TEST_INPUT))).thenReturn(TestResult.failure(exception));
        when(cache.getResultFor(IntArrayWrapper.wrap(THIRD_TEST_INPUT))).thenReturn(TestResult.failure(exception));
        when(cache.getResultFor(IntArrayWrapper.wrap(FOURTH_TEST_INPUT))).thenReturn(TestResult.success());
        final SequentialCombinatorialTestManager generator = Mockito.mock(SequentialCombinatorialTestManager.class);
        when(generator.generateInitialTests()).thenReturn(Arrays.asList(FIRST_TEST_INPUT, SECOND_TEST_INPUT, THIRD_TEST_INPUT));
        when(generator.generateAdditionalTestInputsWithResult(aryEq(SECOND_TEST_INPUT), eq(TestResult.failure(exception)))).thenReturn(List.of(FOURTH_TEST_INPUT));
        when(generator.generateAdditionalTestInputsWithResult(aryEq(FOURTH_TEST_INPUT), eq(TestResult.success()))).thenReturn(List.of(FIFTH_TEST_INPUT));
        final CachingDelegatingSequentialCombinatorialTestManager cachingGenerator = new CachingDelegatingSequentialCombinatorialTestManager(cache, generator);
        
        final List<int[]> calculatedTestInputs = cachingGenerator.generateInitialTests();
        
        Assertions.assertEquals(IntArrayWrapper.wrapToSet(Arrays.asList(FIRST_TEST_INPUT, FIFTH_TEST_INPUT)), IntArrayWrapper.wrapToSet(calculatedTestInputs));
        verify(generator, times(1)).generateAdditionalTestInputsWithResult(aryEq(THIRD_TEST_INPUT), eq(TestResult.failure(exception)));
    }
    
    @Test
    void doesNotGiveDuplicates() {
        final TestResultCache cache = Mockito.mock(TestResultCache.class);
        when(cache.containsResultFor(any())).thenReturn(false);
        when(cache.containsResultFor(IntArrayWrapper.wrap(FIRST_TEST_INPUT))).thenReturn(true);
        when(cache.containsResultFor(IntArrayWrapper.wrap(SECOND_TEST_INPUT))).thenReturn(true);
        final SequentialCombinatorialTestManager generator = Mockito.mock(SequentialCombinatorialTestManager.class);
        when(generator.generateInitialTests()).thenReturn(Arrays.asList(FIRST_TEST_INPUT, SECOND_TEST_INPUT));
        when(generator.generateAdditionalTestInputsWithResult(aryEq(FIRST_TEST_INPUT), any())).thenReturn(Arrays.asList(SECOND_TEST_INPUT, THIRD_TEST_INPUT));
        when(generator.generateAdditionalTestInputsWithResult(aryEq(SECOND_TEST_INPUT), any())).thenReturn(Arrays.asList(THIRD_TEST_INPUT, FOURTH_TEST_INPUT, FOURTH_TEST_INPUT));
        final CachingDelegatingSequentialCombinatorialTestManager cachingGenerator = new CachingDelegatingSequentialCombinatorialTestManager(cache, generator);
        
        final List<IntArrayWrapper> calculatedTestInputs = IntArrayWrapper.wrapToList(cachingGenerator.generateInitialTests());
        
        assertEquals(2, calculatedTestInputs.size());
        assertTrue(calculatedTestInputs.contains(IntArrayWrapper.wrap(THIRD_TEST_INPUT)));
        assertTrue(calculatedTestInputs.contains(IntArrayWrapper.wrap(FOURTH_TEST_INPUT)));
    }
    
    @Test
    void returnsOnlyNonCachedTestInputsInAdditional() {
        final TestResultCache cache = Mockito.mock(TestResultCache.class);
        when(cache.containsResultFor(any())).thenReturn(false);
        when(cache.containsResultFor(IntArrayWrapper.wrap(FIRST_TEST_INPUT))).thenReturn(true);
        when(cache.containsResultFor(IntArrayWrapper.wrap(SECOND_TEST_INPUT))).thenReturn(true);
        final SequentialCombinatorialTestManager generator = Mockito.mock(SequentialCombinatorialTestManager.class);
        when(generator.generateAdditionalTestInputsWithResult(aryEq(FIFTH_TEST_INPUT), any())).thenReturn(Arrays.asList(FIRST_TEST_INPUT, SECOND_TEST_INPUT));
        when(generator.generateAdditionalTestInputsWithResult(aryEq(FIRST_TEST_INPUT), any())).thenReturn(Arrays.asList(SECOND_TEST_INPUT, THIRD_TEST_INPUT));
        when(generator.generateAdditionalTestInputsWithResult(aryEq(SECOND_TEST_INPUT), any())).thenReturn(Arrays.asList(THIRD_TEST_INPUT, FOURTH_TEST_INPUT, FOURTH_TEST_INPUT));
        final CachingDelegatingSequentialCombinatorialTestManager cachingGenerator = new CachingDelegatingSequentialCombinatorialTestManager(cache, generator);
        
        final List<IntArrayWrapper> calculatedTestInputs = IntArrayWrapper.wrapToList(cachingGenerator.generateAdditionalTestInputsWithResult(FIFTH_TEST_INPUT, TestResult.failure(new IllegalArgumentException())));
        
        assertEquals(2, calculatedTestInputs.size());
        assertTrue(calculatedTestInputs.contains(IntArrayWrapper.wrap(THIRD_TEST_INPUT)));
        assertTrue(calculatedTestInputs.contains(IntArrayWrapper.wrap(FOURTH_TEST_INPUT)));
    }
    
}
