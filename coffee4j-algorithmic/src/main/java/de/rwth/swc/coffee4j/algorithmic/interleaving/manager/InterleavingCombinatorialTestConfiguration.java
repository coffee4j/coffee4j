package de.rwth.swc.coffee4j.algorithmic.interleaving.manager;

import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.feedback.FeedbackCheckingStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.report.InterleavingGenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.SequentialCombinatorialTestConfiguration;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.Objects;
import java.util.Optional;

/**
 * Configuration for an {@link InterleavingCombinatorialTestManager} to generate test inputs and start identification
 * of failure-inducing combinations for a given test-model.
 *
 * <p>
 * Interleaving version of {@link SequentialCombinatorialTestConfiguration}
 * </p>
 */
public final class InterleavingCombinatorialTestConfiguration {
    private final TestInputGenerationStrategyFactory testInputGenerationStrategyFactory;
    private final IdentificationStrategyFactory identificationStrategyFactory;
    private final FeedbackCheckingStrategyFactory feedbackCheckingStrategyFactory;
    private final ConstraintCheckerFactory constraintCheckerFactory;
    private final ClassificationStrategyFactory classificationStrategyFactory;
    private final InterleavingGenerationReporter generationReporter;

    /**
     * Creates a new configuration.
     *
     * @param testInputGenerationStrategyFactory factory creating strategy for the generation-phase of interleaving CT.
     *                                           Must not be {@code null}.
     * @param identificationStrategyFactory factory for creating strategy of the identification-phase of interleaving CT.
     *                                      Must not be {@code null}.
     * @param feedbackCheckingStrategyFactory factory for creating strategy of the checking-phase of interleaving CT.
     *                                        Must not be {@code null}.
     * @param classificationStrategyFactory factory for creating strategy of the classification-phase of interleaving CT.
     *                                      Must not be {@code null}.
     * @param constraintCheckerFactory factory for creating a Constraint Checker. Must not be {@code null}.
     * @param generationReporter generation reporter for notification of events in a combinatorial test.
     *                           Can be {@code null}.
     */
    public InterleavingCombinatorialTestConfiguration(TestInputGenerationStrategyFactory testInputGenerationStrategyFactory,
                                                      IdentificationStrategyFactory identificationStrategyFactory,
                                                      FeedbackCheckingStrategyFactory feedbackCheckingStrategyFactory,
                                                      ClassificationStrategyFactory classificationStrategyFactory,
                                                      ConstraintCheckerFactory constraintCheckerFactory,
                                                      InterleavingGenerationReporter generationReporter) {
        this.testInputGenerationStrategyFactory = Preconditions.notNull(testInputGenerationStrategyFactory);
        this.identificationStrategyFactory = Preconditions.notNull(identificationStrategyFactory);
        this.feedbackCheckingStrategyFactory = Preconditions.notNull(feedbackCheckingStrategyFactory);
        this.constraintCheckerFactory = Preconditions.notNull(constraintCheckerFactory);
        this.classificationStrategyFactory = Preconditions.notNull(classificationStrategyFactory);
        this.generationReporter = generationReporter;
    }

    public TestInputGenerationStrategyFactory getTestInputGenerationStrategyFactory() {
        return testInputGenerationStrategyFactory;
    }

    public IdentificationStrategyFactory getIdentificationStrategyFactory() {
        return identificationStrategyFactory;
    }

    public FeedbackCheckingStrategyFactory getFeedbackCheckingStrategyFactory() {
        return feedbackCheckingStrategyFactory;
    }

    public ConstraintCheckerFactory getConstraintCheckerFactory() {
        return constraintCheckerFactory;
    }

    public ClassificationStrategyFactory getClassificationStrategyFactory() { return classificationStrategyFactory; }

    public Optional<InterleavingGenerationReporter> getGenerationReporter() {
        return Optional.ofNullable(generationReporter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterleavingCombinatorialTestConfiguration)) return false;
        InterleavingCombinatorialTestConfiguration that = (InterleavingCombinatorialTestConfiguration) o;
        return Objects.equals(testInputGenerationStrategyFactory, that.testInputGenerationStrategyFactory) &&
                Objects.equals(identificationStrategyFactory, that.identificationStrategyFactory) &&
                Objects.equals(feedbackCheckingStrategyFactory, that.feedbackCheckingStrategyFactory) &&
                Objects.equals(constraintCheckerFactory, that.constraintCheckerFactory) &&
                Objects.equals(classificationStrategyFactory, that.classificationStrategyFactory) &&
                Objects.equals(generationReporter, that.generationReporter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testInputGenerationStrategyFactory, identificationStrategyFactory, feedbackCheckingStrategyFactory, constraintCheckerFactory, classificationStrategyFactory, generationReporter);
    }

    @Override
    public String toString() {
        return "InterleavingCombinatorialTestConfiguration{" +
                "testInputGenerationStrategyFactory=" + testInputGenerationStrategyFactory +
                ", identificationStrategyFactory=" + identificationStrategyFactory +
                ", feedbackCheckingStrategyFactory=" + feedbackCheckingStrategyFactory +
                ", classificationStrategyFactory=" + classificationStrategyFactory +
                ", constraintCheckerFactory=" + constraintCheckerFactory +
                ", generationReporter=" + generationReporter +
                '}';
    }
}
