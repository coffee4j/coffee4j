package de.rwth.swc.coffee4j.algorithmic.interleaving.feedback;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.CoverageMap;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

/**
 * Configuration to create a {@link FeedbackCheckingStrategy}.
 */
public class FeedbackCheckingConfiguration {
    
    private final CompleteTestModel testModel;
    private final ConstraintChecker constraintChecker;
    private final CoverageMap coverageMap;
    private final int numberOfFeedbackChecks;

    private FeedbackCheckingConfiguration(Builder builder) {
        Preconditions.check(builder.numberOfFeedbackChecks > 0);

        this.testModel = Preconditions.notNull(builder.testModel);
        this.constraintChecker = Preconditions.notNull(builder.constraintChecker);
        this.coverageMap = Preconditions.notNull(builder.coverageMap);
        this.numberOfFeedbackChecks = builder.numberOfFeedbackChecks;
    }

    /**
     * @return returns {@link Builder} to build an {@link FeedbackCheckingConfiguration}.
     */
    public static Builder configuration() {
        return new Builder();
    }

    public CompleteTestModel getTestModel() {
        return testModel;
    }

    public ConstraintChecker getConstraintChecker() {
        return constraintChecker;
    }

    public CoverageMap getCoverageMap() {
        return coverageMap;
    }

    public int getNumberOfFeedbackChecks() {
        return numberOfFeedbackChecks;
    }

    /**
     * Builder class for a {@link FeedbackCheckingConfiguration}.
     */
    public static class Builder {
        
        private static final int DEFAULT_NUMBER_OF_FEEDBACK_CHECKS = 20;

        private CompleteTestModel testModel;
        private ConstraintChecker constraintChecker;
        private CoverageMap coverageMap;
        private int numberOfFeedbackChecks = DEFAULT_NUMBER_OF_FEEDBACK_CHECKS;

        public Builder testModel(CompleteTestModel testModel) {
            this.testModel = testModel;
            return this;
        }

        public Builder constraintChecker(ConstraintChecker constraintChecker) {
            this.constraintChecker = constraintChecker;
            return this;
        }

        public Builder coverageMap(CoverageMap coverageMap) {
            this.coverageMap = coverageMap;
            return this;
        }

        public Builder numberOfFeedbackChecks(int numberOfFeedbackChecks) {
            this.numberOfFeedbackChecks = numberOfFeedbackChecks;
            return this;
        }

        public FeedbackCheckingConfiguration build() {
            return new FeedbackCheckingConfiguration(this);
        }
        
    }
    
}