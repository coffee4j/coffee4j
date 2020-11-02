package de.rwth.swc.coffee4j.algorithmic.interleaving.generator;

import java.util.Optional;

/**
 * Interface that each test input generation strategy used by interleaving CT must implement.
 */
public interface TestInputGenerationStrategy {
    /**
     * @return next test input covering as many uncovered t-tuples as possible. If all tuples are covered, an empty
     * Optional is returned.
     */
    Optional<int[]> generateNextTestInput();
}
