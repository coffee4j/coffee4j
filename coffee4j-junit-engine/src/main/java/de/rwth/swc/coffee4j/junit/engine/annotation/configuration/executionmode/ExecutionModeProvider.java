package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.executionmode;

import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedProvider;

/**
 * An {@link ExecutionModeProvider} is responsible for providing exactly one {@link ExecutionMode} for use in a
 * {@link CombinatorialTest}.
 *
 * <p>Pre default, {@link CombinatorialTestAnnotationBasedExecutionModeProvider} is used. To register a custom
 * {@link ExecutionModeProvider}, use the {@link ExecutionModeSource} annotation (can be used as a meta annotation).
 *
 * <p>Implementations must provide a no-args constructor.
 *
 * <p>This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsProvider} from the
 * junit-jupiter-params project.
 */
@FunctionalInterface
public interface ExecutionModeProvider extends MethodBasedProvider<ExecutionMode> {
}
