package io.github.coffee4j.algorithmic.interleaving.manager;

import io.github.coffee4j.algorithmic.model.CompleteTestModel;

/**
 * Factory for creating an {@link InterleavingCombinatorialTestManager}.
 */
@FunctionalInterface
public interface InterleavingManagerFactory {
    InterleavingCombinatorialTestManager create(InterleavingCombinatorialTestConfiguration testConfiguration, CompleteTestModel testModel);
}
