package de.rwth.swc.coffee4j.algorithmic.conflict;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis.ExhaustiveConflictDiagnostician;
import de.rwth.swc.coffee4j.algorithmic.conflict.explanation.QuickConflictExplainer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.rwth.swc.coffee4j.algorithmic.AssertUtils.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.testng.Assert.assertTrue;

class ConflictDetectionManagerTest {

    @Test
    void testMinimalConflictExplanation() {
        final ConflictDetectionConfiguration configuration = new ConflictDetectionConfiguration(
                true,
                false,
                true,
                QuickConflictExplainer::new,
                false,
                ExhaustiveConflictDiagnostician::new);

        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, List.of(new int[]{0, 1}, new int[]{2, 1}, new int[]{1, 0}, new int[]{1, 2})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, List.of(new int[]{2})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConflictDetectionManager manager = new ConflictDetectionManager(configuration, model);
        final List<MissingInvalidTuple> missingInvalidTuples = manager.detectMissingInvalidTuples();

        assertEquals(4, missingInvalidTuples.size());

        assertTrue(missingInvalidTuples.stream().anyMatch(mit -> mit.getNegatedErrorConstraintId() == 1
                && Arrays.equals(new int[]{0, 1}, mit.getInvolvedParameters())
                && Arrays.equals(new int[]{1, 0}, mit.getMissingValues())
                && mit.getExplanation() instanceof ConflictSet
                && ((ConflictSet) mit.getExplanation()).getConflictElements().size() == 1
                && ((ConflictSet) mit.getExplanation()).getConflictElements().get(0).getConflictingConstraintId() == 2
                && Arrays.equals(new int[] {0, 1} ,((ConflictSet) mit.getExplanation()).getConflictElements().get(0).getInvolvedParameters())
                && Arrays.equals(new int[] {1, 0} ,((ConflictSet) mit.getExplanation()).getConflictElements().get(0).getConflictingValues())
        ));

        assertTrue(missingInvalidTuples.stream().anyMatch(mit -> mit.getNegatedErrorConstraintId() == 1
                && Arrays.equals(new int[]{0, 1}, mit.getInvolvedParameters())
                && Arrays.equals(new int[]{0, 1}, mit.getMissingValues())
                && mit.getExplanation() instanceof ConflictSet
                && ((ConflictSet) mit.getExplanation()).getConflictElements().size() == 1
                && ((ConflictSet) mit.getExplanation()).getConflictElements().get(0).getConflictingConstraintId() == 2
                && Arrays.equals(new int[] {0, 1} ,((ConflictSet) mit.getExplanation()).getConflictElements().get(0).getInvolvedParameters())
                && Arrays.equals(new int[] {0, 1} ,((ConflictSet) mit.getExplanation()).getConflictElements().get(0).getConflictingValues())
        ));

        assertTrue(missingInvalidTuples.stream().anyMatch(mit -> mit.getNegatedErrorConstraintId() == 2
                && Arrays.equals(new int[]{0, 1}, mit.getInvolvedParameters())
                && Arrays.equals(new int[]{1, 0}, mit.getMissingValues())
                && mit.getExplanation() instanceof ConflictSet
                && ((ConflictSet) mit.getExplanation()).getConflictElements().size() == 1
                && ((ConflictSet) mit.getExplanation()).getConflictElements().get(0).getConflictingConstraintId() == 1
                && Arrays.equals(new int[] {0, 1} ,((ConflictSet) mit.getExplanation()).getConflictElements().get(0).getInvolvedParameters())
                && Arrays.equals(new int[] {1, 0} ,((ConflictSet) mit.getExplanation()).getConflictElements().get(0).getConflictingValues())
        ));

        assertTrue(missingInvalidTuples.stream().anyMatch(mit -> mit.getNegatedErrorConstraintId() == 2
                && Arrays.equals(new int[]{0, 1}, mit.getInvolvedParameters())
                && Arrays.equals(new int[]{0, 1}, mit.getMissingValues())
                && mit.getExplanation() instanceof ConflictSet
                && ((ConflictSet) mit.getExplanation()).getConflictElements().size() == 1
                && ((ConflictSet) mit.getExplanation()).getConflictElements().get(0).getConflictingConstraintId() == 1
                && Arrays.equals(new int[] {0, 1} ,((ConflictSet) mit.getExplanation()).getConflictElements().get(0).getInvolvedParameters())
                && Arrays.equals(new int[] {0, 1} ,((ConflictSet) mit.getExplanation()).getConflictElements().get(0).getConflictingValues())
        ));
    }

    @Test
    void testNoConflictExplanation() {
        final ConflictDetectionConfiguration configuration = new ConflictDetectionConfiguration(
                true,
                false,
                true,
                QuickConflictExplainer::new,
                true,
                ExhaustiveConflictDiagnostician::new);

        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{2}, List.of(new int[]{2})));
    
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConflictDetectionManager manager = new ConflictDetectionManager(configuration, model);
        final List<MissingInvalidTuple> missingInvalidTuples = manager.detectMissingInvalidTuples();

        assertEquals(0, missingInvalidTuples.size());
    }

    @Test
    void testCTA2019SoundExample() {
        final ConflictDetectionConfiguration configuration = new ConflictDetectionConfiguration(
                true,
                false,
                true,
                QuickConflictExplainer::new,
                true,
                ExhaustiveConflictDiagnostician::new);

        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, List.of(new int[]{2})));       // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, List.of(new int[]{2})));       // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, List.of(new int[]{2})));       // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, List.of(new int[]{0, 1}))); // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, List.of(new int[]{1, 0}))); // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]
    
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConflictDetectionManager manager = new ConflictDetectionManager(configuration, model);
        final List<MissingInvalidTuple> missingInvalidTuples = manager.detectMissingInvalidTuples();

        assertEquals(0, missingInvalidTuples.size());
    }

    @Test
    void testCTA2019ExampleWithCorrectConstraint() {
        final ConflictDetectionConfiguration configuration = new ConflictDetectionConfiguration(
                true,
                false,
                true,
                QuickConflictExplainer::new,
                true,
                ExhaustiveConflictDiagnostician::new);

        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, List.of(new int[]{2})));                       // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, List.of(new int[]{2}), true));   // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, List.of(new int[]{2})));                       // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, List.of(new int[]{0, 1}, new int[]{0, 2})));            // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{1, 2})));            // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]
    
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConflictDetectionManager manager = new ConflictDetectionManager(configuration, model);
        final List<MissingInvalidTuple> missingInvalidTuples = manager.detectMissingInvalidTuples();

        MissingInvalidTuple mit;
        DiagnosisSets diagnosisSets;
        DiagnosisSet diagnosisSet;

        // getNegatedErrorConstraintId() == 2
        mit = missingInvalidTuples.stream().filter(tuple -> tuple.getNegatedErrorConstraintId() == 2).findFirst().orElseThrow(IllegalStateException::new);
        assertArrayEquals(new int[] { 1 }, mit.getInvolvedParameters());
        assertArrayEquals(new int[] { 2 }, mit.getMissingValues());
        assertInstanceOf(DiagnosisSets.class, mit.getExplanation());

        diagnosisSets = (DiagnosisSets) mit.getExplanation();
        assertEquals(3, diagnosisSets.getDiagnosisSets().size());

        diagnosisSet = diagnosisSets.getDiagnosisSets().get(0);
        assertEquals(1, diagnosisSet.getDiagnosisElements().size());
        assertTrue(diagnosisSet.getDiagnosisElements().stream().anyMatch(element ->
                element.getDiagnosedConstraintId() == 1
                        && Arrays.equals(new int[] { 0 }, element.getInvolvedParameters())
                        && Arrays.equals(new int[] { 2 }, element.getConflictingValues())));

        diagnosisSet = diagnosisSets.getDiagnosisSets().get(1);
        assertEquals(1, diagnosisSet.getDiagnosisElements().size());
        assertTrue(diagnosisSet.getDiagnosisElements().stream().anyMatch(element ->
                element.getDiagnosedConstraintId() == 4
                        && Arrays.equals(new int[] { 0, 1 }, element.getInvolvedParameters())
                        && Arrays.equals(new int[] { 0, 2 }, element.getConflictingValues())));

        diagnosisSet = diagnosisSets.getDiagnosisSets().get(2);
        assertEquals(1, diagnosisSet.getDiagnosisElements().size());
        assertTrue(diagnosisSet.getDiagnosisElements().stream().anyMatch(element ->
                element.getDiagnosedConstraintId() == 5
                        && Arrays.equals(new int[] { 0, 1 }, element.getInvolvedParameters())
                        && Arrays.equals(new int[] { 1, 2 }, element.getConflictingValues())));

        // getNegatedErrorConstraintId() == 4
        mit = missingInvalidTuples.stream().filter(tuple -> tuple.getNegatedErrorConstraintId() == 4).findFirst().orElseThrow(IllegalStateException::new);
        assertArrayEquals(new int[] { 0, 1 }, mit.getInvolvedParameters());
        assertArrayEquals(new int[] { 0, 2 }, mit.getMissingValues());
        assertInstanceOf(InconsistentBackground.class, mit.getExplanation());

        // getNegatedErrorConstraintId() == 5
        mit = missingInvalidTuples.stream().filter(tuple -> tuple.getNegatedErrorConstraintId() == 5).findFirst().orElseThrow(IllegalStateException::new);
        assertArrayEquals(new int[] { 0, 1 }, mit.getInvolvedParameters());
        assertArrayEquals(new int[] { 1, 2 }, mit.getMissingValues());
        assertInstanceOf(InconsistentBackground.class, mit.getExplanation());
    }

    @Test
    void testCTA2019ExampleWithInconsistentBackground() {
        final ConflictDetectionConfiguration configuration = new ConflictDetectionConfiguration(
                true,
                false,
                true,
                QuickConflictExplainer::new,
                true,
                ExhaustiveConflictDiagnostician::new);

        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, List.of(new int[]{2}), true));  // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, List.of(new int[]{2})));                      // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, List.of(new int[]{2})));                      // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, List.of(new int[]{0, 1}, new int[]{0, 2}), true));    // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{1, 2}), true));    // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]
    
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConflictDetectionManager manager = new ConflictDetectionManager(configuration, model);
        final List<MissingInvalidTuple> missingInvalidTuples = manager.detectMissingInvalidTuples();

        assertEquals(3, missingInvalidTuples.size());

        MissingInvalidTuple mit;
        DiagnosisSets diagnosisSets;
        DiagnosisSet diagnosisSet;

        // getNegatedErrorConstraintId() == 2
        mit = missingInvalidTuples.stream().filter(tuple -> tuple.getNegatedErrorConstraintId() == 2).findFirst().orElseThrow(IllegalStateException::new);
        assertArrayEquals(new int[] { 1 }, mit.getInvolvedParameters());
        assertArrayEquals(new int[] { 2 }, mit.getMissingValues());
        assertInstanceOf(InconsistentBackground.class, mit.getExplanation());

        // getNegatedErrorConstraintId() == 4
        mit = missingInvalidTuples.stream().filter(tuple -> tuple.getNegatedErrorConstraintId() == 4).findFirst().orElseThrow(IllegalStateException::new);
        assertArrayEquals(new int[] { 0, 1 }, mit.getInvolvedParameters());
        assertArrayEquals(new int[] { 0, 2 }, mit.getMissingValues());
        assertInstanceOf(DiagnosisSets.class, mit.getExplanation());

        diagnosisSets = (DiagnosisSets) mit.getExplanation();
        assertEquals(1, diagnosisSets.getDiagnosisSets().size());

        diagnosisSet = diagnosisSets.getDiagnosisSets().get(0);
        assertEquals(1, diagnosisSet.getDiagnosisElements().size());
        assertTrue(diagnosisSet.getDiagnosisElements().stream().anyMatch(element ->
                element.getDiagnosedConstraintId() == 2
                        && Arrays.equals(new int[] { 1 }, element.getInvolvedParameters())
                        && Arrays.equals(new int[] { 2 }, element.getConflictingValues())));

        // getNegatedErrorConstraintId() == 5
        mit = missingInvalidTuples.stream().filter(tuple -> tuple.getNegatedErrorConstraintId() == 5).findFirst().orElseThrow(IllegalStateException::new);
        assertArrayEquals(new int[] { 0, 1 }, mit.getInvolvedParameters());
        assertArrayEquals(new int[] { 1, 2 }, mit.getMissingValues());
        assertInstanceOf(DiagnosisSets.class, mit.getExplanation());

        diagnosisSets = (DiagnosisSets) mit.getExplanation();
        assertEquals(1, diagnosisSets.getDiagnosisSets().size());

        diagnosisSet = diagnosisSets.getDiagnosisSets().get(0);
        assertEquals(1, diagnosisSet.getDiagnosisElements().size());
        assertTrue(diagnosisSet.getDiagnosisElements().stream().anyMatch(element ->
                element.getDiagnosedConstraintId() == 2
                        && Arrays.equals(new int[] { 1 }, element.getInvolvedParameters())
                        && Arrays.equals(new int[] { 2 }, element.getConflictingValues())));
    }

    @Test
    void testCTA2019ExampleWithDetectionExplanationAndDiagnosis() {
        final ConflictDetectionConfiguration configuration = new ConflictDetectionConfiguration(
                true,
                false,
                true,
                QuickConflictExplainer::new,
                true,
                ExhaustiveConflictDiagnostician::new);

        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, List.of(new int[]{2})));               // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, List.of(new int[]{2})));               // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, List.of(new int[]{2})));               // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, List.of(new int[]{0, 1}, new int[]{0, 2})));    // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{1, 2})));    // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]
    
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConflictDetectionManager manager = new ConflictDetectionManager(configuration, model);
        final List<MissingInvalidTuple> missingInvalidTuples = manager.detectMissingInvalidTuples();

        assertEquals(3, missingInvalidTuples.size());

        MissingInvalidTuple mit;
        DiagnosisSets diagnosisSets;
        DiagnosisSet diagnosisSet;

        // getNegatedErrorConstraintId() == 2
        mit = missingInvalidTuples.stream().filter(tuple -> tuple.getNegatedErrorConstraintId() == 2).findFirst().orElseThrow(IllegalStateException::new);
        assertArrayEquals(new int[] { 1 }, mit.getInvolvedParameters());
        assertArrayEquals(new int[] { 2 }, mit.getMissingValues());
        assertInstanceOf(DiagnosisSets.class, mit.getExplanation());

        diagnosisSets = (DiagnosisSets) mit.getExplanation();
        assertEquals(3, diagnosisSets.getDiagnosisSets().size());

        diagnosisSet = diagnosisSets.getDiagnosisSets().get(0);
        assertEquals(1, diagnosisSet.getDiagnosisElements().size());
        assertTrue(diagnosisSet.getDiagnosisElements().stream().anyMatch(element ->
                element.getDiagnosedConstraintId() == 1
                        && Arrays.equals(new int[] { 0 }, element.getInvolvedParameters())
                        && Arrays.equals(new int[] { 2 }, element.getConflictingValues())));

        diagnosisSet = diagnosisSets.getDiagnosisSets().get(1);
        assertEquals(1, diagnosisSet.getDiagnosisElements().size());
        assertTrue(diagnosisSet.getDiagnosisElements().stream().anyMatch(element ->
                element.getDiagnosedConstraintId() == 4
                        && Arrays.equals(new int[] { 0, 1 }, element.getInvolvedParameters())
                        && Arrays.equals(new int[] { 0, 2 }, element.getConflictingValues())));

        diagnosisSet = diagnosisSets.getDiagnosisSets().get(2);
        assertEquals(1, diagnosisSet.getDiagnosisElements().size());
        assertTrue(diagnosisSet.getDiagnosisElements().stream().anyMatch(element ->
                element.getDiagnosedConstraintId() == 5
                        && Arrays.equals(new int[] { 0, 1 }, element.getInvolvedParameters())
                        && Arrays.equals(new int[] { 1, 2 }, element.getConflictingValues())));

        // getNegatedErrorConstraintId() == 4
        mit = missingInvalidTuples.stream().filter(tuple -> tuple.getNegatedErrorConstraintId() == 4).findFirst().orElseThrow(IllegalStateException::new);
        assertArrayEquals(new int[] { 0, 1 }, mit.getInvolvedParameters());
        assertArrayEquals(new int[] { 0, 2 }, mit.getMissingValues());
        assertInstanceOf(DiagnosisSets.class, mit.getExplanation());

        diagnosisSets = (DiagnosisSets) mit.getExplanation();
        assertEquals(1, diagnosisSets.getDiagnosisSets().size());

        diagnosisSet = diagnosisSets.getDiagnosisSets().get(0);
        assertEquals(1, diagnosisSet.getDiagnosisElements().size());
        assertTrue(diagnosisSet.getDiagnosisElements().stream().anyMatch(element ->
                element.getDiagnosedConstraintId() == 2
                        && Arrays.equals(new int[] { 1 }, element.getInvolvedParameters())
                        && Arrays.equals(new int[] { 2 }, element.getConflictingValues())));

        // getNegatedErrorConstraintId() == 5
        mit = missingInvalidTuples.stream().filter(tuple -> tuple.getNegatedErrorConstraintId() == 5).findFirst().orElseThrow(IllegalStateException::new);
        assertArrayEquals(new int[] { 0, 1 }, mit.getInvolvedParameters());
        assertArrayEquals(new int[] { 1, 2 }, mit.getMissingValues());
        assertInstanceOf(DiagnosisSets.class, mit.getExplanation());

        diagnosisSets = (DiagnosisSets) mit.getExplanation();
        assertEquals(1, diagnosisSets.getDiagnosisSets().size());

        diagnosisSet = diagnosisSets.getDiagnosisSets().get(0);
        assertEquals(1, diagnosisSet.getDiagnosisElements().size());
        assertTrue(diagnosisSet.getDiagnosisElements().stream().anyMatch(element ->
                element.getDiagnosedConstraintId() == 2
                        && Arrays.equals(new int[] { 1 }, element.getInvolvedParameters())
                        && Arrays.equals(new int[] { 2 }, element.getConflictingValues())));
    }

    @Test
    void testCTA2019ExampleWithDetectionExplanationButNoDiagnosis() {
        final ConflictDetectionConfiguration configuration = new ConflictDetectionConfiguration(
                true,
                false,
                true,
                QuickConflictExplainer::new,
                false,
                () -> null);

        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, List.of(new int[]{2})));               // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, List.of(new int[]{2})));               // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, List.of(new int[]{2})));               // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, List.of(new int[]{0, 1}, new int[]{0, 2})));    // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{1, 2})));    // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]
    
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConflictDetectionManager manager = new ConflictDetectionManager(configuration, model);
        final List<MissingInvalidTuple> missingInvalidTuples = manager.detectMissingInvalidTuples();

        assertEquals(3, missingInvalidTuples.size());

        assertInstanceOf(ConflictSet.class, findMIT(missingInvalidTuples, 2).getExplanation());
        assertArrayEquals(new int[] {2}, findMIT(missingInvalidTuples, 2).getMissingValues());

        assertTrue(((ConflictSet) findMIT(missingInvalidTuples, 2).getExplanation())
                        .getConflictElements()
                        .stream()
                        .anyMatch(conflict ->
                                conflict.getConflictingConstraintId() == 1
                                && Arrays.equals(new int[] {0}, conflict.getInvolvedParameters())
                                && Arrays.equals(new int[] {2}, conflict.getConflictingValues()))
        );
        assertTrue(((ConflictSet) findMIT(missingInvalidTuples, 2).getExplanation())
                        .getConflictElements()
                        .stream()
                        .anyMatch(conflict ->
                                conflict.getConflictingConstraintId() == 4
                                        && Arrays.equals(new int[] {0, 1}, conflict.getInvolvedParameters())
                                        && Arrays.equals(new int[] {0, 2}, conflict.getConflictingValues()))
        );
        assertTrue(((ConflictSet) findMIT(missingInvalidTuples, 2).getExplanation())
                        .getConflictElements()
                        .stream()
                        .anyMatch(conflict ->
                                conflict.getConflictingConstraintId() == 5
                                        && Arrays.equals(new int[] {0, 1}, conflict.getInvolvedParameters())
                                        && Arrays.equals(new int[] {1, 2}, conflict.getConflictingValues()))
        );
    }

    @Test
    void testCTA2019ExampleWithDetectionButNoExplanationAndNoDiagnosis() {
        final ConflictDetectionConfiguration configuration = new ConflictDetectionConfiguration(
                true,
                false,
                false,
                null,
                false,
                null);

        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, List.of(new int[]{2})));               // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, List.of(new int[]{2})));               // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, List.of(new int[]{2})));               // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, List.of(new int[]{0, 1}, new int[]{0, 2})));    // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{1, 2})));    // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]
    
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final ConflictDetectionManager manager = new ConflictDetectionManager(configuration, model);
        final List<MissingInvalidTuple> missingInvalidTuples = manager.detectMissingInvalidTuples();

        assertEquals(3, missingInvalidTuples.size());

        assertInstanceOf(UnknownConflictExplanation.class, findMIT(missingInvalidTuples, 2).getExplanation());
        assertArrayEquals(new int[] {2}, findMIT(missingInvalidTuples, 2).getMissingValues());

        assertInstanceOf(UnknownConflictExplanation.class, findMIT(missingInvalidTuples, 4).getExplanation());
        assertArrayEquals(new int[] {0, 2}, findMIT(missingInvalidTuples, 4).getMissingValues());

        assertInstanceOf(UnknownConflictExplanation.class, findMIT(missingInvalidTuples, 5).getExplanation());
        assertArrayEquals(new int[] {1, 2}, findMIT(missingInvalidTuples, 5).getMissingValues());
    }

    @Test
    void testCTA2019ExampleWithNoDetectionNoExplanationAndNoDiagnosis() {
        final ConflictDetectionConfiguration configuration = new ConflictDetectionConfiguration(
                false,
                false,
                false,
                null,
                false,
                null);

        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, List.of(new int[]{2})));               // [Title:123]
        errorTupleLists.add(new TupleList(2, new int[]{1}, List.of(new int[]{2})));               // [GivenName:123]
        errorTupleLists.add(new TupleList(3, new int[]{2}, List.of(new int[]{2})));               // [FamilyName:123]
        errorTupleLists.add(new TupleList(4, new int[]{0, 1}, List.of(new int[]{0, 1}, new int[]{0, 2})));    // [Title:Mr,GivenName:Jane], [Title:Mr,GivenName:123]
        errorTupleLists.add(new TupleList(5, new int[]{0, 1}, List.of(new int[]{1, 0}, new int[]{1, 2})));    // [Title:Mrs,GivenName:John], [Title:Mrs,GivenName:123]
    
        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();
        
        final ConflictDetectionManager manager = new ConflictDetectionManager(configuration, model);
        final List<MissingInvalidTuple> conflicts = manager.detectMissingInvalidTuples();

        assertEquals(0, conflicts.size());
    }

    private MissingInvalidTuple findMIT(List<MissingInvalidTuple> missingInvalidTuples, int negatedErrorConstraintId) {
        return missingInvalidTuples.stream()
                .filter(tuple -> tuple.getNegatedErrorConstraintId() == negatedErrorConstraintId)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }
}
