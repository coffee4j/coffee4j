package de.rwth.swc.coffee4j.engine.process.interleaving;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.process.manager.PhaseManager;
import de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.checking.CheckingPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.identification.IdentificationPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.extension.DefaultExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutorFactory;
import de.rwth.swc.coffee4j.engine.process.phase.execution.*;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.classification.InterleavingClassificationPhaseFactory;
import de.rwth.swc.coffee4j.engine.process.phase.model.ModelModificationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.model.ModelModificationPhaseFactory;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.checking.CheckingPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.classification.InterleavingClassificationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation.InterleavingGenerationPhase;
import de.rwth.swc.coffee4j.engine.process.phase.interleaving.identification.IdentificationPhase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Configuration for a {@link PhaseManager} managing Interleaving Combinatorial
 * Testing and Error-Constraint Generation, respectively.
 */
public class InterleavingPhaseManagerConfiguration {
    
    private final InterleavingExecutionConfiguration executionConfiguration;
    private final TestMethodConfiguration testMethodConfiguration;
    private final List<Extension> extensions;
    
    private final ExtensionExecutorFactory extensionExecutorFactory;
    private final ExecutionPhaseFactory executionPhaseFactory;
    private final InterleavingGenerationPhaseFactory generationPhaseFactory;
    private final IdentificationPhaseFactory identificationPhaseFactory;
    private final CheckingPhaseFactory checkingPhaseFactory;
    private final InterleavingClassificationPhaseFactory classificationPhaseFactory;
    private final ModelModificationPhaseFactory modelModificationPhaseFactory;

    private InterleavingPhaseManagerConfiguration(Builder builder) {
        executionConfiguration = Preconditions.notNull(builder.executionConfiguration);
        testMethodConfiguration = Preconditions.notNull(builder.testMethodConfiguration);
        extensions = Collections.unmodifiableList(Preconditions.notNull(builder.extensions));
        
        extensionExecutorFactory = Preconditions.notNull(builder.extensionExecutorFactory);
        executionPhaseFactory = Preconditions.notNull(builder.executionPhaseFactory);
        generationPhaseFactory = Preconditions.notNull(builder.generationPhaseFactory);
        identificationPhaseFactory = Preconditions.notNull(builder.identificationPhaseFactory);
        checkingPhaseFactory = Preconditions.notNull(builder.checkingPhaseFactory);
        classificationPhaseFactory = Preconditions.notNull(builder.classificationPhaseFactory);
        modelModificationPhaseFactory = Preconditions.notNull(builder.modelModificationPhaseFactory);
    }

    public InterleavingExecutionConfiguration getExecutionConfiguration() {
        return executionConfiguration;
    }

    public TestMethodConfiguration getTestMethodConfiguration() {
        return testMethodConfiguration;
    }

    public List<Extension> getExtensions() {
        return extensions;
    }
    
    public ExtensionExecutorFactory getExtensionExecutorFactory() {
        return extensionExecutorFactory;
    }
    
    public ExecutionPhaseFactory getExecutionPhaseFactory() {
        return executionPhaseFactory;
    }
    
    public InterleavingGenerationPhaseFactory getGenerationPhaseFactory() {
        return generationPhaseFactory;
    }
    
    public IdentificationPhaseFactory getIdentificationPhaseFactory() {
        return identificationPhaseFactory;
    }
    
    public CheckingPhaseFactory getCheckingPhaseFactory() {
        return checkingPhaseFactory;
    }
    
    public InterleavingClassificationPhaseFactory getClassificationPhaseFactory() {
        return classificationPhaseFactory;
    }
    
    public ModelModificationPhaseFactory getModelModificationPhaseFactory() {
        return modelModificationPhaseFactory;
    }
    
    public Builder toBuilder() {
        return phaseManagerConfiguration()
                .executionConfiguration(executionConfiguration)
                .testMethodConfiguration(testMethodConfiguration)
                .extensions(extensions)
                .extensionExecutorFactory(extensionExecutorFactory)
                .executionPhaseFactory(executionPhaseFactory)
                .generationPhaseFactory(generationPhaseFactory)
                .identificationPhaseFactory(identificationPhaseFactory)
                .checkingPhaseFactory(checkingPhaseFactory)
                .classificationPhaseFactory(classificationPhaseFactory)
                .modelModificationPhaseFactory(modelModificationPhaseFactory);
    }
    
    /**
     * @return Builder for a {@link InterleavingPhaseManagerConfiguration}.
     */
    public static Builder phaseManagerConfiguration() {
        return new Builder();
    }

    /**
     * Builder for a {@link InterleavingPhaseManagerConfiguration}.
     */
    public static class Builder {
        
        private InterleavingExecutionConfiguration executionConfiguration;
        private TestMethodConfiguration testMethodConfiguration;
        private final List<Extension> extensions = new ArrayList<>();

        private ExtensionExecutorFactory extensionExecutorFactory = DefaultExtensionExecutor::new;
        private ExecutionPhaseFactory executionPhaseFactory = ExecutionPhase::new;
        private InterleavingGenerationPhaseFactory generationPhaseFactory = InterleavingGenerationPhase::new;
        private IdentificationPhaseFactory identificationPhaseFactory = IdentificationPhase::new;
        private CheckingPhaseFactory checkingPhaseFactory = CheckingPhase::new;
        private InterleavingClassificationPhaseFactory classificationPhaseFactory = InterleavingClassificationPhase::new;
        private ModelModificationPhaseFactory modelModificationPhaseFactory = ModelModificationPhase::new;

        public Builder executionConfiguration(InterleavingExecutionConfiguration configuration) {
            this.executionConfiguration = configuration;
            return this;
        }

        public Builder testMethodConfiguration(TestMethodConfiguration testMethodConfiguration) {
            this.testMethodConfiguration = testMethodConfiguration;
            return this;
        }

        public Builder extensions(Collection<Extension> extensions) {
            this.extensions.addAll(extensions);
            return this;
        }

        public Builder extensionExecutorFactory(ExtensionExecutorFactory extensionExecutorFactory) {
            this.extensionExecutorFactory = extensionExecutorFactory;
            return this;
        }

        public Builder executionPhaseFactory(ExecutionPhaseFactory executionPhaseFactory) {
            this.executionPhaseFactory = executionPhaseFactory;
            return this;
        }

        public Builder generationPhaseFactory(InterleavingGenerationPhaseFactory generationPhaseFactory) {
            this.generationPhaseFactory = generationPhaseFactory;
            return this;
        }

        public Builder identificationPhaseFactory(IdentificationPhaseFactory identificationPhaseFactory) {
            this.identificationPhaseFactory = identificationPhaseFactory;
            return this;
        }

        public Builder checkingPhaseFactory(CheckingPhaseFactory checkingPhaseFactory) {
            this.checkingPhaseFactory = checkingPhaseFactory;
            return this;
        }

        public Builder classificationPhaseFactory(InterleavingClassificationPhaseFactory classificationPhaseFactory) {
            this.classificationPhaseFactory = classificationPhaseFactory;
            return this;
        }
        
        public Builder modelModificationPhaseFactory(ModelModificationPhaseFactory modelModificationPhaseFactory) {
            this.modelModificationPhaseFactory = modelModificationPhaseFactory;
            return this;
        }
    
        public InterleavingPhaseManagerConfiguration build() {
            return new InterleavingPhaseManagerConfiguration(this);
        }
        
    }
    
}
