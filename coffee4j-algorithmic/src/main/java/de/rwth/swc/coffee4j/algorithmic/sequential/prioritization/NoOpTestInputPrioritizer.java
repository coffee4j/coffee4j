package de.rwth.swc.coffee4j.algorithmic.sequential.prioritization;

import de.rwth.swc.coffee4j.algorithmic.model.TestModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An implementation of {@link TestInputPrioritizer} which does not do anything except returning the same test inputs.
 */
public final class NoOpTestInputPrioritizer implements TestInputPrioritizer {
    
    @Override
    public List<int[]> prioritize(Collection<int[]> testCases, TestModel model) {
        return Collections.unmodifiableList(new ArrayList<>(testCases));
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public boolean equals(Object object) {
        return object instanceof NoOpTestInputPrioritizer;
    }
    
    @Override
    public String toString() {
        return "NoOpTestInputPrioritizer{}";
    }
    
}
