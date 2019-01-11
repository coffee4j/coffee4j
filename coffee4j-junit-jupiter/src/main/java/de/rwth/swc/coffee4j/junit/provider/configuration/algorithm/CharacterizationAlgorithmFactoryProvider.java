package de.rwth.swc.coffee4j.junit.provider.configuration.algorithm;

import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.junit.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.provider.ExtensionContextBasedProvider;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * An {@code CharacterizationAlgorithmFactoryProvider} is responsible for {@linkplain #provide(ExtensionContext) providing}
 * exactly one{@link FaultCharacterizationAlgorithmFactory} for use in a
 * {@link CombinatorialTest}.
 * <p>
 * To register a {@link CharacterizationAlgorithmFactoryProvider}, use the {@link CharacterizationAlgorithmFactorySource}
 * annotation as demonstrated by {@link CharacterizationAlgorithm}.
 * <p>
 * Implementations must provide a no-args constructor.
 * <p>
 * This is more or less a copy of {@link org.junit.jupiter.params.provider.ArgumentsProvider} from the
 * junit-jupiter-params project.
 */
@FunctionalInterface
public interface CharacterizationAlgorithmFactoryProvider extends ExtensionContextBasedProvider<FaultCharacterizationAlgorithmFactory> {
}
