package io.github.coffee4j.algorithmic.interleaving.manager;

import io.github.coffee4j.algorithmic.model.CompleteTestModel;

/**
 * Factory for creating an {@link GeneratingInterleavingCombinatorialTestManager}.
 */
@FunctionalInterface
public interface GeneratingInterleavingManagerFactory extends InterleavingManagerFactory {
    @Override
    GeneratingInterleavingCombinatorialTestManager create(InterleavingCombinatorialTestConfiguration testConfiguration, CompleteTestModel testModel);
}
