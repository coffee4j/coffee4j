package de.rwth.swc.coffee4j.algorithmic.classification;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

/**
 * Configuration used to create a {@link ClassificationStrategy}.
 */
public class ClassificationConfiguration {
    
    private final CompleteTestModel testModel;
    private final ConstraintChecker constraintChecker;

    private ClassificationConfiguration(Builder builder) {
        this.testModel = Preconditions.notNull(builder.testModel);
        this.constraintChecker = Preconditions.notNull(builder.constraintChecker);
    }

    /**
     * @return builder for {@link ClassificationConfiguration}
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

    /**
     * Builder class for a {@link ClassificationConfiguration}
     */
    public static class Builder {
        
        private CompleteTestModel testModel;
        private ConstraintChecker constraintChecker;

        public Builder testModel(CompleteTestModel testModel) {
            this.testModel = testModel;
            return this;
        }

        public Builder constraintChecker(ConstraintChecker constraintChecker) {
            this.constraintChecker = constraintChecker;
            return this;
        }

        public ClassificationConfiguration build() {
            return new ClassificationConfiguration(this);
        }
        
    }
    
}
