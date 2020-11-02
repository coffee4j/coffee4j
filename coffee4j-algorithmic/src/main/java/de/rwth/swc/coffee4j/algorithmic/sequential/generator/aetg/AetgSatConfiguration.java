package de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg;

import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.Objects;

/**
 * A class combining all information needed to construct a new instance of the {@link AetgSatAlgorithm} algorithm. This
 * class is used instead of a constructor with all parameters to reduce API incompatibility issues when addition more
 * arguments and for a general nicer way of constructing the algorithm since constructor with many parameters are not
 * easy to read in code.
 */
public class AetgSatConfiguration {

    private static final int DEFAULT_NUMBER_OF_CANDIDATES = 50;
    private static final int DEFAULT_NUMBER_OF_TRIES = 5;

    private final int numberOfCandidates;
    private final int numberOfTries;
    private final TestModel model;
    private final Reporter reporter;

    private AetgSatConfiguration(Builder builder) {
        this.numberOfCandidates = builder.numberOfCandidates;
        this.numberOfTries = builder.numberOfTries;
        this.model = Objects.requireNonNull(builder.model);
        this.reporter = Objects.requireNonNull(builder.reporter);
    }

    /**
     * Construct a builder for a new configuration.
     *
     * @return a fluent builder
     */
    public static Builder aetgSatConfiguration() {
        return new Builder();
    }

    public int getNumberOfCandidates() {
        return numberOfCandidates;
    }
    
    public int getMaximumNumberOfTries() {
        return numberOfTries;
    }
    
    public TestModel getModel() {
        return model;
    }
    
    public Reporter getReporter() {
        return reporter;
    }

    /**
     * Builder for {@link AetgSatConfiguration}.
     */
    public static class Builder {

        private int numberOfCandidates = DEFAULT_NUMBER_OF_CANDIDATES;
        private int numberOfTries = DEFAULT_NUMBER_OF_TRIES;
        private TestModel model;
        private Reporter reporter = Reporter.getEmptyReporter();

        /**
         * Sets the execution reporter.
         *
         * @param reporter the reporter. This may not be {@code null}.
         * @return the builder for method chaining
         */
        public Builder reporter(Reporter reporter) {
            Preconditions.notNull(reporter);
            this.reporter = reporter;
            return this;
        }

        /**
         * Sets the test model.
         *
         * @param model the model. This may not be {@code null}.
         * @return the builder for method chaining
         */
        public Builder model(TestModel model) {
            Preconditions.notNull(model);
            this.model = model;
            return this;
        }

        /**
         * Sets the number of candidates that should be considered for each iteration.
         * <p>
         * The more candidates are generated, the longer the runtime of the algorithm will be, but it will also increase
         * the quality of the covering array.
         *
         * @param numberOfCandidates the number of candidates. Must be positive.
         * @return the builder for method chaining
         */
        public Builder withNumberOfCandidates(int numberOfCandidates) {
            Preconditions.check(numberOfCandidates > 0);
            this.numberOfCandidates = numberOfCandidates;
            return this;
        }

        /**
         * Sets tje number of tries that should be done before declaring a model not solvable.
         *
         * @param numberOfTries the number of tries. Must be positive.
         * @return the builder for method chaining
         */
        public Builder withNumberOfTries(int numberOfTries) {
            Preconditions.check(numberOfTries > 0);
            this.numberOfTries = numberOfTries;
            return this;
        }

        /**
         * Constructs the configuration.
         * <p>
         * Only the model is required to be set.
         *
         * @return the configuration
         */
        public AetgSatConfiguration build() {
            return new AetgSatConfiguration(this);
        }
    }
}
