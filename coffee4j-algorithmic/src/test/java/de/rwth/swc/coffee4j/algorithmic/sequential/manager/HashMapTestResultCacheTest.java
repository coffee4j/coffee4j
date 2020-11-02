package de.rwth.swc.coffee4j.algorithmic.sequential.manager;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HashMapTestResultCacheTest {
    
    @Test
    void doesNotHaveTestResultsForNonCachedTests() {
        final HashMapTestResultCache testResultCache = new HashMapTestResultCache();
        assertFalse(testResultCache.containsResultFor(new IntArrayWrapper(new int[]{0})));
        assertFalse(testResultCache.containsResultFor(new IntArrayWrapper(new int[]{1})));
        assertFalse(testResultCache.containsResultFor(null));
    }
    
    @Test
    void containsResultsOfAddedTests() {
        final IntArrayWrapper firstTest = new IntArrayWrapper(new int[]{0});
        final IntArrayWrapper secondTest = new IntArrayWrapper(new int[]{1});
        final HashMapTestResultCache testResultCache = new HashMapTestResultCache();
        assertFalse(testResultCache.containsResultFor(firstTest));
        assertFalse(testResultCache.containsResultFor(secondTest));
        
        testResultCache.addResultFor(firstTest, TestResult.success());
        assertTrue(testResultCache.containsResultFor(firstTest));
        assertFalse(testResultCache.containsResultFor(secondTest));
        Assertions.assertEquals(TestResult.success(), testResultCache.getResultFor(firstTest));
        
        final Exception exception = new IllegalArgumentException();
        testResultCache.addResultFor(secondTest, TestResult.failure(exception));
        assertTrue(testResultCache.containsResultFor(firstTest));
        assertTrue(testResultCache.containsResultFor(secondTest));
        Assertions.assertEquals(TestResult.success(), testResultCache.getResultFor(firstTest));
        Assertions.assertEquals(TestResult.failure(exception), testResultCache.getResultFor(secondTest));
    }
    
}
