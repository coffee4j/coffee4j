package de.rwth.swc.coffee4j.engine.process.interleaving;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;

import java.util.HashMap;
import java.util.Map;

/**
 * Default {@link ResultCache} using a {@link HashMap}.
 */
public class HashMapResultCache implements ResultCache {
    private final Map<Combination, TestResult> results = new HashMap<>();

    @Override
    public boolean containsResultFor(Combination testInput) {
        return results.containsKey(testInput);
    }

    @Override
    public TestResult getResultFor(Combination testInput) {
        return results.get(testInput);
    }

    @Override
    public void addResultIfAbsentFor(Combination testInput, TestResult result) {
        results.putIfAbsent(testInput, result);
    }

    /**
     * @return map containing all executed {@link Combination}s with their corresponding {@link TestResult}s.
     */
    public Map<Combination, TestResult> getResults() {
        return results;
    }
}
