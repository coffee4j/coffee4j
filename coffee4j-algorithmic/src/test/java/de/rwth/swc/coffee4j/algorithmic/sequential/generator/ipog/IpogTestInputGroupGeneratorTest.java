package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.constraint.*;
import de.rwth.swc.coffee4j.algorithmic.model.GroupSpecificTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IpogTestInputGroupGeneratorTest {
    
    @Test
    void simpleGenerationTest() {
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2)
                .build();

        final ConstraintCheckerFactory factory = new NoConstraintCheckerFactory();

        final Set<Supplier<TestInputGroup>> group = new Ipog(new IpogConfiguration(factory))
                .generate(model, Mockito.mock(Reporter.class));
        
        assertEquals(1, group.size());
        final TestInputGroup generatedGroup = new ArrayList<>(group).get(0).get();
        assertEquals(2, generatedGroup.getTestInputs().size());
        
        final TestModel positiveGroupModel = GroupSpecificTestModel.positive(model,
                factory.createConstraintChecker(model));
        assertEquals(positiveGroupModel, generatedGroup.getFaultCharacterizationConfiguration()
                .orElseThrow(IllegalAccessError::new)
                .getModel());
    }
}
