package de.rwth.swc.coffee4j.algorithmic.sequential.generator.emptyset;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * Generator that returns an empty test group that can be used by interleaving Fault Characterization Algorithms.
 */
public class EmptySetGenerator implements TestInputGroupGenerator {
    
    private static final String IDENTIFIER = "Empty Test Suite";
    
    @Override
    public Collection<Supplier<TestInputGroup>> generate(CompleteTestModel model, Reporter reporter) {
        return Collections.singleton(
                () -> new TestInputGroup(
                        IDENTIFIER,
                        Collections.emptyList(),
                        new FaultCharacterizationConfiguration(model, reporter)
                )
        );
    }
    
}
