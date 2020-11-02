package de.rwth.swc.coffee4j.algorithmic.classification;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesChecker;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClassificationStrategyTest {
    
    final CompleteTestModel model = CompleteTestModel.builder()
            .positiveTestingStrength(2)
            .parameterSizes(2, 2)
            .build();
    final ConstraintChecker checker = new MinimalForbiddenTuplesChecker(model);

    ClassificationConfiguration configuration;
    final Map<int[], Throwable> errorConstraintExceptionCausingTestInputs = new HashMap<>();
    final List<int[]> exceptionInducingCombinationsToClassify = new ArrayList<>();
    final Set<int[]> possiblyFailureInducingCombinations = new HashSet<>();
    final Map<IntList, Class<? extends Throwable>> classifiedExceptionInducingCombinations = new HashMap<>();

    @BeforeEach
    void initialize() {
        configuration = ClassificationConfiguration.configuration()
                .testModel(model)
                .constraintChecker(checker)
                .build();

        errorConstraintExceptionCausingTestInputs.put(new int[]{0,0}, new ErrorConstraintException());
        errorConstraintExceptionCausingTestInputs.put(new int[]{1,1}, new ErrorConstraintException());

        exceptionInducingCombinationsToClassify.add(new int[]{1,-1});
        exceptionInducingCombinationsToClassify.add(new int[]{-1,0});

        classifiedExceptionInducingCombinations.put(new IntArrayList(new int[]{1,-1}), ErrorConstraintException.class);
        classifiedExceptionInducingCombinations.put(new IntArrayList(new int[]{-1,0}), ErrorConstraintException.class);
    }

    @Test
    void testValidConfiguration() {
        assertEquals(model, configuration.getTestModel());
        assertEquals(checker, configuration.getConstraintChecker());
    }

    @Test
    void testInvalidConfiguration() {
        assertThrows(NullPointerException.class, () -> ClassificationConfiguration
                .configuration()
                .testModel(null)
                .constraintChecker(checker)
                .build());

        assertThrows(NullPointerException.class, () -> ClassificationConfiguration
                .configuration()
                .testModel(model)
                .constraintChecker(null)
                .build());
    }

    @Test
    void testNoOpClassification() {
        ClassificationStrategy strategy = NoOpClassificationStrategy.noOpClassificationStrategy()
                .create(configuration);

        runTest(strategy);
    }

    @Test
    void testMaxCountClassificationStrategy() {
        ClassificationStrategy strategy = MaxCountClassificationStrategy.maxCountClassificationStrategy()
                .create(configuration);

        runTest(strategy);
    }

    private void runTest(ClassificationStrategy strategy) {
        assertEquals(Optional.empty(), strategy.startClassification(
                errorConstraintExceptionCausingTestInputs,
                exceptionInducingCombinationsToClassify,
                possiblyFailureInducingCombinations));

        assertEquals(Optional.empty(), strategy.generateNextTestInputForClassification(null, null));

        assertEquals(classifiedExceptionInducingCombinations, strategy.getClassifiedExceptionInducingCombinations()
                .entrySet()
                .stream().collect(Collectors.toMap(entry -> new IntArrayList(entry.getKey()), Map.Entry::getValue)));
    }

    @Test
    void testIsolatingClassificationStrategy() {
        ClassificationStrategy strategy = IsolatingClassificationStrategy.isolatingClassificationStrategy()
                .create(configuration);

        IntList nextTestInput = null;

        Optional<int[]> optInput = strategy.startClassification(
                errorConstraintExceptionCausingTestInputs,
                exceptionInducingCombinationsToClassify,
                possiblyFailureInducingCombinations);

        if (optInput.isPresent()) {
            nextTestInput = new IntArrayList(optInput.get());
        }

        assertEquals(new IntArrayList(new int[]{1,1}), nextTestInput);

        assert nextTestInput != null;
        optInput = strategy.generateNextTestInputForClassification(nextTestInput.toIntArray(), TestResult.failure(new ErrorConstraintException()));

        if (optInput.isPresent()) {
            nextTestInput = new IntArrayList(optInput.get());
        }

        assertEquals(new IntArrayList(new int[]{0,0}), nextTestInput);

        optInput = strategy.generateNextTestInputForClassification(nextTestInput.toIntArray(), TestResult.failure(new ErrorConstraintException()));

        assertEquals(Optional.empty(), optInput);

        assertEquals(classifiedExceptionInducingCombinations, strategy.getClassifiedExceptionInducingCombinations()
                .entrySet()
                .stream().collect(Collectors.toMap(entry -> new IntArrayList(entry.getKey()), Map.Entry::getValue)));
    }
}
