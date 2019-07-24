package de.rwth.swc.coffee4j.engine.conflict.diagnosis;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.conflict.*;
import de.rwth.swc.coffee4j.engine.conflict.choco.ChocoModel;
import de.rwth.swc.coffee4j.engine.conflict.explanation.QuickConflictExplainer;
import de.rwth.swc.coffee4j.engine.constraint.InternalConstraint;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import java.util.*;

import static de.rwth.swc.coffee4j.engine.AssertUtils.assertInstanceOf;
import static de.rwth.swc.coffee4j.engine.conflict.choco.ChocoModelTest.createTestModel;
import static de.rwth.swc.coffee4j.engine.constraint.ChocoSolverUtil.findVariable;
import static org.junit.jupiter.api.Assertions.*;
import static org.testng.Assert.assertThrows;

class ConflictDiagnosticianTest {

    @Test
    void testGetAllDiagnosesForImplicitConflict() {
        final ChocoModel chocoModel = createTestModel(2);
        chocoModel.setAssignmentConstraint(new int[] { 1 }, new int[] { 2 });

        final int[] background = new int[] { 2, 6 };
        final int[] relaxable = new int[] {1, 3, 4, 5};

        final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                .getMinimalConflict(chocoModel, background, relaxable);

        assertTrue(explanation.isPresent());

        final ConflictDiagnostician diagnostician = new ExhaustiveConflictDiagnostician();
        final int[][] diagnoses = diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation.get());

        assertEquals(3, diagnoses.length);
        assertTrue(Arrays.stream(diagnoses)
                .anyMatch(diagnosis -> Arrays.equals(diagnosis, new int[]{1})));
        assertTrue(Arrays.stream(diagnoses)
                .anyMatch(diagnosis -> Arrays.equals(diagnosis, new int[]{4})));
        assertTrue(Arrays.stream(diagnoses)
                .anyMatch(diagnosis -> Arrays.equals(diagnosis, new int[]{5})));
    }

    @Test
    void testGetFirstDiagnosisForImplicitConflict() {
        final ChocoModel chocoModel = createTestModel(2);
        chocoModel.setAssignmentConstraint(new int[] { 1 }, new int[] { 2 });

        final int[] background = new int[] { 2, 6 };
        final int[] relaxable = new int[] {1, 3, 4, 5};

        final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                .getMinimalConflict(chocoModel, background, relaxable);

        assertTrue(explanation.isPresent());

        final ConflictDiagnostician diagnostician = new FastConflictDiagnostician();
        final int[][] diagnoses = diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation.get());

        assertEquals(1, diagnoses.length);
        assertArrayEquals(new int[]{1}, diagnoses[0]);
    }

    @Test
    void testGetAllDiagnosesForExplicitConflict() {
        final ChocoModel chocoModel = createTestModel(4);
        chocoModel.setAssignmentConstraint(new int[] { 0, 1 }, new int[] { 0, 2 });

        final int[] background = new int[] { 4, 6 };
        final int[] relaxable = new int[] {1, 2, 3, 5};

        final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                .getMinimalConflict(chocoModel, background, relaxable);

        assertTrue(explanation.isPresent());

        final ConflictDiagnostician diagnostician = new ExhaustiveConflictDiagnostician();
        final int[][] diagnoses = diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation.get());

        assertEquals(1, diagnoses.length);
        assertArrayEquals(new int[]{2}, diagnoses[0]);
    }

    @Test
    void testGetFirstDiagnosisForExplicitConflict() {
        final ChocoModel chocoModel = createTestModel(4);
        chocoModel.setAssignmentConstraint(new int[] { 0, 1 }, new int[] { 0, 2 });

        final int[] background = new int[] { 4, 6 };
        final int[] relaxable = new int[] {1, 2, 3, 5};

        final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                .getMinimalConflict(chocoModel, background, relaxable);

        assertTrue(explanation.isPresent());
        assertInstanceOf(InternalConflictSet.class, explanation.get());

        final ConflictDiagnostician diagnostician = new FastConflictDiagnostician();
        final int[][] diagnoses = diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation.get());

        assertEquals(1, diagnoses.length);
        assertArrayEquals(diagnoses[0], new int[]{2});
    }

    @Nested
    class MoreDetailed {

        @Test
        void testNoConflictOfMoreDetailedModel() {
            final ChocoModel chocoModel = createMoreDetailedTestModel();
            final int id = chocoModel.setAssignmentConstraint(new int[] { 0, 1 }, new int[] { 0, 1 });
            chocoModel.setNegationOfConstraint(41);

            final int[] background = new int[] { 41, id };
            final int[] relaxable = new int[] {1, 2, 3, 42, 51, 52};

            final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                    .getMinimalConflict(chocoModel, background, relaxable);

            assertFalse(explanation.isPresent());
        }

        @Test
        void testGetAllDiagnosesForExplicitConflictOfMoreDetailedModel() {
            final ChocoModel chocoModel = createMoreDetailedTestModel();
            final int id = chocoModel.setAssignmentConstraint(new int[] { 0, 1 }, new int[] { 0, 2 });
            chocoModel.setNegationOfConstraint(42);

            final int[] background = new int[] { 42, id };
            final int[] relaxable = new int[] {1, 2, 3, 41, 51, 52};

            final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                    .getMinimalConflict(chocoModel, background, relaxable);

            assertTrue(explanation.isPresent());

            final ConflictDiagnostician diagnostician = new ExhaustiveConflictDiagnostician();
            final int[][] diagnoses = diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation.get());

            assertEquals(1, diagnoses.length);
            assertArrayEquals(new int[] {2}, diagnoses[0]);
        }

        @Test
        void testGetAllDiagnosesForImplicitConflictOfMoreDetailedModel() {
            final ChocoModel chocoModel = createMoreDetailedTestModel();
            int id = chocoModel.setAssignmentConstraint(new int[] { 1 }, new int[] { 2 });
            chocoModel.setNegationOfConstraint(2);

            final int[] background = new int[] { 2, id };
            final int[] relaxable = new int[] {1, 3, 41, 42, 51, 52};

            final Optional<InternalExplanation> explanation = new QuickConflictExplainer()
                    .getMinimalConflict(chocoModel, background, relaxable);

            assertTrue(explanation.isPresent());

            final ConflictDiagnostician diagnostician = new ExhaustiveConflictDiagnostician();
            final int[][] diagnoses = diagnostician.getMinimalDiagnoses((InternalConflictSet) explanation.get());

            assertEquals(3, diagnoses.length);
            assertTrue(Arrays.stream(diagnoses)
                    .anyMatch(diagnosis -> Arrays.equals(diagnosis, new int[]{1})));
            assertTrue(Arrays.stream(diagnoses)
                    .anyMatch(diagnosis -> Arrays.equals(diagnosis, new int[]{42})));
            assertTrue(Arrays.stream(diagnoses)
                    .anyMatch(diagnosis -> Arrays.equals(diagnosis, new int[]{52})));
        }

        private ChocoModel createMoreDetailedTestModel() {
            final List<InternalConstraint> internalConstraints = new ArrayList<>();

            internalConstraints.add(
                    new InternalConstraint(1, (Model model)
                            -> model.arithm((IntVar) findVariable(model, 0).get(), "=", 2).getOpposite())
            );
            internalConstraints.add(
                    new InternalConstraint(2, (Model model)
                            -> model.arithm((IntVar) findVariable(model, 1).get(), "=", 2).getOpposite())
            );
            internalConstraints.add(
                    new InternalConstraint(3, (Model model)
                            -> model.arithm((IntVar) findVariable(model, 2).get(), "=", 2).getOpposite())
            );
            internalConstraints.add(
                    new InternalConstraint(41, (Model model)
                            -> model.and(
                            model.arithm((IntVar) findVariable(model, 0).get(), "=", 0),
                            model.arithm((IntVar) findVariable(model, 1).get(), "=", 1)
                    ).getOpposite())
            );
            internalConstraints.add(
                    new InternalConstraint(42, (Model model)
                            -> model.and(
                            model.arithm((IntVar) findVariable(model, 0).get(), "=", 0),
                            model.arithm((IntVar) findVariable(model, 1).get(), "=", 2)
                    ).getOpposite())
            );
            internalConstraints.add(
                    new InternalConstraint(51, (Model model)
                            -> model.and(
                            model.arithm((IntVar) findVariable(model, 0).get(), "=", 1),
                            model.arithm((IntVar) findVariable(model, 1).get(), "=", 0)
                    ).getOpposite())
            );
            internalConstraints.add(
                    new InternalConstraint(52, (Model model)
                            -> model.and(
                            model.arithm((IntVar) findVariable(model, 0).get(), "=", 1),
                            model.arithm((IntVar) findVariable(model, 1).get(), "=", 2)
                    ).getOpposite())
            );

            final TestModel testModel = new TestModel(2, new int[] { 3, 3, 3 }, Collections.emptyList(), Collections.emptyList());
            return new ChocoModel(testModel.getParameterSizes(), internalConstraints);
        }
    }

    @Nested
    class ExpandPathsTest {
        @Test
        void testInitialExpansion() {
            final int[] currentPath = new int[0];
            final int[] nextLabels = { 1, 2, 3 };

            final List<int[]> expansions = new ExhaustiveConflictDiagnostician().expandPaths(currentPath, nextLabels);

            Assert.assertTrue(expansions.size() == 3);
        }

        @Test
        void testExpansion() {
            final int[] currentPath = { 1 };
            final int[] nextLabels = { 2, 3 };

            final List<int[]> expansions = new ExhaustiveConflictDiagnostician().expandPaths(currentPath, nextLabels);

            Assert.assertTrue(expansions.size() == 2);
        }

        @Test
        void testEmptyExpansion() {
            final int[] currentPath = { 1 };
            final int[] nextLabels = { };

            assertThrows(IllegalArgumentException.class,
                    () -> new ExhaustiveConflictDiagnostician().expandPaths(currentPath, nextLabels));
        }
    }

    @Nested
    class IsCurrentPathAlreadyContainedByRelaxationTest {

        @Test
        void testCoverage() {
            final List<int[]> diagnoses = Collections.singletonList(new int[]{ 2 });

            assertFalse(new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[] { 1 }, diagnoses));
            assertTrue(new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[] { 2 }, diagnoses));
            assertTrue(new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[] { 1, 2 }, diagnoses));
            assertFalse(new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[] { 1, 4 }, diagnoses));
        }

        @Test
        void testNoRelaxations() {
            final List<int[]> diagnoses = Collections.emptyList();

            assertFalse(new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[] { 1 }, diagnoses));
            assertFalse(new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[] { 1, 4 }, diagnoses));
        }

        @Test
        void testNoCurrentPath() {
            final List<int[]> diagnoses = Collections.singletonList(new int[]{ 2 });

            assertThrows(IllegalArgumentException.class,
                    () -> new ExhaustiveConflictDiagnostician().isCurrentPathAlreadyCoveredByDiagnoses(new int[0], diagnoses));
        }
    }

    @Nested
    class IsSubsetTest {

        @Test
        void testIsSubset() {
            assertTrue(new ExhaustiveConflictDiagnostician().isSubset(new int[]{ 1 }, new int[]{ 1 }));
            assertFalse(new ExhaustiveConflictDiagnostician().isSubset(new int[]{ 1 }, new int[]{ 2 }));
            assertTrue(new ExhaustiveConflictDiagnostician().isSubset(new int[]{ 1 }, new int[]{ 1, 2 }));
            assertTrue(new ExhaustiveConflictDiagnostician().isSubset(new int[]{ 1, 2 }, new int[]{ 1, 2 }));
            assertFalse(new ExhaustiveConflictDiagnostician().isSubset(new int[]{ 1, 2 }, new int[]{ 1 }));
            assertFalse(new ExhaustiveConflictDiagnostician().isSubset(new int[]{ 1, 2 }, new int[]{ 1, 3 }));
        }
    }
}
