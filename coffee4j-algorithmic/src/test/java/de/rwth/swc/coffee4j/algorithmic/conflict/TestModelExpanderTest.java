package de.rwth.swc.coffee4j.algorithmic.conflict;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestModelExpanderTest {

    @Test
    void testComputeFactorForEmptyTestModel() {
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .build();

        final TestModelExpander expander = new TestModelExpander(testModel);

        assertEquals(10, expander.getFactor());
    }

    @Test
    void testComputeFactorForTestModelWith2Tuples() {
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .errorTupleLists(List.of(new TupleList(2, new int[]{0, 1}, Collections.singletonList(new int[]{1, 1}))))
                .build();

        final TestModelExpander expander = new TestModelExpander(testModel);

        assertEquals(10, expander.getFactor());
    }

    @Test
    void testComputeFactorForTestModelWith4Tuples() {
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .exclusionTupleLists(List.of(new TupleList(1, new int[]{0, 1}, Collections.singletonList(new int[]{0, 0}))))
                .errorTupleLists(List.of(new TupleList(2, new int[]{0, 1}, Collections.singletonList(new int[]{1, 1}))))
                .build();

        final TestModelExpander expander = new TestModelExpander(testModel);

        assertEquals(10, expander.getFactor());
    }

    @Test
    void testComputeFactorForTestModelWith9Tuples() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{2, 1}, new int[]{1, 0}, new int[]{1, 2})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, Collections.singletonList(new int[]{2})));
    
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final TestModelExpander expander = new TestModelExpander(testModel);

        assertEquals(10, expander.getFactor());
    }

    @Test
    void testComputeFactorForTestModelWithLargeTupleList() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(
                new int[]{0, 1},
                new int[]{1, 1},
                new int[]{2, 1},
                new int[]{3, 1},
                new int[]{5, 1},
                new int[]{1, 0},
                new int[]{1, 2},
                new int[]{1, 3},
                new int[]{1, 4},
                new int[]{2, 0})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, Collections.singletonList(new int[]{2})));
    
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(5, 5, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final TestModelExpander expander = new TestModelExpander(testModel);

        assertEquals(100, expander.getFactor());
    }

    @Test
    void testCreateExpandedTestModelForEmptyTestModel() {
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .build();


        final TestModelExpander expander = new TestModelExpander(testModel);
        final CompleteTestModel internal = expander.createExpandedTestModel();

        assertEquals(0, internal.getExclusionTupleLists().size());
        assertEquals(0, internal.getErrorTupleLists().size());
    }

    @Test
    void testCreateExpandedTestModelWithSingleTuples() {
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .exclusionTupleLists(List.of(new TupleList(1, new int[]{0, 1}, Collections.singletonList(new int[]{0, 0}))))
                .errorTupleLists(List.of(new TupleList(2, new int[]{0, 1}, Collections.singletonList(new int[]{1, 1}))))
                .build();

        final TestModelExpander expander = new TestModelExpander(testModel);
        final CompleteTestModel internal = expander.createExpandedTestModel();

        assertEquals(1, internal.getExclusionTupleLists().size());
        assertEquals(10, internal.getExclusionTupleLists().get(0).getId());
        assertEquals(1, internal.getErrorTupleLists().size());
        assertEquals(20, internal.getErrorTupleLists().get(0).getId());
    }

    @Test
    void testCreateExpandedTestModelWithTwoTuples() {
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .exclusionTupleLists(List.of(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1}))))
                .errorTupleLists(List.of(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1}))))
                .build();

        final TestModelExpander expander = new TestModelExpander(testModel);
        final CompleteTestModel internal = expander.createExpandedTestModel();

        assertEquals(2, internal.getExclusionTupleLists().size());
        assertEquals(10, internal.getExclusionTupleLists().get(0).getId());
        assertEquals(11, internal.getExclusionTupleLists().get(1).getId());
        assertEquals(2, internal.getErrorTupleLists().size());
        assertEquals(20, internal.getErrorTupleLists().get(0).getId());
        assertEquals(21, internal.getErrorTupleLists().get(1).getId());
    }

    @Test
    void testCreateExpandedTestModel() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{1, 0}, new int[]{2, 0}, new int[]{0, 1}, new int[]{0, 2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{2, 1}, new int[]{1, 0}, new int[]{1, 2})));
        errorTupleLists.add(new TupleList(3, new int[]{2}, Collections.singletonList(new int[]{2})));
    
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .parameterSizes(3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final TestModelExpander expander = new TestModelExpander(testModel);
        final CompleteTestModel internal = expander.createExpandedTestModel();

        assertEquals(0, internal.getExclusionTupleLists().size());
        assertEquals(9, internal.getErrorTupleLists().size());
        assertEquals(10, internal.getErrorTupleLists().get(0).getId());
        assertEquals(11, internal.getErrorTupleLists().get(1).getId());
    }

    @Test
    void testComputeOriginalId() {
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .exclusionTupleLists(List.of(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1}))))
                .errorTupleLists(List.of(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1}))))
                .build();

        final TestModelExpander expander = new TestModelExpander(testModel);
        final CompleteTestModel expanded = expander.createExpandedTestModel();

        assertEquals(1, expander.computeOriginalId(expanded.getExclusionTupleLists().get(0)));
        assertEquals(1, expander.computeOriginalId(expanded.getExclusionTupleLists().get(1)));
        assertEquals(2, expander.computeOriginalId(expanded.getErrorTupleLists().get(0)));
        assertEquals(2, expander.computeOriginalId(expanded.getErrorTupleLists().get(1)));
    }

    @Test
    void testComputeOriginalIndexInTupleList() {
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .exclusionTupleLists(List.of(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1}))))
                .errorTupleLists(List.of(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1}))))
                .build();

        final TestModelExpander expander = new TestModelExpander(testModel);
        final CompleteTestModel expanded = expander.createExpandedTestModel();

        assertEquals(0, expander.computeOriginalIndexInTupleList(expanded.getExclusionTupleLists().get(0)));
        assertEquals(1, expander.computeOriginalIndexInTupleList(expanded.getExclusionTupleLists().get(1)));
        assertEquals(0, expander.computeOriginalIndexInTupleList(expanded.getErrorTupleLists().get(0)));
        assertEquals(1, expander.computeOriginalIndexInTupleList(expanded.getErrorTupleLists().get(1)));
    }

    @Test
    void testCorrectTupleListsRemainCorrect() {
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .exclusionTupleLists(List.of(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1}), true)))
                .errorTupleLists(List.of(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1}), true)))
                .build();

        final TestModelExpander expander = new TestModelExpander(testModel);
        final CompleteTestModel internal = expander.createExpandedTestModel();

        assertTrue(internal.getExclusionTupleLists().get(0).isMarkedAsCorrect());
        assertTrue(internal.getExclusionTupleLists().get(1).isMarkedAsCorrect());
        assertTrue(internal.getErrorTupleLists().get(0).isMarkedAsCorrect());
        assertTrue(internal.getErrorTupleLists().get(1).isMarkedAsCorrect());
    }
}
