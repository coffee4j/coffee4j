package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.Objects;

/**
 * A class combining all information needed to construct a new instance of the {@link IpogAlgorithm} algorithm. This class is
 * used instead of a constructor with all parameters to reduce API incompatibility issues when addition more arguments
 * and for a general nicer way of constructing the algorithm since constructor with many parameters are not easy to
 * read in code.
 */
final class IpogAlgorithmConfiguration {

    private final TestModel testModel;
    private final int testingStrength;
    private final ConstraintChecker constraintChecker;
    private final ParameterCombinationFactory factory;
    private final ParameterOrder order;
    private final Reporter reporter;

    private IpogAlgorithmConfiguration(Builder builder) {
        Preconditions.check(builder.testingStrength >= 0);
        this.testModel = Preconditions.notNull(builder.testModel);
        this.testingStrength = builder.testingStrength;
        this.constraintChecker = Preconditions.notNull(builder.constraintChecker);
        this.factory = Preconditions.notNull(builder.factory);
        this.order = Preconditions.notNull(builder.order);
        this.reporter = Preconditions.notNull(builder.reporter);
    }

    TestModel getTestModel() {
        return testModel;
    }

    int getTestingStrength() {
        return testingStrength;
    }

    ConstraintChecker getConstraintChecker() { return constraintChecker; }

    ParameterCombinationFactory getFactory() {
        return factory;
    }

    ParameterOrder getOrder() {
        return order;
    }

    Reporter getReporter() {
        return reporter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpogAlgorithmConfiguration that = (IpogAlgorithmConfiguration) o;
        return testingStrength == that.testingStrength &&
                Objects.equals(testModel, that.testModel) &&
                Objects.equals(constraintChecker, that.constraintChecker) &&
                Objects.equals(factory, that.factory) &&
                Objects.equals(order, that.order) &&
                Objects.equals(reporter, that.reporter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testModel, testingStrength, constraintChecker, factory, order, reporter);
    }

    @Override
    public String toString() {
        return "IpogAlgorithmConfiguration{" +
                "testModel=" + testModel +
                ", testingStrength=" + testingStrength +
                ", constraintChecker=" + constraintChecker +
                ", factory=" + factory +
                ", order=" + order +
                ", reporter=" + reporter +
                '}';
    }

    static Builder ipogConfiguration() {
        return new Builder();
    }

    /**
     * A implementation of the Builder patter to create a new {@link IpogAlgorithmConfiguration}.
     */
    static final class Builder {
        private TestModel testModel;
        private int testingStrength;
        private ConstraintChecker constraintChecker;
        private ParameterCombinationFactory factory = new StrengthBasedParameterCombinationFactory();
        private ParameterOrder order = new StrengthBasedParameterOrder();
        private Reporter reporter = Reporter.getEmptyReporter();

        /**
         * @param testModel used to generate the test suite. As this contains all main information and the algorithm cannot
         *                  work without it, this parameter is required. If it is not set before {@link #build()} is called, a
         *                  {@link NullPointerException} will be thrown
         * @return this
         */
        Builder testModel(TestModel testModel) {
            this.testModel = testModel;
            return this;
        }

        Builder testingStrength(int testingStrength) {
            this.testingStrength = testingStrength;
            return this;
        }

        Builder constraintChecker(ConstraintChecker constraintChecker) {
            this.constraintChecker = constraintChecker;
            return this;
        }

        /**
         * @param factory used to generate all parameter combinations which IPOG needs to cover. This is an optional
         *                field. If it is not set, the default {@link StrengthBasedParameterCombinationFactory} will be used
         * @return this
         */
        Builder factory(ParameterCombinationFactory factory) {
            this.factory = factory;
            return this;
        }

        /**
         * @param order used to determine in which order the parameters are included into the test suite during the
         *              horizontal expansion of IPOG. This is an optional field. If not set, the default of
         *              {@link StrengthBasedParameterOrder} is used for a more optimal generation time and test suite size
         * @return this
         */
        Builder order(ParameterOrder order) {
            this.order = order;
            return this;
        }

        /**
         * @param reporter a reporter which should be used to carry important information to the outside. This is an
         *                 optional field. If not set, the default of a no operation reporter is used
         * @return this
         */
        Builder reporter(Reporter reporter) {
            this.reporter = reporter;
            return this;
        }

        /**
         * @return a new complete configuration which can be used to construct an instance of {@link IpogAlgorithm}
         * @throws NullPointerException if any parameter has been set to {@code null} or if the testModel has not been set
         */
        IpogAlgorithmConfiguration build() {
            return new IpogAlgorithmConfiguration(this);
        }
    }
}
