package io.github.coffee4j.junit.engine.annotation.configuration.classification;

import io.github.coffee4j.algorithmic.classification.ClassificationStrategy;
import io.github.coffee4j.algorithmic.classification.IsolatingClassificationStrategy;

import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableClassification {
    /**
     * @return returns the {@link ClassificationStrategy} provided by this annotation.
     */
    Class<? extends ClassificationStrategy> value() default IsolatingClassificationStrategy.class;
}
