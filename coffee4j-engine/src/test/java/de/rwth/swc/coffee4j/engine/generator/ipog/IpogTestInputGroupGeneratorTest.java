package de.rwth.swc.coffee4j.engine.generator.ipog;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.constraint.NoConstraintChecker;
import de.rwth.swc.coffee4j.engine.report.Reporter;
import de.rwth.swc.coffee4j.engine.generator.TestInputGroup;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IpogTestInputGroupGeneratorTest {
    
    @Test
    void simpleGenerationTest() {
        final TestModel model = new TestModel(1, new int[]{2}, Collections.emptyList(), Collections.emptyList());
        
        final Set<Supplier<TestInputGroup>> group = new Ipog().generate(model, Mockito.mock(Reporter.class));
        
        assertEquals(1, group.size());
        final TestInputGroup generatedGroup = new ArrayList<>(group).get(0).get();
        assertEquals(2, generatedGroup.getTestInputs().size());
        assertEquals(model, generatedGroup.getFaultCharacterizationConfiguration().get().getTestModel());
        assertEquals(NoConstraintChecker.class, generatedGroup.getFaultCharacterizationConfiguration().get().getChecker().getClass());
    }
    
}
