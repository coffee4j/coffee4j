package de.rwth.swc.coffee4j.engine.configuration.execution;

import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.classification.NoOpClassificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.algorithmic.conflict.ConflictDetectionConfiguration;
import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.*;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.NoOpTestInputPrioritizer;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.Buildable;
import de.rwth.swc.coffee4j.engine.converter.model.IndexBasedModelConverter;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverterFactory;
import de.rwth.swc.coffee4j.engine.report.SequentialExecutionReporter;
import de.rwth.swc.coffee4j.engine.process.manager.sequential.CombinatorialTestManagerFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.ConstraintGeneratingSequentialCombinatorialTestManager;

import java.util.*;

/**
 * The complete reusable part of the configuration for a combinatorial test input. This means that multiple combinatorial
 * tests can be executed with the same {@link SequentialExecutionConfiguration}, as generally only
 * the testModel changes.
 * Includes a factory for creating a {@link SequentialCombinatorialTestManager},
 * {@link ModelConverter},
 * {@link FaultCharacterizationAlgorithm}s, generators for initial test inputs,
 * reporters and converters.
 */
public final class SequentialExecutionConfiguration {
    
    private final CombinatorialTestManagerFactory managerFactory;
    private final ModelConverterFactory modelConverterFactory;
    private final ConflictDetectionConfiguration conflictDetectionConfiguration;
    private final FaultCharacterizationAlgorithmFactory characterizationAlgorithmFactory;
    private final ClassificationStrategyFactory classificationStrategyFactory;
    private final List<TestInputGroupGenerator> generators;
    private final TestInputPrioritizer prioritizer;
    private final List<SequentialExecutionReporter> executionReporters;
    private final List<ArgumentConverter> argumentConverters;
    private final ExecutionMode executionMode;
    private final boolean isConstraintGenerator;
    
    private SequentialExecutionConfiguration(Builder builder) {
        managerFactory = builder.managerFactory;
        modelConverterFactory = Preconditions.notNull(builder.modelConverterFactory);
        conflictDetectionConfiguration = Preconditions.notNull(builder.conflictDetectionConfiguration);
        characterizationAlgorithmFactory = builder.characterizationAlgorithmFactory;
        classificationStrategyFactory = builder.classificationStrategyFactory;
        generators = builder.generators;
        prioritizer = builder.prioritizer;
        executionReporters = builder.executionReporters;
        argumentConverters = builder.argumentConverters;
        executionMode = Preconditions.notNull(builder.executionMode);
        isConstraintGenerator = builder.isConstraintGenerator;
    }
    
    /**
     * @return the factory used to create a new manager for a combinatorial test
     */
    public CombinatorialTestManagerFactory getManagerFactory() {
        return managerFactory;
    }
    
    /**
     * @return the factory used to create a new manager for an input parameter testModel
     */
    public ModelConverterFactory getModelConverterFactory() {
        return modelConverterFactory;
    }

    public ConflictDetectionConfiguration getConflictDetectionConfiguration()  {
        return conflictDetectionConfiguration;
    }

    /**
     * @return an optional containing the factory for creating new characterization algorithms if one is configured,
     * otherwise and empty {@link Optional} is returned
     */
    public Optional<FaultCharacterizationAlgorithmFactory> getCharacterizationAlgorithmFactory() {
        return Optional.ofNullable(characterizationAlgorithmFactory);
    }

    public ClassificationStrategyFactory getClassificationStrategyFactory() {
        return classificationStrategyFactory;
    }

    /**
     * @return all generators which should be used for generating initial test inputs. May be empty
     */
    public List<TestInputGroupGenerator> getGenerators() {
        return generators;
    }
    
    /**
     * @return the prioritizer which shall be used to prioritize the inputs generated by the
     *     {@link #getGenerators() generators}. Is never {@code null}
     */
    public TestInputPrioritizer getPrioritizer() {
        return prioritizer;
    }
    
    /**
     * @return all reporter for listening to interesting events during the generating and execution. May be empty
     */
    public List<SequentialExecutionReporter> getExecutionReporters() {
        return executionReporters;
    }
    
    /**
     * @return all argument converter for converting reports and identifiers for test input groups. May be empty
     */
    public List<ArgumentConverter> getArgumentConverters() {
        return argumentConverters;
    }
    
    /**
     * @return the execution mode for executing the test inputs in groups
     */
    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    /**
     * @return whether test is used for generating error-constraints
     */
    public boolean isConstraintGenerator() {
        return isConstraintGenerator;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final SequentialExecutionConfiguration other
                = (SequentialExecutionConfiguration) object;
        return Objects.equals(managerFactory, other.managerFactory) &&
                Objects.equals(modelConverterFactory, other.modelConverterFactory) &&
                Objects.equals(conflictDetectionConfiguration, other.conflictDetectionConfiguration) &&
                Objects.equals(characterizationAlgorithmFactory, other.characterizationAlgorithmFactory) &&
                Objects.equals(classificationStrategyFactory, other.classificationStrategyFactory) &&
                Objects.equals(generators, other.generators) &&
                Objects.equals(executionReporters, other.executionReporters) &&
                Objects.equals(argumentConverters, other.argumentConverters) &&
                Objects.equals(executionMode, other.executionMode) &&
                Objects.equals(isConstraintGenerator, other.isConstraintGenerator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managerFactory, modelConverterFactory, conflictDetectionConfiguration,
                characterizationAlgorithmFactory, classificationStrategyFactory, generators, executionReporters,
                argumentConverters, executionMode, isConstraintGenerator);
    }

    @Override
    public String toString() {
        return "CombinatorialTestExecutionConfiguration{" +
                "managerFactory=" + managerFactory +
                ", modelConverterFactory=" + modelConverterFactory +
                ", conflictDetectionConfiguration=" + conflictDetectionConfiguration +
                ", characterizationAlgorithmFactory=" + characterizationAlgorithmFactory +
                ", classificationStrategyFactory=" + classificationStrategyFactory +
                ", generators=" + generators +
                ", executionReporters=" + executionReporters +
                ", argumentConverters=" + argumentConverters +
                ", executionMode=" + executionMode +
                ", isConstraintGenerator=" + isConstraintGenerator +
                '}';
    }

    /**
     * Initializes the builder pattern for {@link SequentialExecutionConfiguration}
     *
     * @return a new {@link Builder}
     */
    public static Builder executionConfiguration() {
        return new Builder();
    }
    
    /**
     * Constructs a instance of the {@link Builder} with the values of this configuration. Can be used to extend the
     * configuration without rebuilding it from scratch.
     *
     * @return the instance with the same values as the current configuration
     */
    public Builder toBuilder() {
        return executionConfiguration()
                .managerFactory(managerFactory)
                .modelConverterFactory(modelConverterFactory)
                .faultCharacterizationAlgorithmFactory(characterizationAlgorithmFactory)
                .classificationStrategyFactory(classificationStrategyFactory)
                .conflictDetectionConfiguration(conflictDetectionConfiguration)
                .generators(generators)
                .prioritizer(prioritizer)
                .executionReporters(executionReporters)
                .argumentConverters(argumentConverters)
                .executionMode(executionMode)
                .isConstraintGenerator(isConstraintGenerator);
    }
    
    /**
     * The realization of the builder pattern for a quick and readable construction of a new configuration.
     */
    public static final class Builder implements
            Buildable<SequentialExecutionConfiguration> {
        
        private CombinatorialTestManagerFactory managerFactory = (configuration, generationReporter) -> new CachingDelegatingSequentialCombinatorialTestManager(new HashMapTestResultCache(), new BasicSequentialCombinatorialTestManager(configuration, generationReporter));
        private ModelConverterFactory modelConverterFactory = IndexBasedModelConverter::new;
        private FaultCharacterizationAlgorithmFactory characterizationAlgorithmFactory;
        private ClassificationStrategyFactory classificationStrategyFactory;
        private ConflictDetectionConfiguration conflictDetectionConfiguration = ConflictDetectionConfiguration.disable();
        private final List<TestInputGroupGenerator> generators = new ArrayList<>();
        private TestInputPrioritizer prioritizer = new NoOpTestInputPrioritizer();
        private final List<SequentialExecutionReporter> executionReporters = new ArrayList<>();
        private final List<ArgumentConverter> argumentConverters = new ArrayList<>();
        private ExecutionMode executionMode = ExecutionMode.EXECUTE_ALL;
        private boolean isConstraintGenerator = false;
        
        /**
         * Sets which factory shall be used to create new
         * {@link SequentialCombinatorialTestManager} instances. The default creates new ones
         * using a {@link CachingDelegatingSequentialCombinatorialTestManager} with a {@link HashMapTestResultCache} wrapped
         * around a {@link BasicSequentialCombinatorialTestManager}.
         *
         * @param managerFactory the factory for creating new managers. Must not be {@code null} when
         *                       {@link #build()} is called
         * @return this
         */
        public Builder managerFactory(CombinatorialTestManagerFactory managerFactory) {
            this.managerFactory = managerFactory;

            return this;
        }
        
        /**
         * Sets which factory shall be used to create new {@link ModelConverter}
         * instances. The default is a {@link IndexBasedModelConverter}.
         *
         * @param modelConverterFactory the factory for creating new converters. Must not be {@code null} when
         *                              {@link #build()} is called
         * @return this
         */
        public Builder modelConverterFactory(ModelConverterFactory modelConverterFactory) {
            this.modelConverterFactory = modelConverterFactory;
            
            return this;
        }
        
        /**
         * Sets which factory shall be used to create new
         * {@link FaultCharacterizationAlgorithm} instances during combinatorial
         * testing. The default value is {@code null}, which means no fault characterization will be used.
         *
         * @param characterizationAlgorithmFactory the factory for creating new converters. Can be {@code null} when calling
         *                                         {@link #build()} to indicate that no fault characterization is used
         * @return this
         */
        public Builder faultCharacterizationAlgorithmFactory(FaultCharacterizationAlgorithmFactory characterizationAlgorithmFactory) {
            this.characterizationAlgorithmFactory = characterizationAlgorithmFactory;
            
            return this;
        }

        public Builder classificationStrategyFactory(ClassificationStrategyFactory classificationStrategyFactory) {
            this.classificationStrategyFactory = classificationStrategyFactory;

            return this;
        }


        /**
         * Sets the {@link ConflictDetectionConfiguration}
         *
         * @param constraintDiagnosisEnabled the {@link ConflictDetectionConfiguration} to use
         * @return this {@link Builder}
         */
        public Builder conflictDetectionConfiguration(ConflictDetectionConfiguration constraintDiagnosisEnabled) {
            this.conflictDetectionConfiguration = constraintDiagnosisEnabled;

            return this;
        }

        /**
         * Adds one execution reporter to listen to important events during combinatorial test execution.
         *
         * @param executionReporter the reporter to be added. Must not be {@code null}
         * @return this
         */
        public Builder executionReporter(SequentialExecutionReporter executionReporter) {
            executionReporters.add(Preconditions.notNull(executionReporter));
            
            return this;
        }
        
        /**
         * Adds all execution reports to listen to important events during combinatorial test execution.
         *
         * @param executionReporters the reporters to be added. Must not be, nor contain {@code null}
         * @return this
         */
        public Builder executionReporters(SequentialExecutionReporter... executionReporters) {
            Preconditions.notNull(executionReporters);
            
            for (SequentialExecutionReporter executionReporter : executionReporters) {
                this.executionReporters.add(Preconditions.notNull(executionReporter));
            }
            
            return this;
        }
        
        /**
         * Adds all execution reporters to listen to important events during combinatorial test execution.
         *
         * @param executionReporters the reporters to be added. Must not be, nor contain {@code null}
         * @return this
         */
        public Builder executionReporters(Collection<? extends SequentialExecutionReporter> executionReporters) {
            Preconditions.notNull(executionReporters);
            
            this.executionReporters.addAll(executionReporters);
            
            return this;
        }
        
        /**
         * Adds the argument converter to convert report arguments from engine to testModel representations.
         *
         * @param argumentConverter the converter to be added. Must not be {@code null}
         * @return this
         */
        public Builder argumentConverter(ArgumentConverter argumentConverter) {
            argumentConverters.add(Preconditions.notNull(argumentConverter));
            
            return this;
        }
        
        /**
         * Adds the argument converters to convert report arguments from engine to testModel representations.
         *
         * @param argumentConverters the converters to be added. Must not be, nor contain {@code null}
         * @return this
         */
        public Builder argumentConverters(ArgumentConverter... argumentConverters) {
            Preconditions.notNull(argumentConverters);
            
            for (ArgumentConverter argumentConverter : argumentConverters) {
                this.argumentConverters.add(Preconditions.notNull(argumentConverter));
            }
            
            return this;
        }
        
        /**
         * Adds the argument converters to convert report arguments from engine to testModel representations.
         *
         * @param argumentConverters the converters to be added. Must not be, nor contain {@code null}
         * @return this
         */
        public Builder argumentConverters(Collection<ArgumentConverter> argumentConverters) {
            Preconditions.notNull(argumentConverters);
            
            this.argumentConverters.addAll(argumentConverters);
            
            return this;
        }
        
        /**
         * Adds one generator for initial {@link TestInputGroup} generation.
         *
         * @param generator the generator to be added. Must not be {@code null}
         * @return this
         */
        public Builder generator(TestInputGroupGenerator generator) {
            generators.add(Preconditions.notNull(generator));
            
            return this;
        }
        
        /**
         * Adds all generators for initial {@link TestInputGroup} generation.
         *
         * @param generators the generators to be added. Must not be, nor contain {@code null}
         * @return this
         */
        public Builder generators(TestInputGroupGenerator... generators) {
            Preconditions.notNull(generators);
            
            for (TestInputGroupGenerator generator : generators) {
                this.generators.add(Preconditions.notNull(generator));
            }
            
            return this;
        }
    
        /**
         * Sets the {@link TestInputPrioritizer} which shall be used to prioritize the inputs of the individual
         * {@link TestInputGroup TestInputGroups}. The default is {@link NoOpTestInputPrioritizer}.
         *
         * @param prioritizer the prioritizer to use. Must not be {@code null}
         * @return this
         */
        public Builder prioritizer(TestInputPrioritizer prioritizer) {
            Preconditions.notNull(prioritizer);
            
            this.prioritizer = prioritizer;
            
            return this;
        }
        
        /**
         * Adds all generators for initial {@link TestInputGroup} generation.
         *
         * @param generators the generators to be added. Must not be, nor contain {@code null}
         * @return this
         */
        public Builder generators(Collection<TestInputGroupGenerator> generators) {
            Preconditions.notNull(generators);
            
            this.generators.addAll(generators);
            
            return this;
        }
    
        /**
         * Sets the execution mode for the sequential combinatorial test configuration.
         *
         * @param executionMode the mode to use. The default is {@link ExecutionMode#EXECUTE_ALL}.
         *     Must not be {@code null}
         * @return this
         */
        public Builder executionMode(ExecutionMode executionMode) {
            this.executionMode = Preconditions.notNull(executionMode);
            
            return this;
        }

        /**
         * Sets a flag whether the combinatorial test is used for generating parameter error-constraints.
         * @param flag true iff test is used for generating error-constraints
         * @return this
         */
        public Builder isConstraintGenerator(boolean flag) {
            this.isConstraintGenerator = flag;

            if (flag) {
                // set default value
                if (this.classificationStrategyFactory == null) {
                    this.classificationStrategyFactory = NoOpClassificationStrategy.noOpClassificationStrategy();
                }

                this.managerFactory = (configuration, generationReporter) -> new CachingDelegatingSequentialCombinatorialTestManager(new HashMapTestResultCache(), new ConstraintGeneratingSequentialCombinatorialTestManager(configuration, generationReporter));

            }

            return this;
        }
        
        /**
         * Creates a new configuration based on the supplied values.
         * The {@link #managerFactory(CombinatorialTestManagerFactory)} and
         * {@link #modelConverterFactory(ModelConverterFactory)} must not be {@code null}. If they are not used,
         * they will have the non-{@code null} default values defined at the methods.
         *
         * @return the new configuration
         */
        public SequentialExecutionConfiguration build() {
            return new SequentialExecutionConfiguration(this);
        }
        
    }
    
}
