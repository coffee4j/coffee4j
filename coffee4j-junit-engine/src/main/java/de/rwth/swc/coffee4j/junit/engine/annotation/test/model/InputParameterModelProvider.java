package de.rwth.swc.coffee4j.junit.engine.annotation.test.model;

import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedProvider;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;

/**
 * An {@link InputParameterModelProvider} is responsible for {@linkplain #provide providing} exactly one
 * {@link InputParameterModel} for use in a {@link CombinatorialTest}.
 *
 * <p>Per default, {@link MethodBasedInputParameterModelProvider} is used.
 * To register a custom {@link InputParameterModelProvider}, use the {@link InputParameterModelSource} annotation.
 *
 * <p>Implementations must provide a no-args constructor.
 *
 * <p>This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsProvider} from the
 * junit-jupiter-params project.
 */
@FunctionalInterface
public interface InputParameterModelProvider extends MethodBasedProvider<InputParameterModel> {
}
