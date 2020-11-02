package de.rwth.swc.coffee4j.engine.configuration.execution;

import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.classification.NoOpClassificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.conflict.ConflictDetectionConfiguration;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.feedback.FeedbackCheckingStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.DefaultGeneratingInterleavingManager;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.DefaultInterleavingManager;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.GeneratingInterleavingManagerFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingManagerFactory;

import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.Buildable;
import de.rwth.swc.coffee4j.engine.converter.model.IndexBasedModelConverter;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverterFactory;
import de.rwth.swc.coffee4j.engine.report.InterleavingExecutionReporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class InterleavingExecutionConfiguration {
    
    protected final InterleavingManagerFactory managerFactory;
    private final ModelConverterFactory modelConverterFactory;
    private final ConflictDetectionConfiguration conflictDetectionConfiguration;
    private final TestInputGenerationStrategyFactory testInputGenerationStrategyFactory;
    private final IdentificationStrategyFactory identificationStrategyFactory;
    private final FeedbackCheckingStrategyFactory feedbackCheckingStrategyFactory;
    private final ClassificationStrategyFactory classificationStrategyFactory;
    private final ConstraintCheckerFactory constraintCheckerFactory;
    private final List<InterleavingExecutionReporter> executionReporters;
    private final List<ArgumentConverter> argumentConverters;
    private final ExecutionMode executionMode;
    private final boolean isGenerating;

    InterleavingExecutionConfiguration(Builder builder) {
        Preconditions.notNull(builder.managerFactory);
        Preconditions.notNull(builder.modelConverterFactory);
        Preconditions.notNull(builder.conflictDetectionConfiguration);
        Preconditions.notNull(builder.testInputGenerationStrategyFactory);
        Preconditions.notNull(builder.identificationStrategyFactory);
        Preconditions.notNull(builder.feedbackCheckingStrategyFactory);
        Preconditions.notNull(builder.classificationStrategyFactory);
        Preconditions.notNull(builder.executionMode);
        Preconditions.check(!builder.isGenerating
                || builder.managerFactory instanceof GeneratingInterleavingManagerFactory);
        
        managerFactory = builder.managerFactory;
        modelConverterFactory = builder.modelConverterFactory;
        conflictDetectionConfiguration = builder.conflictDetectionConfiguration;
        testInputGenerationStrategyFactory = builder.testInputGenerationStrategyFactory;
        identificationStrategyFactory = builder.identificationStrategyFactory;
        feedbackCheckingStrategyFactory = builder.feedbackCheckingStrategyFactory;
        classificationStrategyFactory = builder.classificationStrategyFactory;
        constraintCheckerFactory = builder.constraintCheckerFactory;
        executionReporters = new ArrayList<>(builder.executionReporters);
        argumentConverters = new ArrayList<>(builder.argumentConverters);
        executionMode = builder.executionMode;
        isGenerating = builder.isGenerating;
    }

    public InterleavingManagerFactory getManagerFactory() {
        return managerFactory;
    }

    public ModelConverterFactory getModelConverterFactory() {
        return modelConverterFactory;
    }

    public ConflictDetectionConfiguration getConflictDetectionConfiguration() {
        return conflictDetectionConfiguration;
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

    public ClassificationStrategyFactory getClassificationStrategyFactory() {
        return classificationStrategyFactory;
    }

    public ConstraintCheckerFactory getConstraintCheckerFactory() {
        return constraintCheckerFactory;
    }

    public List<InterleavingExecutionReporter> getExecutionReporters() {
        return executionReporters;
    }

    public List<ArgumentConverter> getArgumentConverters() {
        return argumentConverters;
    }
    
    /**
     * @return the execution mode for executing the test inputs in groups
     */
    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public boolean isGenerating() {
        return isGenerating;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof InterleavingExecutionConfiguration)) {
            return false;
        }
        
        final InterleavingExecutionConfiguration other =
                (InterleavingExecutionConfiguration) object;
        return Objects.equals(modelConverterFactory, other.modelConverterFactory) &&
                Objects.equals(conflictDetectionConfiguration, other.conflictDetectionConfiguration) &&
                Objects.equals(testInputGenerationStrategyFactory, other.testInputGenerationStrategyFactory) &&
                Objects.equals(identificationStrategyFactory, other.identificationStrategyFactory) &&
                Objects.equals(feedbackCheckingStrategyFactory, other.feedbackCheckingStrategyFactory) &&
                Objects.equals(classificationStrategyFactory, other.classificationStrategyFactory) &&
                Objects.equals(constraintCheckerFactory, other.constraintCheckerFactory) &&
                Objects.equals(executionReporters, other.executionReporters) &&
                Objects.equals(argumentConverters, other.argumentConverters) &&
                Objects.equals(executionMode, other.executionMode) &&
                Objects.equals(isGenerating, other.isGenerating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managerFactory, modelConverterFactory, conflictDetectionConfiguration,
                testInputGenerationStrategyFactory, identificationStrategyFactory, feedbackCheckingStrategyFactory,
                classificationStrategyFactory, constraintCheckerFactory, executionReporters, argumentConverters,
                executionMode, isGenerating);
    }

    @Override
    public String toString() {
        return "InterleavingExecutionConfiguration{" +
                "managerFactory=" + managerFactory +
                ", modelConverterFactory=" + modelConverterFactory +
                ", conflictDetectionConfiguration=" + conflictDetectionConfiguration +
                ", testInputGenerationStrategyFactory=" + testInputGenerationStrategyFactory +
                ", identificationStrategyFactory=" + identificationStrategyFactory +
                ", feedbackCheckingStrategyFactory=" + feedbackCheckingStrategyFactory +
                ", classificationStrategyFactory=" + classificationStrategyFactory +
                ", constraintCheckerFactory=" + constraintCheckerFactory +
                ", executionReporters=" + executionReporters +
                ", argumentConverters=" + argumentConverters +
                ", executionMode=" + executionMode +
                ", isGenerating=" + isGenerating +
                '}';
    }
    
    public static Builder executionConfiguration() {
        return new Builder()
                .managerFactory(DefaultInterleavingManager.managerFactory())
                .isGenerating(false);
    }
    
    public static Builder generatingExecutionConfiguration() {
        return executionConfiguration()
                .managerFactory(DefaultGeneratingInterleavingManager.managerFactory())
                .isGenerating(true);
    }

    public static class Builder
            implements Buildable<InterleavingExecutionConfiguration> {
        
        protected InterleavingManagerFactory managerFactory;
        private ModelConverterFactory modelConverterFactory = IndexBasedModelConverter::new;
        private ConflictDetectionConfiguration conflictDetectionConfiguration = ConflictDetectionConfiguration.disable();
        private TestInputGenerationStrategyFactory testInputGenerationStrategyFactory;
        private IdentificationStrategyFactory identificationStrategyFactory;
        private FeedbackCheckingStrategyFactory feedbackCheckingStrategyFactory;
        private ClassificationStrategyFactory classificationStrategyFactory = NoOpClassificationStrategy.noOpClassificationStrategy();
        private ConstraintCheckerFactory constraintCheckerFactory =  MinimalForbiddenTuplesChecker.minimalForbiddenTuplesChecker();
        private final List<InterleavingExecutionReporter> executionReporters = new ArrayList<>();
        private final List<ArgumentConverter> argumentConverters = new ArrayList<>();
        private ExecutionMode executionMode = ExecutionMode.EXECUTE_ALL;
        private boolean isGenerating;
    
    
        public Builder managerFactory(InterleavingManagerFactory managerFactory) {
            this.managerFactory = managerFactory;
            return this;
        }
        
        public Builder modelConverterFactory(ModelConverterFactory modelConverterFactory) {
            this.modelConverterFactory = modelConverterFactory;
            return this;
        }

        public Builder conflictDetectionConfiguration(ConflictDetectionConfiguration conflictDetectionConfiguration) {
            this.conflictDetectionConfiguration = conflictDetectionConfiguration;
            return this;
        }

        public Builder testInputGenerationStrategyFactory(TestInputGenerationStrategyFactory testInputGenerationStrategyFactory) {
            this.testInputGenerationStrategyFactory = testInputGenerationStrategyFactory;
            return this;
        }

        public Builder identificationStrategyFactory(IdentificationStrategyFactory identificationStrategyFactory) {
            this.identificationStrategyFactory = identificationStrategyFactory;
            return this;
        }

        public Builder feedbackCheckingStrategyFactory(FeedbackCheckingStrategyFactory feedbackCheckingStrategyFactory) {
            this.feedbackCheckingStrategyFactory = feedbackCheckingStrategyFactory;
            return this;
        }

        public Builder classificationStrategyFactory(ClassificationStrategyFactory classificationStrategyFactory) {
            this.classificationStrategyFactory = classificationStrategyFactory;
            return this;
        }

        public Builder constraintCheckingFactory(ConstraintCheckerFactory constraintCheckerFactory) {
            this.constraintCheckerFactory = constraintCheckerFactory;
            return this;
        }

        public Builder executionReporter(InterleavingExecutionReporter executionReporter) {
            this.executionReporters.add(Preconditions.notNull(executionReporter));
            return this;
        }

        public Builder executionReporters(InterleavingExecutionReporter... executionReporters) {
            Preconditions.notNull(executionReporters);

            for (InterleavingExecutionReporter executionReporter : executionReporters) {
                this.executionReporters.add(Preconditions.notNull(executionReporter));
            }

            return this;
        }

        public Builder executionReporters(Collection<InterleavingExecutionReporter> executionReporters) {
            Preconditions.notNull(executionReporters);
            Preconditions.check(!executionReporters.contains(null));

            this.executionReporters.addAll(executionReporters);
            return this;
        }

        public Builder argumentConverter(ArgumentConverter argumentConverter) {
            this.argumentConverters.add(Preconditions.notNull(argumentConverter));
            return this;
        }

        public Builder argumentConverters(ArgumentConverter... argumentConverters) {
            Preconditions.notNull(argumentConverters);

            for (ArgumentConverter argumentConverter : argumentConverters) {
                this.argumentConverters.add(Preconditions.notNull(argumentConverter));
            }

            return this;
        }


        public Builder argumentConverters(Collection<ArgumentConverter> argumentConverters) {
            Preconditions.notNull(argumentConverters);
            Preconditions.check(!argumentConverters.contains(null));

            this.argumentConverters.addAll(argumentConverters);
            return this;
        }
    
        /**
         * Sets the execution mode for the interleaving combinatorial test configuration.
         *
         * @param executionMode the mode to use. The default is {@link ExecutionMode#EXECUTE_ALL}.
         *     Must not be {@code null}
         * @return this
         */
        public Builder executionMode(ExecutionMode executionMode) {
            this.executionMode = Preconditions.notNull(executionMode);
        
            return this;
        }

        public Builder isGenerating(boolean isGenerating) {
            this.isGenerating = Preconditions.notNull(isGenerating);

            return this;
        }
    
        @Override
        public InterleavingExecutionConfiguration build() {
            return new InterleavingExecutionConfiguration(this);
        }
    }
}