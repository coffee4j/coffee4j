package de.rwth.swc.coffee4j.algorithmic.interleaving.manager;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.classification.IsolatingClassificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesChecker;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.feedback.DefaultFeedbackCheckingStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.feedback.FeedbackCheckingStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.aetg.AetgStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.trt.TupleRelationshipStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.report.EmptyInterleavingGenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.interleaving.report.InterleavingGenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultInterleavingManagerTest {
    private TestInputGenerationStrategyFactory testInputGenerationStrategyFactory;
    private IdentificationStrategyFactory identificationStrategyFactory;
    private FeedbackCheckingStrategyFactory feedbackCheckingStrategyFactory;
    private ClassificationStrategyFactory classificationStrategyFactory;
    private InterleavingGenerationReporter generationReporter;
    private ConstraintCheckerFactory constraintCheckerFactory;

    private int[] failingTestInput = null;
    private int[] fic = null;

    void instantiateMocks() {
        testInputGenerationStrategyFactory = Mockito.mock(TestInputGenerationStrategyFactory.class);
        identificationStrategyFactory = Mockito.mock(IdentificationStrategyFactory.class);
        feedbackCheckingStrategyFactory = Mockito.mock(FeedbackCheckingStrategyFactory.class);
        classificationStrategyFactory = Mockito.mock(ClassificationStrategyFactory.class);
        generationReporter = Mockito.mock(InterleavingGenerationReporter.class);
        constraintCheckerFactory = Mockito.mock(MinimalForbiddenTuplesCheckerFactory.class);
    }

    void instantiateConfiguration() {
        testInputGenerationStrategyFactory = AetgStrategy.aetgStrategy();
        identificationStrategyFactory = TupleRelationshipStrategy.tupleRelationshipStrategy();
        feedbackCheckingStrategyFactory = DefaultFeedbackCheckingStrategy.defaultCheckingStrategy();
        classificationStrategyFactory = IsolatingClassificationStrategy.isolatingClassificationStrategy();
        generationReporter = new EmptyInterleavingGenerationReporter();
        constraintCheckerFactory = MinimalForbiddenTuplesChecker.minimalForbiddenTuplesChecker();
    }

    @Test
    void preconditions() {
        instantiateMocks();
        assertThrows(NullPointerException.class, () -> new DefaultInterleavingManager(null, simpleModel()));
        assertThrows(NullPointerException.class, () -> new DefaultInterleavingManager(simpleConfiguration(), null));
        assertThrows(NullPointerException.class, () -> new DefaultGeneratingInterleavingManager(null, simpleModel()));
        assertThrows(NullPointerException.class, () -> new DefaultGeneratingInterleavingManager(simpleConfiguration(), null));
    }

    @Test
    void testCompleteRunOfDefaultInterleavingManager() {
        instantiateConfiguration();
        InterleavingCombinatorialTestManager manager = DefaultInterleavingManager
                .managerFactory().create(simpleConfiguration(), simpleModel());

        run(manager, new ErrorConstraintException());

        assertTrue(manager.combinationIdentified());
    }

    @Test
    void testCompleteRunOfDefaultGeneratingInterleavingManagerIdentifyingExceptionInducingCombination() {
        instantiateConfiguration();
        GeneratingInterleavingCombinatorialTestManager manager = DefaultGeneratingInterleavingManager
                .managerFactory().create(simpleConfiguration(), simpleModel());

        run(manager, new ErrorConstraintException());

        Map<int[], TestResult> errorConstraintExceptionCausingTestInputs = new HashMap<>();
        errorConstraintExceptionCausingTestInputs.put(failingTestInput, TestResult.failure(new ErrorConstraintException()));

        Optional<int[]> nextTestInput = manager.initializeClassification(errorConstraintExceptionCausingTestInputs);
        assertTrue(nextTestInput.isPresent());

        nextTestInput = manager.generateNextTestInput(nextTestInput.get(), TestResult.failure(new ErrorConstraintException()));
        assertFalse(nextTestInput.isPresent());

        List<int[]> classifiedCombinations = new ArrayList<>(manager.getMinimalExceptionInducingCombinations().keySet());
        assertEquals(1, classifiedCombinations.size());
        assertArrayEquals(fic, classifiedCombinations.get(0));
    }

    @Test
    void testCompleteRunOfDefaultGeneratingInterleavingManagerIdentifyingFailureInducingCombination() {
        instantiateConfiguration();
        GeneratingInterleavingCombinatorialTestManager manager = DefaultGeneratingInterleavingManager
                .managerFactory().create(simpleConfiguration(), simpleModel());

        run(manager, new AssertionError());

        Map<int[], TestResult> errorConstraintExceptionCausingTestInputs = new HashMap<>();
        errorConstraintExceptionCausingTestInputs.put(failingTestInput, TestResult.failure(new AssertionError()));

        Optional<int[]> nextTestInput = manager.initializeClassification(errorConstraintExceptionCausingTestInputs);
        assertFalse(nextTestInput.isPresent());

        List<int[]> classifiedCombinations = new ArrayList<>(manager.getMinimalExceptionInducingCombinations().keySet());
        assertEquals(0, classifiedCombinations.size());
    }

    private void run(InterleavingCombinatorialTestManager manager, Throwable exception) {
        Optional<int[]> nextTestInput = manager.generateNextTestInput(null, null);
        assertTrue(nextTestInput.isPresent());

        failingTestInput = nextTestInput.get();

        nextTestInput = manager.initializeIdentification(failingTestInput, TestResult.failure(exception));
        assertTrue(nextTestInput.isPresent());

        fic = CombinationUtil.emptyCombination(failingTestInput.length);

        for (int i = 0; i < failingTestInput.length; i++) {
            if (failingTestInput[i] == nextTestInput.get()[i]) {
                fic[i] = failingTestInput[i];
            }
        }

        // trt needs 50 checks to mitigate safe-value-assumption
        for (int i = 0; i < 50; i++) {
            nextTestInput = manager.generateNextTestInput(nextTestInput.get(), TestResult.failure(exception));
            assertTrue(nextTestInput.isPresent());
        }

        manager.updateCoverage(nextTestInput.get());
        nextTestInput = manager.generateNextTestInput(nextTestInput.get(), TestResult.success());
        assertFalse(nextTestInput.isPresent());

        nextTestInput = manager.initializeFeedbackChecking();
        assertTrue(nextTestInput.isPresent());

        do {
            nextTestInput = manager.generateNextTestInput(nextTestInput.get(), TestResult.failure(exception));
        } while (nextTestInput.isPresent());

        nextTestInput = manager.initializeFeedbackChecking();
        assertFalse(nextTestInput.isPresent());
    }

    private CompleteTestModel simpleModel() {
        return CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2, 2)
                .build();
    }

    private InterleavingCombinatorialTestConfiguration simpleConfiguration() {
        return new InterleavingCombinatorialTestConfiguration(
                testInputGenerationStrategyFactory,
                identificationStrategyFactory,
                feedbackCheckingStrategyFactory,
                classificationStrategyFactory,
                constraintCheckerFactory,
                generationReporter
        );
    }
}
