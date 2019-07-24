package de.rwth.swc.coffee4j.engine.conflict.choco;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import de.rwth.swc.coffee4j.engine.constraint.InternalConstraint;
import de.rwth.swc.coffee4j.engine.constraint.InternalConstraintConverter;
import de.rwth.swc.coffee4j.engine.constraint.NegatingInternalConstraint;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.rwth.swc.coffee4j.engine.constraint.ChocoSolverUtil.findVariable;
import static org.junit.jupiter.api.Assertions.*;

public class ChocoModelTest {

    @Nested
    class EnableAndDisableConstraint {
        final ChocoModel chocoModel = createTestModel(4);

        @RepeatedTest(5)
        void testEnableAndDisableConstraint() {
            chocoModel.setAssignmentConstraint(new int[] { 0, 1 }, new int[] { 0, 2 });
            assertFalse(chocoModel.isSatisfiable());
            chocoModel.reset();

            chocoModel.disableConstraint(2);
            chocoModel.setAssignmentConstraint(new int[] { 0, 1 }, new int[] { 0, 2 });
            assertTrue(chocoModel.isSatisfiable());
            chocoModel.reset();

            chocoModel.enableConstraint(2);
            chocoModel.setAssignmentConstraint(new int[] { 0, 1 }, new int[] { 0, 2 });
            assertFalse(chocoModel.isSatisfiable());
            chocoModel.reset();

            chocoModel.clearAssignmentConstraint();
        }
    }

    @Nested
    class TestIsSatisfiable {
        final ChocoModel chocoModel = createTestModel(-1);

        @Test
        void testSatisfiable() {
            chocoModel.setAssignmentConstraint(new int[] { 0, 1, 2 }, new int[] { 0, 0, 0 });
            assertTrue(chocoModel.isSatisfiable());
            chocoModel.reset();
        }

        @Test
        void testUnsatisfiable() {
            chocoModel.setAssignmentConstraint(new int[] { 0, 1, 2 }, new int[] { 0, 0, 2 });
            assertFalse(chocoModel.isSatisfiable());
            chocoModel.reset();
        }
    }

    @Test
    void testEnableAndDisableConstraint2() {
        final ChocoModel chocoModel = createTestModel(-1);

        //chocoModel.setAssignmentConstraint(new int[] { 0, 1, 2 }, new int[] { 0, 0, 0 });
        //assertTrue(chocoModel.isSatisfiable());
        //chocoModel.reset();

        chocoModel.setAssignmentConstraint(new int[] { 0, 1, 2 }, new int[] { 0, 0, 2 });
        assertFalse(chocoModel.isSatisfiable());
        chocoModel.reset();



        chocoModel.setAssignmentConstraint(new int[] { 0, 1, 2 }, new int[] { 0, 0, 2 });
        chocoModel.disableConstraint(3);
        assertTrue(chocoModel.isSatisfiable());
        chocoModel.reset();
        chocoModel.enableConstraint(3);

        chocoModel.setAssignmentConstraint(new int[] { 0, 1, 2 }, new int[] { 0, 0, 2 });
        assertFalse(chocoModel.isSatisfiable());
        chocoModel.reset();

        chocoModel.setAssignmentConstraint(new int[] { 0, 1, 2 }, new int[] { 0, 0, 0 });
        assertTrue(chocoModel.isSatisfiable());
        chocoModel.reset();
    }

    @Test
    void testAssignmentConstraint() {
        final ChocoModel chocoModel = createTestModel(-1);

        chocoModel.setAssignmentConstraint(new int[] { 0 }, new int[] { 2 });
        assertFalse(chocoModel.isSatisfiable());
        chocoModel.reset();

        chocoModel.setAssignmentConstraint(new int[] { 0 }, new int[] { 0 });
        assertTrue(chocoModel.isSatisfiable());
        chocoModel.reset();

        chocoModel.setAssignmentConstraint(new int[] { 0 }, new int[] { 2 });
        assertFalse(chocoModel.isSatisfiable());
        chocoModel.reset();
    }

    @Nested
    class TestNegationOfConstraint {
        final ChocoModel chocoModel = createTestModel(-1);

        @Test
        void testNegationOfConstraint1() {
            chocoModel.setAssignmentConstraint(new int[] { 0 }, new int[] { 2 });

            assertFalse(chocoModel.isSatisfiable());
            chocoModel.reset();

            chocoModel.setNegationOfConstraint(1);
            assertTrue(chocoModel.isSatisfiable());
            chocoModel.reset();

            chocoModel.resetNegationOfConstraint();
            assertFalse(chocoModel.isSatisfiable());
            chocoModel.reset();

            chocoModel.clearAssignmentConstraint();
        }

        @Test
        void testNegationOfConstraint3() {
            chocoModel.setAssignmentConstraint(new int[] { 2 }, new int[] { 2 });

            assertFalse(chocoModel.isSatisfiable());
            chocoModel.reset();

            chocoModel.setNegationOfConstraint(3);
            assertTrue(chocoModel.isSatisfiable());
            chocoModel.reset();

            chocoModel.resetNegationOfConstraint();
            assertFalse(chocoModel.isSatisfiable());
            chocoModel.reset();
        }
    }

    @Test
    void testIntegration() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, Collections.singletonList(new int[]{2})));               // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, Collections.singletonList(new int[]{2})));               // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, Collections.singletonList(new int[]{2})));               // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{0, 2})));    // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{1, 2})));    // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]

        final TestModel combinatorialTestModel = new TestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), errorTupleLists);

        final InternalConstraintConverter converter = new InternalConstraintConverter();
        final List<InternalConstraint> constraints = new ArrayList<>();
        constraints.addAll(converter.convertAll(combinatorialTestModel.getForbiddenTupleLists()));
        constraints.addAll(converter.convertAll(combinatorialTestModel.getErrorTupleLists()));

        final ChocoModel chocoModel = new ChocoModel(combinatorialTestModel.getParameterSizes(), constraints);

        chocoModel.setAssignmentConstraint(new int[] { 0, 1, 2 }, new int[] { 0, 0, 0 });
        assertTrue(chocoModel.isSatisfiable());
        chocoModel.reset();

        chocoModel.setAssignmentConstraint(new int[] { 0, 1, 2 }, new int[] { 0, 0, 2 });
        assertFalse(chocoModel.isSatisfiable());
        chocoModel.reset();

        chocoModel.setAssignmentConstraint(new int[] { 0, 1, 2 }, new int[] { 0, 0, 2 });
        chocoModel.disableConstraint(3);
        assertTrue(chocoModel.isSatisfiable());
        chocoModel.reset();
        chocoModel.enableConstraint(3);

        chocoModel.setAssignmentConstraint(new int[] { 0, 1, 2 }, new int[] { 0, 0, 2 });
        assertFalse(chocoModel.isSatisfiable());
        chocoModel.reset();

        chocoModel.setAssignmentConstraint(new int[] { 0, 1, 2 }, new int[] { 0, 0, 0 });
        assertTrue(chocoModel.isSatisfiable());
        chocoModel.reset();

        chocoModel.setAssignmentConstraint(new int[] { 2 }, new int[] { 2 });
        assertFalse(chocoModel.isSatisfiable());
        chocoModel.reset();

        chocoModel.setAssignmentConstraint(new int[] { 2 }, new int[] { 2 });
        chocoModel.disableConstraint(3);
        assertTrue(chocoModel.isSatisfiable());
        chocoModel.reset();

        chocoModel.setNegationOfConstraint(1);
        chocoModel.setAssignmentConstraint(new int[] { 1 }, new int[] { 2 });
        assertFalse(chocoModel.isSatisfiable());
        chocoModel.reset();

        chocoModel.clearAssignmentConstraint();
        chocoModel.resetNegationOfConstraint();
    }

    public static ChocoModel createTestModel(int idToBeNegated) {
        List<InternalConstraint> internalConstraints = new ArrayList<>();

        add(idToBeNegated, internalConstraints,
                new InternalConstraint(1, (Model model)
                                -> model.arithm((IntVar) findVariable(model, 0).get(), "=", 2).getOpposite())
        );
        add(idToBeNegated, internalConstraints,
                new InternalConstraint(2, (Model model)
                        -> model.arithm((IntVar) findVariable(model, 1).get(), "=", 2).getOpposite())
        );
        add(idToBeNegated, internalConstraints,
                new InternalConstraint(3, (Model model)
                        -> model.arithm((IntVar) findVariable(model, 2).get(), "=", 2).getOpposite())
        );
        add(idToBeNegated, internalConstraints,
                new InternalConstraint(4, (Model model)
                        -> model.or(
                                model.and(model.arithm((IntVar) findVariable(model, 0).get(), "=", 0), model.arithm((IntVar) findVariable(model, 1).get(), "=", 1)),
                                model.and(model.arithm((IntVar) findVariable(model, 0).get(), "=", 0), model.arithm((IntVar) findVariable(model, 1).get(), "=", 2))
                        ).getOpposite())
        );
        add(idToBeNegated, internalConstraints,
                new InternalConstraint(5, (Model model) ->
                        model.or(
                                model.and(model.arithm((IntVar) findVariable(model, 0).get(), "=", 1), model.arithm((IntVar) findVariable(model, 1).get(), "=", 0)),
                                model.and(model.arithm((IntVar) findVariable(model, 0).get(), "=", 1), model.arithm((IntVar) findVariable(model, 1).get(), "=", 2))
                        ).getOpposite())
        );

        final TestModel testModel = new TestModel(2, new int[] { 3, 3, 3 }, Collections.emptyList(), Collections.emptyList());

        return new ChocoModel(testModel.getParameterSizes(), internalConstraints);
    }

    private static void add(int idToBeNegated,
                            List<InternalConstraint> internalConstraints,
                            InternalConstraint internalConstraint) {
        if(internalConstraint.getId() == idToBeNegated) {
            internalConstraint = new NegatingInternalConstraint(internalConstraint);
        }

        internalConstraints.add(internalConstraint);
    }
}
