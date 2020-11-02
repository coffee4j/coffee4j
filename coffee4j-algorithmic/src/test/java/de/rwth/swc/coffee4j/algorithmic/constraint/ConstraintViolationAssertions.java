package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class ConstraintViolationAssertions {
    
    private ConstraintViolationAssertions() {
    }
    
    public static void assertExactNumberOfErrorConstraintViolations(CompleteTestModel ipm, int[] tuple, int expectedNumberOfViolations) {
        ConstraintConverter converter = new ConstraintConverter();
        List<Constraint> errorConstraints = converter.convertAll(ipm.getErrorTupleLists());
        
        long actualNumberOfViolations = errorConstraints.stream()
                .filter(errorConstraint -> isConstraintViolation(ipm, tuple, errorConstraint)).count();
        
        if (actualNumberOfViolations != expectedNumberOfViolations) {
            fail("ASSERTION-ERROR: " + Arrays.toString(tuple) + " violates " + actualNumberOfViolations + " constraints");
        }
    }
    
    public static void assertAtMostNumberOfErrorConstraintViolations(CompleteTestModel ipm, int[] tuple, int expectedNumberOfViolations) {
        ConstraintConverter converter = new ConstraintConverter();
        List<Constraint> errorConstraints = converter.convertAll(ipm.getErrorTupleLists());
        
        long actualNumberOfViolations = errorConstraints.stream()
                .filter(errorConstraint -> isConstraintViolation(ipm, tuple, errorConstraint)).count();
        
        if (actualNumberOfViolations > expectedNumberOfViolations) {
            fail("ASSERTION-ERROR: " + Arrays.toString(tuple) + " violates " + actualNumberOfViolations + " constraints");
        }
    }
    
    public static void assertNoExclusionConstraintViolations(CompleteTestModel ipm, int[] tuple) {
        ConstraintConverter converter = new ConstraintConverter();
        List<Constraint> exclusionConstraints = converter.convertAll(ipm.getExclusionTupleLists());
        
        for (Constraint constraint : exclusionConstraints) {
            if (isConstraintViolation(ipm, tuple, constraint)) {
                fail("ASSERTION-ERROR: " + Arrays.toString(tuple) + " violates constraint of " + constraint.getTupleList().getId());
            }
        }
    }
    
    private static boolean isConstraintViolation(CompleteTestModel ipm, int[] tuple, Constraint constraint) {
        Preconditions.check(ipm.getNumberOfParameters() == tuple.length);
        
        final Model model = new Model();
        model.getSettings().setCheckDeclaredConstraints(false);

        IntVar[] vars = new IntVar[ipm.getNumberOfParameters()];
        for (int i = 0; i < ipm.getNumberOfParameters(); i++) {
            if (tuple[i] > -1) {
                vars[i] = model.intVar(String.valueOf(i), 0, ipm.getParameterSize(i) - 1);
            }
        }
        
        for (int i = 0; i < tuple.length; i++) {
            if (tuple[i] > -1) {
                model.arithm(vars[i], "=", tuple[i]).post();
            }
        }
        
        constraint.apply(model).post();
        
        return !model.getSolver().solve();
    }
}
