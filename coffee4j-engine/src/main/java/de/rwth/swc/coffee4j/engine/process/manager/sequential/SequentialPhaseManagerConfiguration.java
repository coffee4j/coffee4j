package de.rwth.swc.coffee4j.engine.process.manager.sequential;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.engine.process.extension.DefaultExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutorFactory;
import de.rwth.swc.coffee4j.engine.process.manager.ConflictDetector;
import de.rwth.swc.coffee4j.engine.process.manager.ConflictDetectorFactory;
import de.rwth.swc.coffee4j.engine.process.manager.PhaseManager;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionPhase;
import de.rwth.swc.coffee4j.engine.process.phase.execution.ExecutionPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.model.ModelModificationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.model.ModelModificationPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.characterization.FaultCharacterizationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.characterization.FaultCharacterizationPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.classification.SequentialClassificationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.classification.SequentialClassificationPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.generation.SequentialGenerationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.generation.SequentialGenerationPhaseFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data class for the configuration of a {@link PhaseManager}
 *
 * Models a complete combinatorial test.
 */
public class SequentialPhaseManagerConfiguration {
    
    private final SequentialExecutionConfiguration executionConfiguration;
    private final TestMethodConfiguration testMethodConfiguration;
    private final List<Extension> extensions;
    
    private final ExtensionExecutorFactory extensionExecutorFactory;
    private final ExecutionPhaseFactory executionPhaseFactory;
    private final SequentialGenerationPhaseFactory generationPhaseFactory;
    private final FaultCharacterizationPhaseFactory faultCharacterizationPhaseFactory;
    private final SequentialClassificationPhaseFactory classificationPhaseFactory;
    private final ConflictDetectorFactory conflictDetectorFactory;
    private final ModelModificationPhaseFactory modelModificationPhaseFactory;

    private SequentialPhaseManagerConfiguration(Builder builder) {
        executionConfiguration = Preconditions.notNull(builder.executionConfiguration, "executionConfiguration");
        testMethodConfiguration = Preconditions.notNull(builder.testMethodConfiguration, "testMethodConfiguration");
        extensions = Collections.unmodifiableList(new ArrayList<>(builder.extensions));
        
        extensionExecutorFactory = Preconditions.notNull(builder.extensionExecutorFactory, "extensionExecutorFactory");
        executionPhaseFactory = Preconditions.notNull(builder.executionPhaseFactory, "executionPhaseFactory");
        generationPhaseFactory = Preconditions.notNull(builder.generationPhaseFactory, "generationPhaseFactory");
        faultCharacterizationPhaseFactory = Preconditions.notNull(builder.faultCharacterizationPhaseFactory,
                "faultCharacterizationPhaseFactory");
        classificationPhaseFactory = Preconditions.notNull(builder.classificationPhaseFactory,
                "classificationPhaseFactory");
        conflictDetectorFactory = Preconditions.notNull(builder.conflictDetectorFactory, "conflictDetectorFactory");
        modelModificationPhaseFactory = Preconditions.notNull(builder.modelModificationPhaseFactory,
                "modelModificationPhaseFactory");
    }

    public SequentialExecutionConfiguration getExecutionConfiguration() {
        return executionConfiguration;
    }

    public TestMethodConfiguration getTestMethodConfiguration() {
        return testMethodConfiguration;
    }

    public List<Extension> getExtensions() {
        return extensions;
    }
    
    public ModelModificationPhaseFactory getModelModificationPhaseFactory() {
        return modelModificationPhaseFactory;
    }
    
    public ExtensionExecutorFactory getExtensionExecutorFactory() {
        return extensionExecutorFactory;
    }
    
    public ExecutionPhaseFactory getExecutionPhaseFactory() {
        return executionPhaseFactory;
    }
    
    public SequentialGenerationPhaseFactory getGenerationPhaseFactory() {
        return generationPhaseFactory;
    }
    
    public FaultCharacterizationPhaseFactory getFaultCharacterizationPhaseFactory() {
        return faultCharacterizationPhaseFactory;
    }
    
    public SequentialClassificationPhaseFactory getClassificationPhaseFactory() {
        return classificationPhaseFactory;
    }
    
    public ConflictDetectorFactory getConflictDetectorFactory() {
        return conflictDetectorFactory;
    }
    
    public Builder toBuilder() {
        return new Builder()
                .executionConfiguration(executionConfiguration)
                .testMethodConfiguration(testMethodConfiguration)
                .extensions(extensions)
                .extensionExecutorFactory(extensionExecutorFactory)
                .executionPhaseFactory(executionPhaseFactory)
                .generationPhaseFactory(generationPhaseFactory)
                .faultCharacterizationPhaseFactory(faultCharacterizationPhaseFactory)
                .classificationPhaseFactory(classificationPhaseFactory)
                .conflictDetectorFactory(conflictDetectorFactory)
                .modelModificationPhaseFactory(modelModificationPhaseFactory);
    }
    
    /**
     * Initiates the Builder pattern for a {@link SequentialPhaseManagerConfiguration}
     *
     * @return a {@link Builder}
     */
    public static Builder phaseManagerConfiguration() {
        return new Builder();
    }

    /**
     * Builder for a {@link SequentialPhaseManagerConfiguration}
     */
    public static class Builder {
        
        private SequentialExecutionConfiguration executionConfiguration;
        private TestMethodConfiguration testMethodConfiguration;
        private final List<Extension> extensions = new ArrayList<>();

        private ExtensionExecutorFactory extensionExecutorFactory = DefaultExtensionExecutor::new;
        private ExecutionPhaseFactory executionPhaseFactory = ExecutionPhase::new;
        private SequentialGenerationPhaseFactory generationPhaseFactory = SequentialGenerationPhase::new;
        private FaultCharacterizationPhaseFactory faultCharacterizationPhaseFactory = FaultCharacterizationPhase::new;
        private SequentialClassificationPhaseFactory classificationPhaseFactory = SequentialClassificationPhase::new;
        private ConflictDetectorFactory conflictDetectorFactory = ConflictDetector::new;
        private ModelModificationPhaseFactory modelModificationPhaseFactory = ModelModificationPhase::new;

        /**
         * Sets the {@link SequentialExecutionConfiguration}
         *
         * @param executionConfiguration the {@link SequentialExecutionConfiguration} to set
         * @return this {@link Builder}
         */
        public Builder executionConfiguration(SequentialExecutionConfiguration executionConfiguration) {
            this.executionConfiguration = executionConfiguration;
            return this;
        }

        /**
         * Sets the {@link TestMethodConfiguration}
         *
         * @param testMethodConfiguration the {@link TestMethodConfiguration} to set
         * @return this {@link Builder}
         */
        public Builder testMethodConfiguration(TestMethodConfiguration testMethodConfiguration) {
            this.testMethodConfiguration = testMethodConfiguration;
            return this;
        }

        /**
         * Adds the {@link Extension extensions}
         *
         * @param extensions the {@link Extension extension} to add
         * @return this {@link Builder}
         */
        public Builder extensions(List<Extension> extensions) {
            this.extensions.addAll(extensions);
            return this;
        }

        /**
         * Sets the {@link ExtensionExecutorFactory}
         *
         * @param extensionExecutorFactory the {@link ExtensionExecutorFactory} to set
         * @return this {@link Builder}
         */
        public Builder extensionExecutorFactory(ExtensionExecutorFactory extensionExecutorFactory) {
            this.extensionExecutorFactory = extensionExecutorFactory;
            return this;
        }

        /**
         * Sets the {@link ExecutionPhaseFactory}
         *
         * @param abstractExecutionPhaseFactory the {@link ExecutionPhaseFactory} to set
         * @return this {@link Builder}
         */
        public Builder executionPhaseFactory(ExecutionPhaseFactory abstractExecutionPhaseFactory) {
            this.executionPhaseFactory = abstractExecutionPhaseFactory;
            return this;
        }

        /**
         * Sets the {@link SequentialGenerationPhaseFactory}
         *
         * @param generationPhaseFactory the {@link SequentialGenerationPhaseFactory} to set
         * @return this {@link Builder}
         */
        public Builder generationPhaseFactory(SequentialGenerationPhaseFactory generationPhaseFactory) {
            this.generationPhaseFactory = generationPhaseFactory;
            return this;
        }

        /**
         * Sets the {@link FaultCharacterizationPhaseFactory}
         *
         * @param faultCharacterizationPhaseFactory the {@link FaultCharacterizationPhaseFactory} to set
         * @return this {@link Builder}
         */
        public Builder faultCharacterizationPhaseFactory(FaultCharacterizationPhaseFactory faultCharacterizationPhaseFactory) {
            this.faultCharacterizationPhaseFactory = faultCharacterizationPhaseFactory;
            return this;
        }

        public Builder classificationPhaseFactory(SequentialClassificationPhaseFactory classificationPhaseFactory) {
            this.classificationPhaseFactory = classificationPhaseFactory;
            return this;
        }

        /**
         * Sets the {@link ConflictDetectorFactory}
         *
         * @param conflictDetectorFactory the {@link ConflictDetectorFactory} to set
         * @return this {@link Builder}
         */
        public Builder conflictDetectorFactory(ConflictDetectorFactory conflictDetectorFactory) {
            this.conflictDetectorFactory = conflictDetectorFactory;
            return this;
        }
    
        /**
         * Sets the {@link ModelModificationPhaseFactory}.
         *
         * @param modelModificationPhaseFactory the {@link ModelModificationPhaseFactory} to set
         * @return this {@link Builder}
         */
        public Builder modelModificationPhaseFactory(ModelModificationPhaseFactory modelModificationPhaseFactory) {
            this.modelModificationPhaseFactory = modelModificationPhaseFactory;
            
            return this;
        }
        
        /**
         * Builds the {@link SequentialPhaseManagerConfiguration}
         *
         * @return the built {@link SequentialPhaseManagerConfiguration}
         */
        public SequentialPhaseManagerConfiguration build() {
            return new SequentialPhaseManagerConfiguration(this);
        }

    }
    
}
