package io.github.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration;

import io.github.coffee4j.junit.engine.annotation.configuration.converter.ConverterLoader;
import io.github.coffee4j.junit.engine.annotation.configuration.diagnosis.ConflictDetectionConfigurationLoader;
import io.github.coffee4j.junit.engine.annotation.configuration.executionmode.ExecutionModeLoader;
import io.github.coffee4j.junit.engine.annotation.configuration.reporter.ReporterLoader;
import io.github.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.DelegatingConfigurationProvider;
import io.github.coffee4j.junit.engine.annotation.configuration.classification.ClassificationStrategyFactoryLoader;
import io.github.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import io.github.coffee4j.algorithmic.interleaving.feedback.DefaultFeedbackCheckingStrategy;
import io.github.coffee4j.algorithmic.interleaving.generator.aetg.AetgStrategy;
import io.github.coffee4j.algorithmic.interleaving.identification.trt.TupleRelationshipStrategy;
import io.github.coffee4j.engine.process.report.interleaving.LoggingInterleavingExecutionReporter;
import io.github.coffee4j.engine.process.report.interleaving.LoggingInterleavingExecutionReporterForGeneration;
import io.github.coffee4j.engine.report.InterleavingExecutionReporter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides a new {@link InterleavingExecutionConfiguration} based on further providers and sources
 * which can be registered using annotations.
 * <p>
 *     Copy of {@link DelegatingConfigurationProvider} for interleaving combinatorial testing.
 * </p>
 */
public class DelegatingInterleavingConfigurationProvider implements InterleavingConfigurationProvider {

    private final boolean isGeneratingConfigurationNeeded;

    /**
     * @param isGeneratingConfigurationNeeded indicates whether a configuration for error-constraint generation is needed
     *                                        or not.
     */
    public DelegatingInterleavingConfigurationProvider(boolean isGeneratingConfigurationNeeded) {
        this.isGeneratingConfigurationNeeded = isGeneratingConfigurationNeeded;
    }
    
    @Override
    public InterleavingExecutionConfiguration provide(Method method) {
        InterleavingExecutionConfiguration.Builder executionConfigurationBuilder = isGeneratingConfigurationNeeded
                ? InterleavingExecutionConfiguration.generatingExecutionConfiguration()
                : InterleavingExecutionConfiguration.executionConfiguration();

        List<InterleavingExecutionReporter> reporters = new ReporterLoader().load(method).stream()
                .map(InterleavingExecutionReporter.class::cast)
                .collect(Collectors.toList());

        if (reporters.isEmpty()) {
            if (isGeneratingConfigurationNeeded){
                reporters.add(new LoggingInterleavingExecutionReporterForGeneration());
            } else {
                reporters.add(new LoggingInterleavingExecutionReporter());
            }
        }

        executionConfigurationBuilder
                .argumentConverters(new ConverterLoader().load(method))
                .conflictDetectionConfiguration(new ConflictDetectionConfigurationLoader().load(method))
                .executionReporters(reporters)
                .testInputGenerationStrategyFactory(AetgStrategy.aetgStrategy())
                .identificationStrategyFactory(TupleRelationshipStrategy.tupleRelationshipStrategy())
                .feedbackCheckingStrategyFactory(DefaultFeedbackCheckingStrategy.defaultCheckingStrategy())
                .classificationStrategyFactory(new ClassificationStrategyFactoryLoader().load(method))
                .executionMode(new ExecutionModeLoader().load(method));

        return executionConfigurationBuilder.build();
    }
}
