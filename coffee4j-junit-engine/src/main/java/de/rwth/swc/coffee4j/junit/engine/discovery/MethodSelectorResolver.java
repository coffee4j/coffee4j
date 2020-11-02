package de.rwth.swc.coffee4j.junit.engine.discovery;

import de.rwth.swc.coffee4j.junit.engine.descriptor.CombinatorialTestMethodDescriptor;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.support.discovery.SelectorResolver;

import java.lang.reflect.Method;
import java.util.Optional;

public class MethodSelectorResolver implements SelectorResolver {
    
    private static final IsCombinatorialTestContainer IS_COMBINATORIAL_TEST_CONTAINER = new IsCombinatorialTestContainer();
    
    @Override
    public Resolution resolve(MethodSelector selector, Context context) {
        final Class<?> testClass = selector.getJavaClass();
        final Method testMethod = selector.getJavaMethod();
        
        if (IS_COMBINATORIAL_TEST_CONTAINER.test(testClass)
                && AnnotationSupport.isAnnotated(testMethod, CombinatorialTest.class)) {
            return context.addToParent(() -> DiscoverySelectors.selectClass(testClass), parent -> Optional
                    .of(new CombinatorialTestMethodDescriptor(parent, testMethod)))
                    .map(testDescriptor -> Resolution.match(Match.exact(testDescriptor)))
                    .orElse(Resolution.unresolved());
        } else {
            return Resolution.unresolved();
        }
    }
    
}
