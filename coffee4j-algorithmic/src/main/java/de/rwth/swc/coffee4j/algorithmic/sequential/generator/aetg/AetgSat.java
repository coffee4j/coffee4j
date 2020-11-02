package de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.DynamicHardConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.GroupSpecificTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Generator for one test group containing the test inputs generated with the {@link AetgSatAlgorithm} algorithm with
 * the strength given by the {@link CompleteTestModel}.
 */
public class AetgSat implements TestInputGroupGenerator {

    private static final String DISPLAY_NAME = "Positive AetgSatAlgorithm Tests";

    @Override
    public Set<Supplier<TestInputGroup>> generate(CompleteTestModel model, Reporter reporter) {
        if (model.getPositiveTestingStrength() == 0) {
            return Collections.emptySet();
        }

        return Collections.singleton(() -> {
    
            final ConstraintChecker constraintChecker = new DynamicHardConstraintChecker(model,
                    model.getExclusionConstraints(), model.getErrorConstraints());
            final TestModel groupModel = GroupSpecificTestModel.positive(model, constraintChecker);
            
            final List<int[]> testInputs = new AetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration()
                    .model(groupModel).build())
                    .generate();
            final FaultCharacterizationConfiguration faultCharacterizationConfiguration =
                    new FaultCharacterizationConfiguration(groupModel, reporter);
            return new TestInputGroup(DISPLAY_NAME, testInputs, faultCharacterizationConfiguration);
        });
    }
}
