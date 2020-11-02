package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm;

import de.rwth.swc.coffee4j.algorithmic.constraint.ExistentialHardConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.constraint.ExistentialMinimalForbiddenTuplesCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.constraint.HardConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.report.LoggingReporter;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.IpogNegConfiguration;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintViolationAssertions.assertExactNumberOfErrorConstraintViolations;
import static de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintViolationAssertions.assertNoExclusionConstraintViolations;
import static de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil.contains;
import static org.junit.jupiter.api.Assertions.*;

class IpogNegTest {

    @Test
    void onlyErrorTuplesAppear() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Collections.singletonList(new int[]{0, 0})));
        errorTupleLists.add(new TupleList(2, new int[]{1, 2}, Collections.singletonList(new int[]{1, 1})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .negativeTestingStrength(2)
                .parameterSizes(2, 2, 2, 2)
                .errorTupleLists(errorTupleLists)
                .build();

        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new HardConstraintCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator.generate(model, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());
        assertEquals(errorTupleLists.get(0), testInputGroups.get(0).getIdentifier());

        final List<int[]> firstTestInputs = testInputGroups.get(0).getTestInputs();
        assertFalse(firstTestInputs.isEmpty());
        assertTrue(firstTestInputs.stream().allMatch(testInput -> contains(testInput, new int[]{0, 0, -1, -1})));
        assertEquals(errorTupleLists.get(1), testInputGroups.get(1).getIdentifier());

        final List<int[]> secondTestInputs = testInputGroups.get(1).getTestInputs();
        assertFalse(secondTestInputs.isEmpty());
        assertTrue(secondTestInputs.stream().allMatch(testInput -> contains(testInput, new int[]{-1, 1, 1, -1})));
    }

    @Test
    void testImplicitConflictsDoNotAppear() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})));
        errorTupleLists.add(new TupleList(2, new int[]{1, 2}, Collections.singletonList(new int[]{1, 1})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .negativeTestingStrength(2)
                .parameterSizes(2, 2, 2, 2)
                .errorTupleLists(errorTupleLists)
                .build();

        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new HardConstraintCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator.generate(model, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());
        assertEquals(errorTupleLists.get(0), testInputGroups.get(0).getIdentifier());

        final List<int[]> firstTestInputs = testInputGroups.get(0).getTestInputs();
        assertTrue(firstTestInputs.stream().allMatch(testInput -> contains(testInput, new int[]{0, 0, -1, -1})
                || contains(testInput, new int[]{1, 1, -1, -1})));
        assertTrue(firstTestInputs.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 0, -1, -1})));
        assertTrue(firstTestInputs.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 1, -1, -1})));
        assertEquals(errorTupleLists.get(1), testInputGroups.get(1).getIdentifier());

        final List<int[]> secondTestInputs = testInputGroups.get(1).getTestInputs();
        assertTrue(secondTestInputs.stream().allMatch(testInput -> contains(testInput, new int[]{-1, 1, 1, -1})));
    }

    @Test
    void testConflictsDoNotAppear() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{1}, Collections.singletonList(new int[]{2})));
        errorTupleLists.add(new TupleList(2, new int[]{0, 1}, Arrays.asList(new int[]{0, 1}, new int[]{0, 2}, new int[]{1, 0}, new int[]{1, 2})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .negativeTestingStrength(2)
                .parameterSizes(2, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new HardConstraintCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator.generate(model, new LoggingReporter()).stream().map(Supplier::get).collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());
        assertEquals(errorTupleLists.get(0), testInputGroups.get(0).getIdentifier());

        final List<int[]> firstTestInputs = testInputGroups.get(0).getTestInputs();
        assertTrue(firstTestInputs.isEmpty());
        assertEquals(errorTupleLists.get(1), testInputGroups.get(1).getIdentifier());

        final List<int[]> secondTestInputs = testInputGroups.get(1).getTestInputs();
        assertFalse(secondTestInputs.isEmpty());
        assertTrue(secondTestInputs.stream().allMatch(testInput -> contains(testInput, new int[]{0, 1, -1})
                || contains(testInput, new int[]{1, 0, -1})));
    }

    @Test
    void testOnlyErrorConstraintUnsatisfied() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Collections.singletonList(new int[]{0, 0})));
        errorTupleLists.add(new TupleList(2, new int[]{1, 2}, Collections.singletonList(new int[]{1, 1})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .negativeTestingStrength(2)
                .parameterSizes(2, 2, 2, 2)
                .errorTupleLists(errorTupleLists)
                .build();

        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new HardConstraintCheckerFactory(), 2)
        );
        final List<TestInputGroup> testInputGroups = generator.generate(model, new LoggingReporter()).stream().map(Supplier::get).collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        for (TestInputGroup group : testInputGroups) {
            for (int[] tuple : group.getTestInputs()) {
                assertNoExclusionConstraintViolations(model, tuple);
                assertExactNumberOfErrorConstraintViolations(model, tuple, 1);
            }
        }
    }

    private CompleteTestModel model(int strengthB) {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0}, List.of(new int[]{3})));
        errorTupleLists.add(new TupleList(2, new int[]{1, 2},
                List.of(new int[]{0, 1}, new int[]{0, 2}, new int[]{1, 0}, new int[]{1, 2}, new int[]{2, 0}, new int[]{2, 1})));

        return CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .negativeTestingStrength(strengthB)
                .parameterSizes(4, 4, 3, 2)
                .errorTupleLists(errorTupleLists)
                .build();
    }

    @Test
    void testExistentialHardConstraintCheckerB0() {
        final CompleteTestModel testModel = model(0);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new ExistentialHardConstraintCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(1, testSuite.size());
        assertTrue(contains(testSuite.get(0), new int[]{3, -1, -1, -1}));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(1, testSuite.size());
        assertTrue(contains(testSuite.get(0), new int[]{-1, 0, 1, -1}));
    }

    @Test
    void testExistentialHardConstraintCheckerB1() {
        final CompleteTestModel testModel = model(1);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new ExistentialHardConstraintCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(4, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));
        // all b-wise interactions for p1
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, -1, -1})));
        // all b-wise interactions for p2
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 2, -1})));
        // all b-wise interactions for p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, -1, 1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(3, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[]{-1, 0, 1, -1})));
        // all b-wise interactions for p0
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, 1, -1})));
        // all b-wise interactions for p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {-1, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {-1, 0, 1, 1})));
    }

    @Test
    void testExistentialHardConstraintCheckerB2() {
        final CompleteTestModel testModel = model(2);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new ExistentialHardConstraintCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(9, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));
        // all b-wise interactions for p1, p2
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 2, -1})));

        // all b-wise interactions for p1, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, -1, 1})));

        // all b-wise interactions for p2, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 2, 1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(6, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[]{-1, 0, 1, -1})));
        // all b-wise interactions for p0, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, 1, 1})));
    }

    @Test
    void testExistentialHardConstraintCheckerB3() {
        final CompleteTestModel testModel = model(3);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new ExistentialHardConstraintCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(12, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));
        // all b-wise interactions for p1, p2, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 2, 1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(6, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[]{-1, 0, 1, -1})));
        // all b-wise interactions for p0, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, 1, 1})));
    }

    @Test
    void testExistentialMinimalForbiddenTuplesCheckerB0() {
        final CompleteTestModel testModel = model(0);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new ExistentialMinimalForbiddenTuplesCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(1, testSuite.size());
        assertTrue(contains(testSuite.get(0), new int[]{3, -1, -1, -1}));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(1, testSuite.size());
        assertTrue(contains(testSuite.get(0), new int[]{-1, 0, 1, -1}));
    }

    @Test
    void testExistentialMinimalForbiddenTuplesCheckerB1() {
        final CompleteTestModel testModel = model(1);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new ExistentialMinimalForbiddenTuplesCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(4, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));
        // all b-wise interactions for p1
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, -1, -1})));
        // all b-wise interactions for p2
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 2, -1})));
        // all b-wise interactions for p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, -1, 1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(3, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[]{-1, 0, 1, -1})));
        // all b-wise interactions for p0
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, 1, -1})));
        // all b-wise interactions for p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {-1, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {-1, 0, 1, 1})));
    }

    @Test
    void testExistentialMinimalForbiddenTuplesCheckerB2() {
        final CompleteTestModel testModel = model(2);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new ExistentialMinimalForbiddenTuplesCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(9, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));
        // all b-wise interactions for p1, p2
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 2, -1})));

        // all b-wise interactions for p1, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, -1, 1})));

        // all b-wise interactions for p2, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 2, 1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(6, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[]{-1, 0, 1, -1})));
        // all b-wise interactions for p0, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, 1, 1})));
    }

    @Test
    void testExistentialMinimalForbiddenTuplesCheckerB3() {
        final CompleteTestModel testModel = model(3);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new ExistentialMinimalForbiddenTuplesCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(12, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));
        // all b-wise interactions for p1, p2, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 2, 1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(6, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[]{-1, 0, 1, -1})));
        // all b-wise interactions for p0, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, 1, 1})));
    }

    @Test
    void testA1B0() {
        final CompleteTestModel testModel = model(0);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new HardConstraintCheckerFactory(), 1)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(1, testSuite.size());
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(4, testSuite.size());
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 0, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 1, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 2, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, -1, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, -1, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, -1, 2, -1})));
    }

    @Test
    void testA2B0() {
        final CompleteTestModel testModel = model(0);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new HardConstraintCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(1, testSuite.size());
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(6, testSuite.size());
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 0, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 0, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 1, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 1, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 2, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 2, 1, -1})));
    }

    @Test
    void testA1B1() {
        final CompleteTestModel testModel = model(1);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new HardConstraintCheckerFactory(), 1)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(4, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));
        // all b-wise interactions for p1
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, -1, -1})));
        // all b-wise interactions for p2
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 2, -1})));
        // all b-wise interactions for p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, -1, 1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(15, testSuite.size());
        // all b-wise interactions for p0
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 0, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 1, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 2, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, -1, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, -1, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, -1, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 0, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 1, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 2, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, -1, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, -1, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, -1, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 0, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 1, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 2, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, -1, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, -1, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, -1, 2, -1})));

        // all b-wise interactions for p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 0, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 2, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, -1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, -1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, -1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 0, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 1, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 2, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, -1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, -1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, -1, 2, 1})));
    }

    @Test
    void testA2B1() {
        final CompleteTestModel testModel = model(1);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new HardConstraintCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(4, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));
        // all b-wise interactions for p1
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, -1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, -1, -1})));
        // all b-wise interactions for p2
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 2, -1})));
        // all b-wise interactions for p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, -1, 1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(18, testSuite.size());
        // all b-wise interactions for p0
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 0, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 0, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 1, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 1, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 2, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 2, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 0, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 0, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 1, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 1, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 2, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 2, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 0, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 0, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 1, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 1, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 2, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 2, 1, -1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 0, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 2, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 2, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 0, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 1, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 2, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{-1, 2, 1, 1})));
    }

    @Test
    void testA1B2() {
        final CompleteTestModel testModel = model(2);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new HardConstraintCheckerFactory(), 1)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(9, testSuite.size());
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));
        // all b-wise interactions for p1, p2
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 2, -1})));

        // all b-wise interactions for p1, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, -1, 1})));

        // all b-wise interactions for p2, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 2, 1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(24, testSuite.size());

        // all b-wise interactions for p0, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 1, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 1, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 1, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 2, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 2, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 2, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 2, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 2, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 2, -1, 1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, -1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, -1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, -1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, -1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, -1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, -1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, -1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, -1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, -1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, -1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, -1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, -1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, -1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, -1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, -1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, -1, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, -1, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, -1, 2, 1})));
    }

    @Test
    void testA2B2() {
        final CompleteTestModel testModel = model(2);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new HardConstraintCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(9, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));
        // all b-wise interactions for p1, p2
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, 2, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 0, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 1, -1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 2, -1})));

        // all b-wise interactions for p1, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, -1, 1})));

        // all b-wise interactions for p2, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, 2, 1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(36, testSuite.size());
        // all b-wise interactions for p0, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 0, 1, 1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 0, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 0, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 0, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 0, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 0, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 0, 2, 1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 1, 0, 1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 1, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 1, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 1, 2, 1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 2, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 2, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 2, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 2, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 2, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 2, 0, 1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 2, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 2, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 2, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 2, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 2, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 2, 1, 1})));
    }

    @Test
    void testA1B3() {
        final CompleteTestModel testModel = model(3);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new HardConstraintCheckerFactory(), 1)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(12, testSuite.size());
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));
        // all b-wise interactions for p1, p2, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 2, 1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(24, testSuite.size());

        // all b-wise interactions for p0, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 0, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 0, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 0, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 1, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 1, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 1, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 1, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 2, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 2, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 2, -1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, 2, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, 2, -1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, 2, -1, 1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, -1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, -1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, -1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, -1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, -1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, -1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, -1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, -1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, -1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, -1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, -1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, -1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, -1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, -1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, -1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {0, -1, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {1, -1, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {2, -1, 2, 1})));
    }

    @Test
    void testA2B3() {
        final CompleteTestModel testModel = model(3);
        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new HardConstraintCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(testModel, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        List<int[]> testSuite;

        testSuite = testInputGroups.get(0).getTestInputs();
        assertEquals(12, testSuite.size());
        assertTrue(testSuite.stream().allMatch(testInput -> contains(testInput, new int[] {3, -1, -1, -1})));
        // all b-wise interactions for p1, p2, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 0, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 1, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 2, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[] {3, 3, 2, 1})));

        testSuite = testInputGroups.get(1).getTestInputs();
        assertEquals(36, testSuite.size());
        // all b-wise interactions for p0, p3
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 0, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 0, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 0, 1, 1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 0, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 0, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 0, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 0, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 0, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 0, 2, 1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 1, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 1, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 1, 0, 1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 1, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 1, 2, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 1, 2, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 1, 2, 1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 2, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 2, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 2, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 2, 0, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 2, 0, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 2, 0, 1})));

        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 2, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{0, 2, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 2, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{1, 2, 1, 1})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 2, 1, 0})));
        assertTrue(testSuite.stream().anyMatch(testInput -> contains(testInput, new int[]{2, 2, 1, 1})));
    }

    @Test
    void testNegativeTestingStrengthZero() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, Collections.singletonList(new int[]{0, 0})));
        errorTupleLists.add(new TupleList(2, new int[]{1, 2}, Collections.singletonList(new int[]{1, 1})));
        final List<TupleList> exclusionTupleLists = new ArrayList<>();
        exclusionTupleLists.add(new TupleList(3, new int[]{0, 1, 2}, Collections.singletonList(new int[]{0, 0, 0})));
        exclusionTupleLists.add(new TupleList(4, new int[]{1, 2, 3}, Collections.singletonList(new int[]{1, 1, 0})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .positiveTestingStrength(2)
                .negativeTestingStrength(0)
                .parameterSizes(2, 2, 2, 2)
                .errorTupleLists(errorTupleLists)
                .exclusionTupleLists(exclusionTupleLists)
                .build();

        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new MinimalForbiddenTuplesCheckerFactory(), 2)
        );
        final List<TestInputGroup> testInputGroups = generator
                .generate(model, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(2, testInputGroups.size());

        final List<int[]> firstTestInput = testInputGroups.get(0).getTestInputs();
        assertFalse(firstTestInput.isEmpty());
        assertEquals(1, firstTestInput.size());
        assertTrue(contains(firstTestInput.get(0), new int[]{0, 0, -1, -1}));
        assertFalse(contains(firstTestInput.get(0), new int[]{0, 0, 0, -1}));

        final List<int[]> secondTestInput = testInputGroups.get(1).getTestInputs();
        assertFalse(secondTestInput.isEmpty());
        assertEquals(1, secondTestInput.size());
        assertTrue(contains(secondTestInput.get(0), new int[]{-1, 1, 1, -1}));
        assertFalse(contains(secondTestInput.get(0), new int[]{-1, 1, 1, 0}));
    }
}

