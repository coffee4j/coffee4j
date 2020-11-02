package de.rwth.swc.coffee4j.junit.engine.discovery;

import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ModifierSupport;

import java.util.function.Predicate;

class IsCombinatorialTestContainer implements Predicate<Class<?>> {

    @Override
    public boolean test(Class<?> classCandidate) {
        if (ModifierSupport.isAbstract(classCandidate)) {
            return false;
        } else if (ModifierSupport.isPrivate(classCandidate)) {
            return false;
        } else if (classCandidate.isLocalClass()) {
            return false;
        } else if (classCandidate.isAnonymousClass()) {
            return false;
        } else if (classCandidate.isMemberClass() && !ModifierSupport.isStatic(classCandidate)) {
            return false;
        } else {
            return !AnnotationSupport.findAnnotatedMethods(classCandidate, CombinatorialTest.class,
                    HierarchyTraversalMode.TOP_DOWN).isEmpty();
        }
    }
    
}
