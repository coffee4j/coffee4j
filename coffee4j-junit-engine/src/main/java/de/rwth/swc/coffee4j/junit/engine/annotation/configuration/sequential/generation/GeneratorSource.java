package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation;

import java.lang.annotation.*;

/**
 * {@code GeneratorSource} is an annotation used to register
 * {@linkplain GeneratorProvider generator providers} for the annotated test method.
 * <p>
 * This may also be used as a meta-annotation in order to create a custom composed annotation that inherits the
 * semantics of {@code GeneratorSource} (demonstrated by {@link EnableGeneration}).
 * <p>
 * This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsSource} from the
 * junit-jupiter-params project.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratorSource {

    /**
     * Gets the class of the {@link GeneratorProvider}
     *
     * @return the class which provides {@link de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator}s. Must
     * have a no-args constructor
     */
    Class<? extends GeneratorProvider> value();
}
