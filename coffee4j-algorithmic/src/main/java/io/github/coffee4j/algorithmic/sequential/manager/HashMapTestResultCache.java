package io.github.coffee4j.algorithmic.sequential.manager;

import io.github.coffee4j.algorithmic.model.TestResult;
import io.github.coffee4j.algorithmic.util.IntArrayWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores test results is a hash map.
 */
public class HashMapTestResultCache implements TestResultCache {
    
    private final Map<IntArrayWrapper, TestResult> testResults = new HashMap<>();
    
    @Override
    public boolean containsResultFor(IntArrayWrapper testInput) {
        return testResults.containsKey(testInput);
    }
    
    @Override
    public TestResult getResultFor(IntArrayWrapper testInput) {
        return testResults.get(testInput);
    }
    
    @Override
    public void addResultFor(IntArrayWrapper testInput, TestResult result) {
        testResults.put(testInput, result);
    }
    
}
