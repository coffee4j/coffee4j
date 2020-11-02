package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.prioritization;

import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConstructorBasedTestInputPrioritizerProviderTest {
    
    @Test
    void instantiatesGivenClass() throws NoSuchMethodException {
        final ConstructorBasedTestInputPrioritizerProvider provider =
                new ConstructorBasedTestInputPrioritizerProvider();
        final Method method = getClass().getMethod("someMethod");
        final EnableTestInputPrioritization configuration = method.getAnnotation(EnableTestInputPrioritization.class);
        
        provider.accept(configuration);
        final TestInputPrioritizer prioritizer = provider.provide(method);
        
        assertNotNull(configuration);
        assertThat(prioritizer).isInstanceOf(SomeTestInputPrioritizer.class);
    }
    
    @EnableTestInputPrioritization(SomeTestInputPrioritizer.class)
    public void someMethod() {
    }
    
    public static final class SomeTestInputPrioritizer implements TestInputPrioritizer {
    
        @Override
        public List<int[]> prioritize(Collection<int[]> testCases, TestModel model) {
            return new ArrayList<>(testCases);
        }
    
    }
    
}
