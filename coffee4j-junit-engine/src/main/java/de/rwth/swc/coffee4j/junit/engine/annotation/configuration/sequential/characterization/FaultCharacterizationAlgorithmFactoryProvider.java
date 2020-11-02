package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization;

import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedProvider;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;

/**
 * An {@code FaultCharacterizationAlgorithmFactoryProvider} is responsible for {@linkplain #provide providing}
 * exactly one{@link FaultCharacterizationAlgorithmFactory} for use in a
 * {@link CombinatorialTest}.
 * <p>
 * To register a {@link FaultCharacterizationAlgorithmFactoryProvider}, use the {@link FaultCharacterizationAlgorithmFactorySource}
 * annotation as demonstrated by {@link EnableFaultCharacterization}.
 * <p>
 * Implementations must provide a no-args constructor.
 * <p>
 * This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsProvider} from the
 * junit-jupiter-params project.
 */
@FunctionalInterface
public interface FaultCharacterizationAlgorithmFactoryProvider extends
        MethodBasedProvider<FaultCharacterizationAlgorithmFactory> {
}
