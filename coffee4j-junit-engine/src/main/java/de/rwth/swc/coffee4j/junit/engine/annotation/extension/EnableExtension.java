package de.rwth.swc.coffee4j.junit.engine.annotation.extension;

import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Extends the {@link CombinatorialTest}
 * with supplied {@link Extension extensions}
 * <p>
 *     Extension must be instantiable with a public no-args constructor and must be static if nested.
 * </p>
 * <p>
 * This is more or less a copy of {@code org.junit.jupiter.api.extension.ExtendWith} from the
 * junit-jupiter-api project
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtensionSource(ConstructorBasedExtensionProvider.class)
public @interface EnableExtension {
    
    /**
     * Gets  the classes of the {@link Extension extensions}.
     *
     *
     * @return the classes of the {@link Extension extensions} to be used with the
     *     {@link CombinatorialTest}
     */
    Class<? extends Extension>[] value();
    
}
