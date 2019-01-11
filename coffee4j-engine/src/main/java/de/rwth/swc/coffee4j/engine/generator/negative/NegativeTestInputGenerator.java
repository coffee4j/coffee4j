package de.rwth.swc.coffee4j.engine.generator.negative;

import de.rwth.swc.coffee4j.engine.InputParameterModel;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.engine.constraint.ConflictingErrorConstraintSearcher;
import de.rwth.swc.coffee4j.engine.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.engine.generator.ipog.Ipog;
import de.rwth.swc.coffee4j.engine.generator.ipog.IpogConfiguration;
import de.rwth.swc.coffee4j.engine.generator.ipog.ParameterCombinationFactory;
import de.rwth.swc.coffee4j.engine.generator.ipog.ParameterOrder;
import de.rwth.swc.coffee4j.engine.report.CombinationArgument;
import de.rwth.swc.coffee4j.engine.report.Report;
import de.rwth.swc.coffee4j.engine.report.Reporter;
import de.rwth.swc.coffee4j.engine.TupleList;
import de.rwth.swc.coffee4j.engine.generator.TestInputGroup;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.List;

final class NegativeTestInputGenerator {
    
    private NegativeTestInputGenerator() {
    }
    
    static TestInputGroup createTestInputGroup(ConstraintChecker checker, TupleList errorTuples, InputParameterModel model, Reporter reporter) {
        
        ParameterCombinationFactory factory = new NegativeTWiseParameterCombinationFactory(errorTuples);
        ParameterOrder order = new NegativityAwareParameterOrder(errorTuples);
        
        final List<int[]> testInputs = new Ipog(IpogConfiguration.ipogConfiguration().model(model).checker(checker).factory(factory).order(order).reporter(reporter).build()).generate();
        final FaultCharacterizationConfiguration faultCharacterizationConfiguration = new FaultCharacterizationConfiguration(model, checker, reporter);
        return new TestInputGroup(errorTuples, testInputs, faultCharacterizationConfiguration);
    }
    
    static void checkSatisfiability(ConflictingErrorConstraintSearcher searcher, TupleList errorTuples, Reporter reporter) {
        int counter = 0;
        
        for (int[] tuple : errorTuples.getTuples()) {
            IntList list = searcher.explainValueBasedConflict(errorTuples.getInvolvedParameters(), tuple);
            
            if (!list.isEmpty()) {
                counter++;
                reporter.reportWarn(() -> Report.report("unsatisfiable constraints: no negative test inputs for {0} " + "of no. {1} containing {2}", CombinationArgument.combination(tuple), errorTuples, list));
            }
        }
        
        if (counter > 0) {
            reporter.reportWarn(() -> Report.report("In total, {0} invalid tuples are missing!"));
        }
    }
    
}
