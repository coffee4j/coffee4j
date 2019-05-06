package de.rwth.swc.coffee4j.junit.provider.configuration.diagnosis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableConstraintDiagnosis {

    boolean skip() default true;
}
