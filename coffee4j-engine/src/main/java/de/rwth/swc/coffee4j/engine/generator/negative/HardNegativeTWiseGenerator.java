package de.rwth.swc.coffee4j.engine.generator.negative;

import de.rwth.swc.coffee4j.engine.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.engine.CombinatorialTestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import de.rwth.swc.coffee4j.engine.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.engine.generator.TestInputGroup;
import de.rwth.swc.coffee4j.engine.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.engine.report.Reporter;
import de.rwth.swc.coffee4j.engine.util.Preconditions;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.rwth.swc.coffee4j.engine.generator.negative.NegativeTestInputGenerator.createTestInputGroup;

public class HardNegativeTWiseGenerator implements TestInputGroupGenerator {
    
    @Override
    public Collection<Supplier<TestInputGroup>> generate(CombinatorialTestModel model, Reporter reporter) {
        Preconditions.notNull(model);
        
        final ConstraintCheckerFactory checkerFactory = new ConstraintCheckerFactory(model);
        final int newStrength = Math.max(model.getStrength() - 1, 1);
        final CombinatorialTestModel modelWithNewStrength = new CombinatorialTestModel(newStrength, model.getParameterSizes(), model.getForbiddenTupleLists(), model.getErrorTupleLists());
        
        return model.getErrorTupleLists().stream().map(errorTuples -> createGroupSupplier(errorTuples, modelWithNewStrength, checkerFactory, reporter)).collect(Collectors.toList());
    }
    
    private Supplier<TestInputGroup> createGroupSupplier(TupleList errorTuples, CombinatorialTestModel model, ConstraintCheckerFactory factory, Reporter reporter) {
        return () -> {
            final ConstraintChecker checker = factory.createHardConstraintsCheckerWithNegation(errorTuples);
            return createTestInputGroup(checker, errorTuples, model, reporter);
        };
    }
    
}
