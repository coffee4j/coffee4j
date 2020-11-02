package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.IpogNegConfiguration;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IpogNeg implements TestInputGroupGenerator {

    private final IpogNegConfiguration configuration;

    public IpogNeg() {
        this(IpogNegConfiguration.defaultConfiguration());
    }

    public IpogNeg(IpogNegConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Collection<Supplier<TestInputGroup>> generate(CompleteTestModel model, Reporter reporter) {
        Preconditions.notNull(model);
        Preconditions.notNull(reporter);

        return model.getErrorTupleLists()
                .stream()
                .map(errorTuples
                        -> createGroupSupplier(errorTuples, model, configuration.getConstraintCheckerFactory(), reporter))
                .collect(Collectors.toList());
    }

    private Supplier<TestInputGroup> createGroupSupplier(TupleList errorTuples, CompleteTestModel model, ConstraintCheckerFactory factory, Reporter reporter) {
        return () -> {
            final ConstraintChecker checker = factory.createConstraintCheckerWithNegation(model, errorTuples);

            return createTestInputGroup(checker, errorTuples, model, reporter);
        };
    }

    private TestInputGroup createTestInputGroup(ConstraintChecker checker, TupleList errorTuples, CompleteTestModel testModel, Reporter reporter) {
        final int strengthA = configuration.getStrengthA() > 0
                ? Math.min(configuration.getStrengthA(), errorTuples.getInvolvedParameters().length)
                : errorTuples.getInvolvedParameters().length;

        final ParameterOrder order
                = new NegativeStrengthBasedParameterOrder(errorTuples, strengthA);

        final ParameterCombinationFactory parameterCombinationFactory
                = new NegativeStrengthBasedParameterCombinationFactory(errorTuples, strengthA);

        final List<int[]> testInputs = new IpogAlgorithm(IpogAlgorithmConfiguration
                .ipogConfiguration()
                .testModel(testModel)
                .testingStrength(testModel.getNegativeTestingStrength())
                .constraintChecker(checker)
                .factory(parameterCombinationFactory)
                .order(order)
                .reporter(reporter)
                .build()
        ).generate();

        final FaultCharacterizationConfiguration faultCharacterizationConfiguration
                = new FaultCharacterizationConfiguration(testModel, reporter);

        return new TestInputGroup(errorTuples, testInputs, faultCharacterizationConfiguration);
    }
}

