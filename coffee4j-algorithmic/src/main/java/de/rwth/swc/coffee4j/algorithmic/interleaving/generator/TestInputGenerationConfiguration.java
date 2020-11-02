package de.rwth.swc.coffee4j.algorithmic.interleaving.generator;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.CoverageMap;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

/**
 * Configuration used to create a {@link TestInputGenerationStrategy}.
 */
public class TestInputGenerationConfiguration {
    
    private final CompleteTestModel testModel;
    private final ConstraintChecker constraintChecker;
    private final CoverageMap coverageMap;

    private TestInputGenerationConfiguration(Builder builder) {
        this.testModel = Preconditions.notNull(builder.testModel);
        this.constraintChecker = Preconditions.notNull(builder.constraintChecker);
        this.coverageMap = Preconditions.notNull(builder.coverageMap);
    }

    /**
     * @return returns a {@link Builder} to build an {@link TestInputGenerationConfiguration}.
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

    /**
     * Builder class for a {@link TestInputGenerationConfiguration}.
     */
    public static class Builder {
        
        private CompleteTestModel testModel;
        private ConstraintChecker constraintChecker;
        private CoverageMap coverageMap;

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

        public TestInputGenerationConfiguration build() {
            return new TestInputGenerationConfiguration(this);
        }
        
    }
    
}
