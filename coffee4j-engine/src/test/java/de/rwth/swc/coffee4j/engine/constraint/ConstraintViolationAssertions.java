package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.CombinatorialTestModel;
import de.rwth.swc.coffee4j.engine.util.Preconditions;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class ConstraintViolationAssertions {
    
    private ConstraintViolationAssertions() {
    }
    
    public static void assertExactNumberOfErrorConstraintViolations(CombinatorialTestModel ipm, int[] tuple, int expectedNumberOfViolations) {
        InternalConstraintConverter converter = new InternalConstraintConverter();
        List<InternalConstraint> errorConstraints = converter.convertErrorTuples(ipm);
        
        long actualNumberOfViolations = errorConstraints.stream().filter(errorConstraint -> isConstraintViolation(ipm, tuple, errorConstraint)).count();
        
        if (actualNumberOfViolations != expectedNumberOfViolations) {
            fail("ASSERTION-ERROR: " + Arrays.toString(tuple) + " violates " + actualNumberOfViolations + " constraints");
        }
    }
    
    public static void assertAtMostNumberOfErrorConstraintViolations(CombinatorialTestModel ipm, int[] tuple, int expectedNumberOfViolations) {
        InternalConstraintConverter converter = new InternalConstraintConverter();
        List<InternalConstraint> errorConstraints = converter.convertErrorTuples(ipm);
        
        long actualNumberOfViolations = errorConstraints.stream().filter(errorConstraint -> isConstraintViolation(ipm, tuple, errorConstraint)).count();
        
        if (actualNumberOfViolations > expectedNumberOfViolations) {
            fail("ASSERTION-ERROR: " + Arrays.toString(tuple) + " violates " + actualNumberOfViolations + " constraints");
        }
    }
    
    public static void assertNoExclusionConstraintViolations(CombinatorialTestModel ipm, int[] tuple) {
        InternalConstraintConverter converter = new InternalConstraintConverter();
        List<InternalConstraint> exclusionConstraints = converter.convertForbiddenTuples(ipm);
        
        for (InternalConstraint constraint : exclusionConstraints) {
            if (isConstraintViolation(ipm, tuple, constraint)) {
                fail("ASSERTION-ERROR: " + Arrays.toString(tuple) + " violates constraint of " + constraint.getId());
            }
        }
    }
    
    private static boolean isConstraintViolation(CombinatorialTestModel ipm, int[] tuple, InternalConstraint constraint) {
        Preconditions.check(ipm.getNumberOfParameters() == tuple.length);
        
        final Model model = new Model();
        
        IntVar[] vars = new IntVar[ipm.getNumberOfParameters()];
        for (int i = 0; i < ipm.getNumberOfParameters(); i++) {
            if (tuple[i] > -1) {
                vars[i] = model.intVar(String.valueOf(i), 0, ipm.getSizeOfParameter(i) - 1);
            }
        }
        
        for (int i = 0; i < tuple.length; i++) {
            if (tuple[i] > -1) {
                model.arithm(vars[i], "=", tuple[i]).post();
            }
        }
        
        constraint.post(ipm, model);
        
        return !model.getSolver().solve();
    }
}
