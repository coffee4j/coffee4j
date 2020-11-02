package de.rwth.swc.coffee4j.junit.engine.discovery;

import de.rwth.swc.coffee4j.junit.engine.MockingTest;
import de.rwth.swc.coffee4j.junit.engine.descriptor.CombinatorialTestMethodDescriptor;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.support.discovery.SelectorResolver;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MethodSelectorResolverTest implements MockingTest {
    
    private static MethodSelector methodSelector;
    
    @BeforeAll
    public static void createTestMethod() throws NoSuchMethodException {
        methodSelector = DiscoverySelectors.selectMethod(TestCase.class, TestCase.class.getMethod("testMethod"));
    }

    @Test
    void resolvesMethodSelector() {
        final SelectorResolver.Context context = mock(SelectorResolver.Context.class);
        final CombinatorialTestMethodDescriptor descriptor = mock(CombinatorialTestMethodDescriptor.class);
        when(context.addToParent(any(), any())).thenReturn(Optional.of(descriptor));

        final MethodSelectorResolver resolver = new MethodSelectorResolver();
        final SelectorResolver.Resolution resolution = resolver.resolve(methodSelector, context);
        assertThat(resolution.isResolved()).isTrue();
        
        final Set<SelectorResolver.Match> matches = resolution.getMatches();
        assertThat(matches)
                .hasSize(1);
        final SelectorResolver.Match match = matches.iterator().next();
        assertThat(match.getTestDescriptor())
                .isEqualTo(descriptor);
    }

    public static class TestCase {
        
        @CombinatorialTest
        public void testMethod() {
        
        }
    }
}
