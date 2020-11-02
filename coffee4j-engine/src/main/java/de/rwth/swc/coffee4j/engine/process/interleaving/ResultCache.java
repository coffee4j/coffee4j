package de.rwth.swc.coffee4j.engine.process.interleaving;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;

/**
 * Cache for results of executed test inputs to reduce the execution time. As soon as a test input {@link Combination}
 * is executed, the {@link TestResult} is stored in the cache. If the {@link TestResult} for an already executed
 * test input is requested, the {@link TestResult} is loaded from cache instead of re-executing the test input.
 */
public interface ResultCache {
    
    /**
     * @param testInput test input to check whether a result is present for.
     * @return true iff result for test input is present.
     */
    boolean containsResultFor(Combination testInput);

    /**
     * @param testInput test input to load a result for.
     * @return result for the requested test input.
     */
    TestResult getResultFor(Combination testInput);

    /**
     * @param testInput test input to store a result for.
     * @param result result to be stored.
     */
    void addResultIfAbsentFor(Combination testInput, TestResult result);
    
}
