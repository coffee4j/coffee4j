package io.github.coffee4j.algorithmic.interleaving;

import io.github.coffee4j.algorithmic.interleaving.feedback.FeedbackCheckingStrategy;
import io.github.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategy;
import io.github.coffee4j.algorithmic.interleaving.identification.IdentificationStrategy;
import io.github.coffee4j.algorithmic.util.Preconditions;

import java.util.Objects;

/**
 * Class representing a group for interleaving combinatorial testing.
 */
public record InterleavingCombinatorialTestGroup(Object identifier, TestInputGenerationStrategy generationStrategy,
                                                 IdentificationStrategy identificationStrategy,
                                                 FeedbackCheckingStrategy feedbackCheckingStrategy) {
    /**
     * @param identifier               identifier for the testing group. Must not be null.
     * @param generationStrategy       used strategy for test input generation. Must not be null.
     * @param identificationStrategy   used strategy for identification. Must not be null.
     * @param feedbackCheckingStrategy used strategy for feedback checking. Must not be null.
     */
    public InterleavingCombinatorialTestGroup {
        Preconditions.notNull(identifier);
        Preconditions.notNull(generationStrategy);
        Preconditions.notNull(identificationStrategy);
        Preconditions.notNull(feedbackCheckingStrategy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
