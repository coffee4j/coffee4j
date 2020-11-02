package de.rwth.swc.coffee4j.algorithmic.interleaving.feedback;

/**
 * Factory for creating a {@link FeedbackCheckingStrategy}.
 */
@FunctionalInterface
public interface FeedbackCheckingStrategyFactory {
    FeedbackCheckingStrategy create(FeedbackCheckingConfiguration config);
}
