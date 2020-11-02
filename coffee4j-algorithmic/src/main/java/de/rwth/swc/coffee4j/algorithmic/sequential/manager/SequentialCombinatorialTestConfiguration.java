package de.rwth.swc.coffee4j.algorithmic.sequential.manager;

import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.NoOpTestInputPrioritizer;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;
import de.rwth.swc.coffee4j.algorithmic.sequential.report.GenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;

import java.util.*;

/**
 * All configuration needed for an {@link SequentialCombinatorialTestManager} to generate test inputs for a given testModel.
 */
public final class SequentialCombinatorialTestConfiguration {
    
    private final FaultCharacterizationAlgorithmFactory faultCharacterizationAlgorithmFactory;
    private final ClassificationStrategyFactory classificationStrategyFactory;
    
    private final List<TestInputGroupGenerator> generators;
    
    private final TestInputPrioritizer prioritizer;

    private final GenerationReporter generationReporter;
    
    private final ExecutionMode executionMode;

    /**
     * Creates a new configuration with the given arguments.
     *  @param faultCharacterizationAlgorithmFactory the factory creating fault characterization to be used for a
     *     combinatorial test. Can be {@code null}
     * @param classificationStrategyFactory {@link ClassificationStrategyFactory} creating the classification
     *     strategy for possibly exception-inducing combinations. Can be {@code null}.
     * @param generators All generators which should be used for test input generation. This cannot be {@code null},
     *     but an empty collection is allowed
     * @param prioritizer a prioritizer for ordering the test inputs generated in the initial generation phase.
     *     May be {@code null}. In that case a {@link NoOpTestInputPrioritizer} is used
     * @param generationReporter the generation reporter for notification of important events in a combinatorial test.
     *     Can be {@code null}
     * @param executionMode the execution mode for the initial test inputs
     */
    public SequentialCombinatorialTestConfiguration(
            FaultCharacterizationAlgorithmFactory faultCharacterizationAlgorithmFactory,
            ClassificationStrategyFactory classificationStrategyFactory,
            Collection<TestInputGroupGenerator> generators,
            TestInputPrioritizer prioritizer,
            GenerationReporter generationReporter,
            ExecutionMode executionMode) {
        
        Preconditions.notNull(generators);
        
        this.faultCharacterizationAlgorithmFactory = faultCharacterizationAlgorithmFactory;
        this.classificationStrategyFactory = classificationStrategyFactory;
        this.generators = new ArrayList<>(generators);
        this.prioritizer = prioritizer == null ? new NoOpTestInputPrioritizer() : prioritizer;
        this.generationReporter = generationReporter;
        this.executionMode = executionMode == null ? ExecutionMode.EXECUTE_ALL : executionMode;
    }
    
    /**
     * @return an {@link Optional} if a factory was given in the constructor, otherwise an empty optional
     */
    public Optional<FaultCharacterizationAlgorithmFactory> getFaultCharacterizationAlgorithmFactory() {
        return Optional.ofNullable(faultCharacterizationAlgorithmFactory);
    }

    public Optional<ClassificationStrategyFactory> getClassificationStrategyFactory() {
        return Optional.ofNullable(classificationStrategyFactory);
    }

    /**
     * @return an unmodifiable list of all generates which should be used
     */
    public List<TestInputGroupGenerator> getGenerators() {
        return Collections.unmodifiableList(generators);
    }
    
    public TestInputPrioritizer getPrioritizer() {
        return prioritizer;
    }
    
    /**
     * @return an {@link Optional} containing a reporter if one was given in the constructor, or an empty one otherwise
     */
    public Optional<GenerationReporter> getGenerationReporter() {
        return Optional.ofNullable(generationReporter);
    }
    
    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()){
            return false;
        }
        
        final SequentialCombinatorialTestConfiguration other = (SequentialCombinatorialTestConfiguration) object;
        return Objects.equals(faultCharacterizationAlgorithmFactory, other.faultCharacterizationAlgorithmFactory)
                && classificationStrategyFactory.equals(other.classificationStrategyFactory)
                && generators.equals(other.generators)
                && prioritizer.equals(other.prioritizer)
                && generationReporter.equals(other.generationReporter)
                && executionMode.equals(other.executionMode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(faultCharacterizationAlgorithmFactory, classificationStrategyFactory, generators,
                prioritizer, generationReporter, executionMode);
    }

    @Override
    public String toString() {
        return "CombinatorialTestConfiguration{" +
                "faultCharacterizationAlgorithmFactory=" + faultCharacterizationAlgorithmFactory +
                "classificationStrategyFactory=" + classificationStrategyFactory +
                ", generators=" + generators +
                ", prioritizer=" + prioritizer +
                ", generationReporter=" + generationReporter +
                ", executionMode=" + executionMode +
                '}';
    }
    
}
