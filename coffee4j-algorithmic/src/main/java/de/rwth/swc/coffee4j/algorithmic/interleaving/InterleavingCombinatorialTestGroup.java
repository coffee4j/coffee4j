package de.rwth.swc.coffee4j.algorithmic.interleaving;

import de.rwth.swc.coffee4j.algorithmic.interleaving.feedback.FeedbackCheckingStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.Objects;

/**
 * Class representing a group for interleaving combinatorial testing.
 */
public class InterleavingCombinatorialTestGroup {
    private final Object identifier;

    private final TestInputGenerationStrategy generationStrategy;
    private final IdentificationStrategy identificationStrategy;
    private final FeedbackCheckingStrategy feedbackCheckingStrategy;

    /**
     * @param identifier identifier for the testing group. Must not be null.
     * @param generationStrategy used strategy for test input generation. Must not be null.
     * @param identificationStrategy used strategy for identification. Must not be null.
     * @param feedbackCheckingStrategy used strategy for feedback checking. Must not be null.
     */
    public InterleavingCombinatorialTestGroup(Object identifier,
                                              TestInputGenerationStrategy generationStrategy,
                                              IdentificationStrategy identificationStrategy,
                                              FeedbackCheckingStrategy feedbackCheckingStrategy) {
        this.identifier = Preconditions.notNull(identifier);
        this.generationStrategy = Preconditions.notNull(generationStrategy);
        this.identificationStrategy = Preconditions.notNull(identificationStrategy);
        this.feedbackCheckingStrategy = Preconditions.notNull(feedbackCheckingStrategy);
    }

    public Object getIdentifier() {
        return identifier;
    }

    public TestInputGenerationStrategy getGenerationStrategy() {
        return generationStrategy;
    }

    public IdentificationStrategy getIdentificationStrategy() {
        return identificationStrategy;
    }

    public FeedbackCheckingStrategy getFeedbackCheckingStrategy() {
        return feedbackCheckingStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterleavingCombinatorialTestGroup)) return false;
        InterleavingCombinatorialTestGroup that = (InterleavingCombinatorialTestGroup) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(generationStrategy, that.generationStrategy) &&
                Objects.equals(identificationStrategy, that.identificationStrategy) &&
                Objects.equals(feedbackCheckingStrategy, that.feedbackCheckingStrategy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "Interleaving Combinatorial Test Group {" +
                "identifier=" + identifier.toString() +
                ", generationStrategy=" + generationStrategy.toString() +
                ", identificationStrategy=" + identificationStrategy.toString() +
                ", feedbackCheckingStrategy=" + feedbackCheckingStrategy.toString() +
                '}';
    }
}
