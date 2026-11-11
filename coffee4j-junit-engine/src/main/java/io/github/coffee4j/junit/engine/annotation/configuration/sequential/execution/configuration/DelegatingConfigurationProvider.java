package io.github.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration;

import io.github.coffee4j.algorithmic.report.ArgumentConverter;
import io.github.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import io.github.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import io.github.coffee4j.algorithmic.sequential.manager.CachingDelegatingSequentialCombinatorialTestManager;
import io.github.coffee4j.algorithmic.sequential.manager.ConstraintGeneratingSequentialCombinatorialTestManager;
import io.github.coffee4j.algorithmic.sequential.manager.HashMapTestResultCache;
import io.github.coffee4j.junit.engine.annotation.configuration.classification.ClassificationStrategyFactoryLoader;
import io.github.coffee4j.junit.engine.annotation.configuration.constraintgenerator.sequential.EnableSequentialConstraintGeneration;
import io.github.coffee4j.junit.engine.annotation.configuration.constraintgenerator.sequential.GeneratingFaultCharacterizationAlgorithmFactoryLoader;
import io.github.coffee4j.junit.engine.annotation.configuration.converter.ConverterLoader;
import io.github.coffee4j.junit.engine.annotation.configuration.diagnosis.ConflictDetectionConfigurationLoader;
import io.github.coffee4j.junit.engine.annotation.configuration.executionmode.ExecutionModeLoader;
import io.github.coffee4j.junit.engine.annotation.configuration.reporter.ReporterLoader;
import io.github.coffee4j.junit.engine.annotation.configuration.sequential.characterization.FaultCharacterizationAlgorithmFactoryLoader;
import io.github.coffee4j.junit.engine.annotation.configuration.sequential.generation.GeneratorLoader;
import io.github.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import io.github.coffee4j.engine.report.SequentialExecutionReporter;
import io.github.coffee4j.junit.engine.annotation.CombinatorialTest;
import io.github.coffee4j.junit.engine.annotation.configuration.sequential.prioritization.TestInputPrioritizerLoader;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

import static io.github.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration.executionConfiguration;

/**
 * Provides a new {@link SequentialExecutionConfiguration} based on further providers and sources
 * which can be registered using annotations. Specifically, this provider lets you configure any
 * {@link GeneratorLoader} for loader all {@link TestInputGroupGenerator},
 * {@link FaultCharacterizationAlgorithmFactoryLoader} to load a
 * <p>
 * {@link FaultCharacterizationAlgorithmFactory},
 * {@link ConverterLoader} to add {@link ArgumentConverter} to the default ones,
 * and {@link ReporterLoader} to register custom {@link SequentialExecutionReporter ExecutionReporters}
 * which listen during {@link CombinatorialTest} execution and provide valuable feedback.
 */
public class DelegatingConfigurationProvider implements ConfigurationProvider {
    
    @Override
    public SequentialExecutionConfiguration provide(Method method) {
        SequentialExecutionConfiguration.Builder executionConfigurationBuilder = executionConfiguration();

        executionConfigurationBuilder
                .generators(new GeneratorLoader().load(method))
                .prioritizer(new TestInputPrioritizerLoader().load(method))
                .conflictDetectionConfiguration(new ConflictDetectionConfigurationLoader().load(method))
                .argumentConverters(new ConverterLoader().load(method))
                .classificationStrategyFactory(new ClassificationStrategyFactoryLoader().load(method))
                .executionReporters(new ReporterLoader().load(method).stream()
                        .map(reporter -> (SequentialExecutionReporter) reporter)
                        .collect(Collectors.toList()))
                .executionMode(new ExecutionModeLoader().load(method));

        if (AnnotationSupport.isAnnotated(method, EnableSequentialConstraintGeneration.class)) {
            executionConfigurationBuilder
                    .faultCharacterizationAlgorithmFactory(new GeneratingFaultCharacterizationAlgorithmFactoryLoader().load(method))
                    .isConstraintGenerator(true)
                    .managerFactory(((configuration, generationReporter) -> new CachingDelegatingSequentialCombinatorialTestManager(
                        new HashMapTestResultCache(),
                        new ConstraintGeneratingSequentialCombinatorialTestManager(configuration, generationReporter))));
        } else {
            executionConfigurationBuilder
                    .faultCharacterizationAlgorithmFactory(
                            new FaultCharacterizationAlgorithmFactoryLoader().load(method)
                                    .orElse(null));
        }

        return executionConfigurationBuilder.build();
    }
}
