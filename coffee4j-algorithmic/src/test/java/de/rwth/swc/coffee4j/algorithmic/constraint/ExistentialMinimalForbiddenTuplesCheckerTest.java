package de.rwth.swc.coffee4j.algorithmic.constraint;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.report.LoggingReporter;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.IpogNegConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm.IpogNeg;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExistentialMinimalForbiddenTuplesCheckerTest {

    @Test
    void testOnlyOneInvalidSchemataPerErrorConstraint() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, List.of(new int[]{0, 0}, new int[]{1, 1})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .negativeTestingStrength(1)
                .parameterSizes(3, 3, 3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new ExistentialMinimalForbiddenTuplesCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(model, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(1, testInputGroups.size());
        assertEquals(errorTupleLists.get(0), testInputGroups.get(0).getIdentifier());

        final List<int[]> testInputs = testInputGroups.get(0).getTestInputs();
        assertTrue(testInputs
                .stream()
                .allMatch(testInput -> CombinationUtil.contains(testInput, new int[]{0, 0, -1, -1, -1}))
            || testInputs
                .stream()
                .allMatch(testInput -> CombinationUtil.contains(testInput, new int[]{1, 1, -1, -1, -1}))
        );
    }

    @Test
    void testZeroInteractionsPerErrorConstraint() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, List.of(new int[]{0, 0}, new int[]{1, 1})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .negativeTestingStrength(0)
                .parameterSizes(3, 3, 3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new ExistentialMinimalForbiddenTuplesCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(model, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(1, testInputGroups.size());
        assertEquals(errorTupleLists.get(0), testInputGroups.get(0).getIdentifier());

        final List<int[]> testInputs = testInputGroups.get(0).getTestInputs();
        assertEquals(1, testInputs.size());
        assertTrue(testInputs
                .stream()
                .allMatch(testInput -> CombinationUtil.contains(testInput, new int[]{0, 0, -1, -1, -1}))
                || testInputs
                .stream()
                .allMatch(testInput -> CombinationUtil.contains(testInput, new int[]{1, 1, -1, -1, -1}))
        );
    }

    @Test
    void testEachChoiceInteractionsPerErrorConstraint() {
        final List<TupleList> errorTupleLists = new ArrayList<>();
        errorTupleLists.add(new TupleList(1, new int[]{0, 1}, List.of(new int[]{0, 0}, new int[]{1, 1})));

        final CompleteTestModel model = CompleteTestModel.builder()
                .negativeTestingStrength(1)
                .parameterSizes(3, 3, 3, 3, 3)
                .errorTupleLists(errorTupleLists)
                .build();

        final TestInputGroupGenerator generator = new IpogNeg(
                new IpogNegConfiguration(new ExistentialMinimalForbiddenTuplesCheckerFactory(), 2)
        );

        final List<TestInputGroup> testInputGroups = generator
                .generate(model, new LoggingReporter())
                .stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
        assertEquals(1, testInputGroups.size());
        assertEquals(errorTupleLists.get(0), testInputGroups.get(0).getIdentifier());

        final List<int[]> testInputs = testInputGroups.get(0).getTestInputs();
        assertEquals(3, testInputs.size());
        assertTrue(testInputs
                .stream()
                .allMatch(testInput -> CombinationUtil.contains(testInput, new int[]{0, 0, -1, -1, -1}))
                || testInputs
                .stream()
                .allMatch(testInput -> CombinationUtil.contains(testInput, new int[]{1, 1, -1, -1, -1}))
        );
    }
}

