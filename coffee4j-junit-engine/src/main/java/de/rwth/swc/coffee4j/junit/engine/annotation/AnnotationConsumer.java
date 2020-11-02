package de.rwth.swc.coffee4j.junit.engine.annotation;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

/**
 * Represents an operation, that accepts an {@link Annotation}
 *
 * This is more or less a copy of {@code org.junit.jupiter.params.support.AnnotationConsumer} from the
 * junit-jupiter-params project.
 * @param <A> the type of the {@link Annotation}
 */
@FunctionalInterface
public interface AnnotationConsumer<A extends Annotation> extends Consumer<A> {
}
