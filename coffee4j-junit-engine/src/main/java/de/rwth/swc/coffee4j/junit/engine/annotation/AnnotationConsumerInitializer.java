package de.rwth.swc.coffee4j.junit.engine.annotation;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import static org.junit.platform.commons.support.HierarchyTraversalMode.BOTTOM_UP;
import static org.junit.platform.commons.support.ReflectionSupport.findMethods;

/**
 * Support class for initializing instances with annotations of an annotated element in
 * {@link #initialize(AnnotatedElement, Object)}
 *
 * <p>
 *     This is more or less a copy of {@code org.junit.jupiter.params.support.AnnotationConsumerInitializer} from the
 *     junit-jupiter-params project.
 * </p>
 */
public class AnnotationConsumerInitializer {

    private AnnotationConsumerInitializer() { }

    private static final Predicate<Method> isAnnotationConsumerAcceptMethod = method ->
            method.getName().equals("accept")
                    && method.getParameterCount() == 1
                    && method.getParameterTypes()[0].isAnnotation();

    /**
     * Initializes an instance with an annotation set on an element
     *
     * @param annotatedElement the element which is annotated with an annotation required by the {@link AnnotationConsumer}.
     *                         Should be annotated with the annotation required by the {@link AnnotationConsumer}
     * @param instance an instance of an {@link AnnotationConsumer}
     * @param <T> the type of the {@link AnnotationConsumer}
     * @return the instance of the {@link AnnotationConsumer} initialized with the required annotation
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T initialize(AnnotatedElement annotatedElement, T instance) {
        if (instance instanceof AnnotationConsumer) {
            Method method = findMethods(instance.getClass(), isAnnotationConsumerAcceptMethod, BOTTOM_UP).get(0);
            Class<? extends Annotation> annotationType = (Class<? extends Annotation>) method.getParameterTypes()[0];
            Annotation annotation = AnnotationSupport.findAnnotation(annotatedElement, annotationType) //
                    .orElseThrow(() -> new Coffee4JException(
                            "%s must be used with an annotation of type %s",
                            instance.getClass().getName(),
                            annotationType.getName()
                    ));
            initializeAnnotationConsumer((AnnotationConsumer) instance, annotation);
        }
        return instance;
    }

    private static <A extends Annotation> void initializeAnnotationConsumer(AnnotationConsumer<A> instance,
                                                                            A annotation) {
        try {
            instance.accept(annotation);
        }
        catch (Exception ex) {
            throw new Coffee4JException(ex, "Failed to initialize AnnotationConsumer: %s", instance);
        }
    }
}
