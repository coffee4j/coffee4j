package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.converter;

import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedProvider;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;

import java.util.Collection;

/**
 * An {@code ConverterProvider} is responsible for {@linkplain #provide providing}
 * exactly an arbitrary number of {@link ArgumentConverter} implementations (even none is allowed) for a
 * {@link CombinatorialTest}.
 * <p>
 * To register a {@link ConverterProvider}, use the {@link ConverterSource}
 * annotation as demonstrated by {@link EnableConverter}.
 * <p>
 * Implementations must provide a no-args constructor.
 * <p>
 * This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsProvider} from the
 * junit-jupiter-params project.
 */
@FunctionalInterface
public interface ConverterProvider extends MethodBasedProvider<Collection<ArgumentConverter>> {
}
