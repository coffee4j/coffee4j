package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.prioritization;

import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.NoOpTestInputPrioritizer;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TestInputPrioritizationLoaderTest {
    
    @Test
    void loadsNoOpPrioritizerAsDefault() throws NoSuchMethodException {
        final Method method = getClass().getMethod("nonAnnotatedMethod");
        final TestInputPrioritizerLoader loader = new TestInputPrioritizerLoader();
        
        final TestInputPrioritizer prioritizer = loader.load(method);
        
        assertThat(prioritizer).isInstanceOf(NoOpTestInputPrioritizer.class);
    }
    
    public void nonAnnotatedMethod() {
    }
    
    @Test
    void loadsPrioritizerFromSourceIfGiven() throws NoSuchMethodException {
        final Method method = getClass().getMethod("annotatedMethod");
        final TestInputPrioritizerLoader loader = new TestInputPrioritizerLoader();
        
        final TestInputPrioritizer prioritizer = loader.load(method);
        
        assertThat(prioritizer).isInstanceOf(SomeTestInputPrioritizer.class);
    }
    
    @EnableTestInputPrioritization(SomeTestInputPrioritizer.class)
    public void annotatedMethod() {
    }
    
    public static final class SomeTestInputPrioritizer implements TestInputPrioritizer {
        
        @Override
        public List<int[]> prioritize(Collection<int[]> testCases, TestModel model) {
            return new ArrayList<>(testCases);
        }
        
    }
    
}
