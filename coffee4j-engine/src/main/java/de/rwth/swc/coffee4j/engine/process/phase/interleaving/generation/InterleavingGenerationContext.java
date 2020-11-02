package de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation;

import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingCombinatorialTestConfiguration;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.extension.ExtensionExecutor;
import de.rwth.swc.coffee4j.engine.process.phase.PhaseContext;
import de.rwth.swc.coffee4j.engine.process.report.interleaving.LoggingInterleavingExecutionReporterForGeneration;
import de.rwth.swc.coffee4j.engine.process.report.interleaving.LoggingInterleavingExecutionReporter;
import de.rwth.swc.coffee4j.engine.report.DelegatingInterleavingExecutionReporter;
import de.rwth.swc.coffee4j.engine.report.DelegatingModelBasedArgumentConverter;
import de.rwth.swc.coffee4j.engine.report.InterleavingExecutionReporter;
import de.rwth.swc.coffee4j.engine.report.ModelBasedArgumentConverter;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.InterleavingCombinatorialTestManager;

import java.util.List;

/**
 * Default {@link InterleavingGenerationContext}
 */
public class InterleavingGenerationContext implements PhaseContext {
    
    private final ModelConverter modelConverter;
    final InterleavingCombinatorialTestManager interleavingCombinatorialTestManager;
    private final ExtensionExecutor extensionExecutor;
    private final boolean isGenerating;

    /**
     * Creates new {@link InterleavingGenerationContext}.
     *
     * @param configuration provides information for {@link ModelConverter}, {@link InterleavingCombinatorialTestConfiguration}
     *                      and {@link InterleavingCombinatorialTestManager}
     * @param model provides information for {@link ModelConverter}
     * @param extensionExecutor provides {@link ExtensionExecutor}
     */
    public InterleavingGenerationContext(InterleavingExecutionConfiguration configuration,
                                                InputParameterModel model,
                                                ExtensionExecutor extensionExecutor) {
        this.extensionExecutor = Preconditions.notNull(extensionExecutor);
        this.modelConverter = Preconditions.notNull(configuration).getModelConverterFactory().create(model);
        this.isGenerating = configuration.isGenerating();

        final ModelBasedArgumentConverter argumentConverterManager =
                new DelegatingModelBasedArgumentConverter(configuration.getArgumentConverters());

        argumentConverterManager.initialize(modelConverter);

        final InterleavingExecutionReporterToGenerationReporterAdapter reporterManager =
                new InterleavingExecutionReporterToGenerationReporterAdapter(
                     buildDelegatingOrDefaultExecutionReporter(configuration.getExecutionReporters()),
                     argumentConverterManager,
                     modelConverter
                );

        InterleavingCombinatorialTestConfiguration testConfiguration = new InterleavingCombinatorialTestConfiguration(
                configuration.getTestInputGenerationStrategyFactory(),
                configuration.getIdentificationStrategyFactory(),
                configuration.getFeedbackCheckingStrategyFactory(),
                configuration.getClassificationStrategyFactory(),
                configuration.getConstraintCheckerFactory(),
                reporterManager
        );

        this.interleavingCombinatorialTestManager = configuration.getManagerFactory()
                .create(testConfiguration, modelConverter.getConvertedModel());
    }

    protected InterleavingExecutionReporter buildDelegatingOrDefaultExecutionReporter(List<InterleavingExecutionReporter> executionReporters) {
        if(executionReporters.isEmpty()) {
            if (isGenerating) {
                return new LoggingInterleavingExecutionReporterForGeneration();
            } else {
                return new LoggingInterleavingExecutionReporter();
            }
        } else {
            return new DelegatingInterleavingExecutionReporter(executionReporters);
        }
    }

    public ModelConverter getModelConverter() {
        return modelConverter;
    }

    public InterleavingCombinatorialTestManager getTestManager() {
        return interleavingCombinatorialTestManager;
    }

    public ExtensionExecutor getExtensionExecutor() {
        return extensionExecutor;
    }
    
}
