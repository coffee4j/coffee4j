package de.rwth.swc.coffee4j.junit.engine.annotation.extension;

import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedProvider;

import java.util.List;

/**
 * An {@code ExtensionProvider} is responsible for {@linkplain #provide providing}
 * an arbitrary number of {@link Extension} implementations (even none is allowed) for a
 * {@link CombinatorialTest}.
 *
 * <p>To register a {@link ExtensionProvider}, use the {@link ExtensionSource}
 * annotation as demonstrated by {@link EnableExtension}.
 *
 * <p>Implementations must provide a no-args constructor.
 *
 * <p>This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsProvider} from the
 * junit-jupiter-params project.
 */
public interface ExtensionProvider extends MethodBasedProvider<List<Extension>> {
}
