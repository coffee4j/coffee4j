package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.GroupSpecificTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Generator for one test group containing the test inputs generated with the
 * {@link IpogAlgorithm} algorithm using no constraints and the normal parameter order
 * with the strength given by the {@link CompleteTestModel}.
 */
public class Ipog implements TestInputGroupGenerator {
    
    private static final String DISPLAY_NAME = "Ipog";

    private final IpogConfiguration configuration;

    public Ipog() {
        this(IpogConfiguration.defaultConfiguration());
    }

    public Ipog(IpogConfiguration configuration) {
        this.configuration = Preconditions.notNull(configuration);
    }

    /**
     * Constructs a combinatorial test suite for positive testing.
     * This means that each combination of the given strength is guaranteed
     * to be covered by at least one test input returned by this method.
     *
     * @param model    the complete testModel with which the test input groups
     *                 should be constructed. Must not be {@code null}
     * @param reporter to report information from inside the generation
     * @return a test suite meeting the criteria described above
     */
    @Override
    public Set<Supplier<TestInputGroup>> generate(CompleteTestModel model, Reporter reporter) {
        if(model.getPositiveTestingStrength() == 0) {
            return Collections.emptySet();
        }

        return Collections.singleton(() -> {
            final ConstraintChecker constraintChecker = configuration.getConstraintCheckerFactory().createConstraintChecker(model);
            final TestModel groupModel = GroupSpecificTestModel.positive(model, constraintChecker);
            final List<int[]> testInputs = new IpogAlgorithm(groupModel).generate();
            final FaultCharacterizationConfiguration faultCharacterizationConfiguration
                    = new FaultCharacterizationConfiguration(groupModel, reporter);

            return new TestInputGroup(DISPLAY_NAME, testInputs, faultCharacterizationConfiguration);
        });
    }
    
}
