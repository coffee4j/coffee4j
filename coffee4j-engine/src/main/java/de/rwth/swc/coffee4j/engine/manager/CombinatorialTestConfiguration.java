package de.rwth.swc.coffee4j.engine.manager;

import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.engine.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.engine.report.GenerationReporter;
import de.rwth.swc.coffee4j.engine.util.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * All configuration needed for an {@link CombinatorialTestManager} to generate test inputs for a given model.
 */
public final class CombinatorialTestConfiguration {
    
    private final FaultCharacterizationAlgorithmFactory faultCharacterizationAlgorithmFactory;
    
    private final List<TestInputGroupGenerator> generators;
    
    private final GenerationReporter generationReporter;
    
    /**
     * Creates a new configuration with the given arguments.
     *
     * @param faultCharacterizationAlgorithmFactory the factory creating fault characterization to be used for a
     *                                              combinatorialtest. Can be {@code null}
     * @param generators                            All generators which should be used for test input generation. This cannot be {@code null},
     *                                              but an empty collection is allowed
     * @param generationReporter                    the generation reporter for notification of important events in a combinatorial test.
     *                                              Can be {@code null}
     */
    public CombinatorialTestConfiguration(FaultCharacterizationAlgorithmFactory faultCharacterizationAlgorithmFactory, Collection<TestInputGroupGenerator> generators, GenerationReporter generationReporter) {
        Preconditions.notNull(generators);
        Preconditions.check(!generators.contains(null));
        
        this.faultCharacterizationAlgorithmFactory = faultCharacterizationAlgorithmFactory;
        this.generators = new ArrayList<>(generators);
        this.generationReporter = generationReporter;
    }
    
    /**
     * @return an {@link Optional} if a factory was given in the constructor, otherwise an empty optional
     */
    public Optional<FaultCharacterizationAlgorithmFactory> getFaultCharacterizationAlgorithmFactory() {
        return Optional.ofNullable(faultCharacterizationAlgorithmFactory);
    }
    
    /**
     * @return an unmodifiable list of all generates which should be used
     */
    public List<TestInputGroupGenerator> getGenerators() {
        return Collections.unmodifiableList(generators);
    }
    
    /**
     * @return an {@link Optional} containing a reporter if one was given in the constructor, or an empty one otherwise
     */
    public Optional<GenerationReporter> getGenerationReporter() {
        return Optional.ofNullable(generationReporter);
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final CombinatorialTestConfiguration other = (CombinatorialTestConfiguration) object;
        return Objects.equals(generationReporter, other.generationReporter) && Objects.equals(faultCharacterizationAlgorithmFactory, other.faultCharacterizationAlgorithmFactory) && Objects.equals(generators, other.generators);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(generationReporter, faultCharacterizationAlgorithmFactory, generators);
    }
    
    @Override
    public String toString() {
        return "CombinatorialTestConfiguration{" + "faultCharacterizationAlgorithmFactory=" + faultCharacterizationAlgorithmFactory + ", generators=" + generators + ", generationReporter=" + generationReporter + '}';
    }
    
}
