package de.rwth.swc.coffee4j.model.manager;

import de.rwth.swc.coffee4j.engine.TestResult;
import de.rwth.swc.coffee4j.engine.manager.CombinatorialTestConfiguration;
import de.rwth.swc.coffee4j.engine.manager.CombinatorialTestManager;
import de.rwth.swc.coffee4j.engine.util.Preconditions;
import de.rwth.swc.coffee4j.model.Combination;
import de.rwth.swc.coffee4j.model.InputParameterModel;
import de.rwth.swc.coffee4j.model.converter.ModelConverter;
import de.rwth.swc.coffee4j.model.converter.ModelConverterFactory;
import de.rwth.swc.coffee4j.model.report.ModelBasedArgumentConverter;

import java.util.function.Consumer;

/**
 * A manager for converting test inputs using a {@link ModelConverter} and putting them into a provided consumer.
 * Uses a {@link DelegatingModelBasedArgumentConverter} and {@link DelegatingExecutionReporter}
 * and {@link ExecutionReporterToGenerationReporterAdapter} to convert between different representations and allow multiple converts
 * and reporter even though method in engine only accept one.
 */
public class CombinatorialTestConsumerManager {
    
    private final ModelConverter modelConverter;
    
    private final CombinatorialTestManager generator;
    
    private final Consumer<Combination> testInputConsumer;
    
    /**
     * Creates a new manager with the given configuration, consumer and model.
     *
     * @param configuration     all needed configuration for a combinatorial test. This is the part which can be reused
     *                          across different tests. Must not be {@code null}
     * @param testInputConsumer a consumer for all test inputs generated by the {@link CombinatorialTestManager}
     *                          provided by the configuration. Each test input is converted using a {@link ModelConverter}
     *                          generated by the provided
     *                          {@link ModelConverterFactory}.
     *                          This part is generally not reusable. Must not be {@code null}
     * @param model             the model which defines all parameters and constraints for a combinatorial test. This part
     *                          is generally not reusable. Must not be {@code null}
     */
    public CombinatorialTestConsumerManager(CombinatorialTestConsumerManagerConfiguration configuration, Consumer<Combination> testInputConsumer, InputParameterModel model) {
        Preconditions.notNull(configuration);
        Preconditions.notNull(testInputConsumer);
        Preconditions.notNull(model);
        
        this.testInputConsumer = testInputConsumer;
        modelConverter = configuration.getModelConverterFactory().create(model);
        
        final ModelBasedArgumentConverter argumentConverterManager = new DelegatingModelBasedArgumentConverter(configuration.getArgumentConverters());
        argumentConverterManager.initialize(modelConverter);
        final ExecutionReporterToGenerationReporterAdapter reporterManager = new ExecutionReporterToGenerationReporterAdapter(new DelegatingExecutionReporter(configuration.getExecutionReporters()), argumentConverterManager, modelConverter);
        new DelegatingExecutionReporter(configuration.getExecutionReporters());
        final CombinatorialTestConfiguration managerConfiguration = new CombinatorialTestConfiguration(configuration.getCharacterizationAlgorithmFactory().orElse(null), configuration.getGenerators(), reporterManager);
        generator = configuration.getManagerFactory().apply(managerConfiguration, modelConverter.getConvertedModel());
    }
    
    /**
     * Generates the initial test inputs, converts them and propagates them to the consumer given in the constructor.
     */
    public synchronized void generateInitialTests() {
        generator.generateInitialTests().stream().map(modelConverter::convertCombination).forEach(testInputConsumer);
    }
    
    /**
     * Generates additional test inputs based on a new test result. All returned test inputs are converted and then
     * propagated to the given consumer.
     *
     * @param testInput  a test input. Must not be {@code null}
     * @param testResult the result of the test input. Must not be {@code null}
     */
    public synchronized void generateAdditionalTestInputsWithResult(Combination testInput, TestResult testResult) {
        Preconditions.notNull(testInput);
        Preconditions.notNull(testResult);
        
        generator.generateAdditionalTestInputsWithResult(modelConverter.convertCombination(testInput), testResult).stream().map(modelConverter::convertCombination).forEach(testInputConsumer);
    }
    
}