package de.rwth.swc.coffee4j.engine.configuration;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.execution.TestInputExecutor;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;

/**
 * Specifies the configuration of a test class that it most likely not reused.
 */
public class TestMethodConfiguration {

    private final InputParameterModel inputParameterModel;
    private final TestInputExecutor testInputExecutor;

    private TestMethodConfiguration(Builder builder) {
        Preconditions.notNull(builder.testExecutor);
        this.inputParameterModel = Preconditions.notNull(builder.inputParameterModel);
        this.testInputExecutor = Preconditions.notNull(builder.testExecutor);
    }

    public InputParameterModel getInputParameterModel() {
        return inputParameterModel;
    }

    public TestInputExecutor getTestInputExecutor() {
        return testInputExecutor;
    }
    
    public Builder toBuilder() {
        return testMethodConfiguration()
                .inputParameterModel(inputParameterModel)
                .testExecutor(testInputExecutor);
    }

    /**
     * Initiates the builder pattern for a {@link TestMethodConfiguration}
     *
     * @return a new {@link Builder}
     */
    public static Builder testMethodConfiguration() {
        return new Builder();
    }

    /**
     * Builder Class for the {@link TestMethodConfiguration}
     */
    public static final class Builder {

        private InputParameterModel inputParameterModel;
        private TestInputExecutor testExecutor;

        /**
         * Sets the {@link InputParameterModel}
         *
         * @param inputParameterModel the model to set
         * @return this {@link Builder}
         */
        public Builder inputParameterModel(InputParameterModel inputParameterModel) {
            this.inputParameterModel = inputParameterModel;
            return this;
        }

        /**
         * Sets the {@link TestInputExecutor test input executor} used for the test.
         *
         * @param testExecutor the executor for of this test class
         * @return this {@link Builder}
         */
        public Builder testExecutor(TestInputExecutor testExecutor) {
            this.testExecutor = testExecutor;
            return this;
        }

        /**
         * Builds a {@link TestMethodConfiguration}
         *
         * @return the built {@link TestMethodConfiguration}
         */
        public TestMethodConfiguration build() {
            return new TestMethodConfiguration(this);
        }
    }
    
}
