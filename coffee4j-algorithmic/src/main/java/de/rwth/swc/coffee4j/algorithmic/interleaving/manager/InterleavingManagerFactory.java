package de.rwth.swc.coffee4j.algorithmic.interleaving.manager;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;

/**
 * Factory for creating an {@link InterleavingCombinatorialTestManager}.
 */
@FunctionalInterface
public interface InterleavingManagerFactory {
    InterleavingCombinatorialTestManager create(InterleavingCombinatorialTestConfiguration testConfiguration, CompleteTestModel testModel);
}
