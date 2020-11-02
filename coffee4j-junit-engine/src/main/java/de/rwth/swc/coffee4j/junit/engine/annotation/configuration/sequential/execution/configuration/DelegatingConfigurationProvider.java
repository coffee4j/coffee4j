package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration;

import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.CachingDelegatingSequentialCombinatorialTestManager;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.ConstraintGeneratingSequentialCombinatorialTestManager;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.HashMapTestResultCache;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.classification.ClassificationStrategyFactoryLoader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.sequential.EnableSequentialConstraintGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.sequential.GeneratingFaultCharacterizationAlgorithmFactoryLoader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.converter.ConverterLoader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.diagnosis.ConflictDetectionConfigurationLoader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.executionmode.ExecutionModeLoader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.reporter.ReporterLoader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization.FaultCharacterizationAlgorithmFactoryLoader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.GeneratorLoader;
import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import de.rwth.swc.coffee4j.engine.report.SequentialExecutionReporter;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.prioritization.TestInputPrioritizerLoader;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

import static de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration.executionConfiguration;

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
