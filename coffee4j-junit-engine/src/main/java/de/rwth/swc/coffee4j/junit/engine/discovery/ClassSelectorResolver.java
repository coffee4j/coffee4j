package de.rwth.swc.coffee4j.junit.engine.discovery;

import de.rwth.swc.coffee4j.junit.engine.descriptor.CombinatorialTestClassDescriptor;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.discovery.SelectorResolver;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotatedMethods;
import static org.junit.platform.commons.support.HierarchyTraversalMode.*;

/**
 * This is an adapted copy of {@code org.junit.jupiter.engine.discovery.ClassSelectorResolver}
 */
class ClassSelectorResolver implements SelectorResolver {

    private static final IsCombinatorialTestContainer IS_COMBINATORIAL_TEST_CONTAINER = new IsCombinatorialTestContainer();

    @Override
    public Resolution resolve(ClassSelector selector, Context context) {
        final Class<?> testClass = selector.getJavaClass();
        
        if (IS_COMBINATORIAL_TEST_CONTAINER.test(testClass)) {
            return context.addToParent(parent -> Optional.of(new CombinatorialTestClassDescriptor(parent, testClass)))
                    .map(testDescriptor -> Resolution.match(Match.exact(testDescriptor, () ->
                            findAnnotatedMethods(testClass, CombinatorialTest.class, TOP_DOWN).stream()
                                    .map(method -> DiscoverySelectors.selectMethod(testClass, method))
                                    .collect(Collectors.toSet()))))
                    .orElse(Resolution.unresolved());
        } else {
            return Resolution.unresolved();
        }
    }
    
}
