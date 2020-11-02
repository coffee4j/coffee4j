package de.rwth.swc.coffee4j.junit.engine.annotation.extension;

import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code ExtensionSource} is an annotation used to register
 * {@linkplain ExtensionProvider extension providers} for the annotated test class.
 *
 * <p>This may also be used as a meta-annotation in order to create a custom composed annotation that inherits the
 * semantics of {@code ExtensionSource} (demonstrated by {@link EnableExtension}).
 *
 * <p>This is more or less a copy of {@code org.junit.jupiter.params.provider.ArgumentsSource} from the
 * junit-jupiter-params project.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ExtensionSources.class)
public @interface ExtensionSource {
    
    /**
     * Gets the class of the {@link ExtensionProvider}
     *
     * @return the class which provides an {@link Extension}. Must have a no-args constructor
     */
    Class<? extends ExtensionProvider> value();
    
}
