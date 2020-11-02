package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.diagnosis;

import de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis.ConflictDiagnostician;
import de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis.ExhaustiveConflictDiagnostician;
import de.rwth.swc.coffee4j.algorithmic.conflict.explanation.ConflictExplainer;
import de.rwth.swc.coffee4j.algorithmic.conflict.explanation.QuickConflictExplainer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableConflictDetection {

    /**
     * Should the test process be aborted if conflicts are detected
     * @return  true    the test process will be aborted in the presence of conflicts
     *          false   the test process will not be aborted
     */
    boolean shouldAbort() default true;

    boolean explainConflicts() default true;

    Class<? extends ConflictExplainer> conflictExplanationAlgorithm() default QuickConflictExplainer.class;

    boolean diagnoseConflicts() default false;

    Class<? extends ConflictDiagnostician> conflictDiagnosisAlgorithm() default ExhaustiveConflictDiagnostician.class;

}
