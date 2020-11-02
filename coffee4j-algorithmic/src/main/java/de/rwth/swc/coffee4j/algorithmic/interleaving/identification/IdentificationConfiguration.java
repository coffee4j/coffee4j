package de.rwth.swc.coffee4j.algorithmic.interleaving.identification;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.CoverageMap;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

/**
 * Configuration used to create an {@link IdentificationStrategy}.
 */
public class IdentificationConfiguration {
    
    private final CompleteTestModel testModel;
    private final ConstraintChecker constraintChecker;
    private final CoverageMap coverageMap;

    private IdentificationConfiguration(IdentificationConfigurationBuilder builder) {
        this.testModel = Preconditions.notNull(builder.testModel);
        this.constraintChecker = Preconditions.notNull(builder.constraintChecker);
        this.coverageMap = Preconditions.notNull(builder.coverageMap);
    }

    /**
     * @return returns an {@link IdentificationConfigurationBuilder} to build an {@link IdentificationConfiguration}.
     */
    public static IdentificationConfigurationBuilder configuration() {
        return new IdentificationConfigurationBuilder();
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
     * Builder class for an {@link IdentificationConfiguration}.
     */
    public static class IdentificationConfigurationBuilder {
        
        private CompleteTestModel testModel;
        private ConstraintChecker constraintChecker;
        private CoverageMap coverageMap;

        public IdentificationConfigurationBuilder testModel(CompleteTestModel testModel) {
            this.testModel = testModel;
            return this;
        }

        public IdentificationConfigurationBuilder constraintChecker(ConstraintChecker constraintChecker) {
            this.constraintChecker = constraintChecker;
            return this;
        }

        public IdentificationConfigurationBuilder coverageMap(CoverageMap coverageMap) {
            this.coverageMap = coverageMap;
            return this;
        }

        public IdentificationConfiguration build() {
            return new IdentificationConfiguration(this);
        }
        
    }
}
