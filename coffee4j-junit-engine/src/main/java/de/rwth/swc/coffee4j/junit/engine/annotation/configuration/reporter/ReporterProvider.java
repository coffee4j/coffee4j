package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.reporter;

import de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedProvider;
import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;

import java.util.Collection;

/**
 * An {@code ReporterProvider} is responsible for {@linkplain #provide providing}
 * an arbitrary number of {@link ExecutionReporter} implementations (even none is allowed) for a
 * {@link CombinatorialTest}.
 * <p>
 * To register a {@link ReporterProvider}, use the {@link ReporterSource}
 * annotation as demonstrated by {@link EnableReporter}.
 * <p>
 * Implementations must provide a no-args constructor.
 * <p>
 * This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsProvider} from the
 * junit-jupiter-params project.
 */
@FunctionalInterface
public interface ReporterProvider extends MethodBasedProvider<Collection<ExecutionReporter>> {
}
