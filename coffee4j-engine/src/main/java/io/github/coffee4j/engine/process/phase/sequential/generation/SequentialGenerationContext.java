package io.github.coffee4j.engine.process.phase.sequential.generation;

import io.github.coffee4j.algorithmic.sequential.manager.SequentialCombinatorialTestConfiguration;
import io.github.coffee4j.algorithmic.sequential.manager.SequentialCombinatorialTestManager;
import io.github.coffee4j.engine.configuration.model.InputParameterModel;
import io.github.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import io.github.coffee4j.engine.converter.model.ModelConverter;
import io.github.coffee4j.engine.process.phase.PhaseContext;
import io.github.coffee4j.engine.process.report.sequential.LoggingSequentialExecutionReporter;
import io.github.coffee4j.engine.report.DelegatingSequentialExecutionReporter;
import io.github.coffee4j.engine.report.ModelBasedArgumentConverter;
import io.github.coffee4j.engine.report.SequentialExecutionReporter;
import io.github.coffee4j.engine.process.extension.ExtensionExecutor;
import io.github.coffee4j.engine.report.DelegatingModelBasedArgumentConverter;
import io.github.coffee4j.engine.process.report.sequential.LoggingSequentialExecutionReporterForGeneration;

import java.util.List;

public class SequentialGenerationContext implements PhaseContext {

    private final ModelConverter modelConverter;
    private final SequentialCombinatorialTestManager generator;
    private final ExtensionExecutor extensionExecutor;
    private final boolean isGenerating;

    /**
     * Creates a new {@link SequentialGenerationContext} configured
     * with a {@link SequentialExecutionConfiguration},
     * an {@link InputParameterModel},
     * and an {@link ExtensionExecutor}.
     * <p>
     * Contains adapted content written by Bonn
     * from the CombinatorialTestConsumerManager from the previous version of the framework
     *
     * @param configuration the {@link SequentialExecutionConfiguration}
     *                      with which to configure the {@link SequentialGenerationContext}
     * @param model the {@link InputParameterModel} with which to configure the {@link SequentialGenerationContext}
     * @param extensionExecutor the {@link ExtensionExecutor} with which to configure the {@link SequentialGenerationContext}
     */
    public SequentialGenerationContext(SequentialExecutionConfiguration configuration,
            InputParameterModel model, ExtensionExecutor extensionExecutor) {
        
        this.extensionExecutor = extensionExecutor;
        this.modelConverter = configuration.getModelConverterFactory().create(model);
        isGenerating = configuration.isConstraintGenerator();

        final ModelBasedArgumentConverter argumentConverterManager =
                new DelegatingModelBasedArgumentConverter(configuration.getArgumentConverters());
        argumentConverterManager.initialize(modelConverter);

        final SequentialExecutionReporterToGenerationReporterAdapter reporterManager =
                new SequentialExecutionReporterToGenerationReporterAdapter(
                        buildDelegatingOrDefaultExecutionReporter(configuration.getExecutionReporters()),
                        argumentConverterManager,
                        modelConverter);

        SequentialCombinatorialTestConfiguration testConfiguration = new SequentialCombinatorialTestConfiguration(
                configuration.getCharacterizationAlgorithmFactory().orElse(null),
                configuration.getClassificationStrategyFactory(),
                configuration.getGenerators(),
                configuration.getPrioritizer(),
                reporterManager,
                configuration.getExecutionMode());

        this.generator = configuration.getManagerFactory()
                .apply(testConfiguration, modelConverter.getConvertedModel());
    }

    private SequentialExecutionReporter buildDelegatingOrDefaultExecutionReporter(
            List<SequentialExecutionReporter> executionReporters) {
        
        if(executionReporters.isEmpty()) {
            if (isGenerating) {
                return new LoggingSequentialExecutionReporterForGeneration();
            } else {
                return new LoggingSequentialExecutionReporter();
            }
        } else {
            return new DelegatingSequentialExecutionReporter(executionReporters);
        }
    }

    public ModelConverter getModelConverter() {
        return modelConverter;
    }

    public SequentialCombinatorialTestManager getGenerator() {
        return generator;
    }

    public ExtensionExecutor getExtensionExecutor() {
        return extensionExecutor;
    }

}
