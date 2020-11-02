package de.rwth.swc.coffee4j.algorithmic.sequential.manager;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.ConstraintGeneratingSequentialCombinatorialTestManager;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.GeneratingSequentialCombinatorialTestManager;
import de.rwth.swc.coffee4j.algorithmic.sequential.manager.SequentialCombinatorialTestConfiguration;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.classification.NoOpClassificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.GeneratingFaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.report.GenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConstraintGeneratingSequentialCombinatorialTestManagerTest {
    
    private GenerationReporter generationReporter;
    
    @BeforeEach
    void instantiateMocks() {
        generationReporter = Mockito.mock(GenerationReporter.class);
    }
    
    @Test
    void preconditions() {
        assertThrows(NullPointerException.class, () -> new ConstraintGeneratingSequentialCombinatorialTestManager(null, simpleModel()));
        assertThrows(NullPointerException.class, () -> new ConstraintGeneratingSequentialCombinatorialTestManager(simpleConfiguration(), null));
    }
    
    private CompleteTestModel simpleModel() {
        return CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(2)
                .build();
    }
    
    private SequentialCombinatorialTestConfiguration simpleConfiguration() {
        return new SequentialCombinatorialTestConfiguration(null, null, Collections.emptyList(), null,
                generationReporter, null);
    }
    
    @Test
    void returnsListOfTestInputsFromOneGeneratorInInitialGeneration() {
        final GeneratingFaultCharacterizationAlgorithm algorithm = Mockito.mock(GeneratingFaultCharacterizationAlgorithm.class);
        final FaultCharacterizationAlgorithmFactory factory = Mockito.mock(FaultCharacterizationAlgorithmFactory.class);
        when(factory.create(any())).thenReturn(algorithm);

        final List<int[]> testInputs = Arrays.asList(new int[]{0}, new int[]{1});
        final TestInputGroup group = new TestInputGroup("test", testInputs);
        final Supplier<TestInputGroup> groupSupplier = () -> group;
        final Set<Supplier<TestInputGroup>> allGroups = Collections.singleton(groupSupplier);
        final TestInputGroupGenerator generator = Mockito.mock(TestInputGroupGenerator.class);
        when(generator.generate(any(), any())).thenReturn(allGroups);
        
        final SequentialCombinatorialTestConfiguration configuration = new SequentialCombinatorialTestConfiguration(factory, NoOpClassificationStrategy::new, Collections.singleton(generator),
                null, generationReporter, null);
        final CompleteTestModel model = simpleModel();
        final ConstraintGeneratingSequentialCombinatorialTestManager testInputGenerator = new ConstraintGeneratingSequentialCombinatorialTestManager(configuration, model);
        
        final List<int[]> generatedTestInputs = testInputGenerator.generateInitialTests();
        
        Assertions.assertEquals(IntArrayWrapper.wrapToSet(testInputs), IntArrayWrapper.wrapToSet(generatedTestInputs));
        assertEquals(Collections.emptyList(), testInputGenerator.generateAdditionalTestInputsWithResult(testInputs.get(0), TestResult.failure(new IllegalArgumentException())));
        assertEquals(Collections.emptyList(), testInputGenerator.generateAdditionalTestInputsWithResult(testInputs.get(1), TestResult.failure(new IllegalArgumentException())));
        assertEquals(Optional.empty(), testInputGenerator.initializeClassification(new HashMap<>()));
        
        verify(generator, times(1)).generate(eq(model), any());
        verify(generationReporter, times(1)).testInputGroupFinished(group);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    void faultCharacterizationUsedWhenAlgorithmAvailable() {
        final List<int[]> testInputs = Arrays.asList(new int[]{0}, new int[]{1});
        final List<int[]> characterizationTestInputs = List.of(new int[]{2});
        final FaultCharacterizationConfiguration characterizationConfiguration = new FaultCharacterizationConfiguration(Mockito.mock(
                CompleteTestModel.class), Mockito.mock(Reporter.class));
        final TestInputGroup group = new TestInputGroup("test", testInputs, characterizationConfiguration);
        final Supplier<TestInputGroup> groupSupplier = () -> group;
        final Set<Supplier<TestInputGroup>> allGroups = Collections.singleton(groupSupplier);
        final TestInputGroupGenerator generator = Mockito.mock(TestInputGroupGenerator.class);
        when(generator.generate(any(), any())).thenReturn(allGroups);
        
        final GeneratingFaultCharacterizationAlgorithm algorithm = Mockito.mock(GeneratingFaultCharacterizationAlgorithm.class);
        final FaultCharacterizationAlgorithmFactory factory = Mockito.mock(FaultCharacterizationAlgorithmFactory.class);
        when(factory.create(any())).thenReturn(algorithm);
        when(algorithm.computeNextTestInputs(any())).thenReturn(characterizationTestInputs);
        
        final SequentialCombinatorialTestConfiguration configuration = new SequentialCombinatorialTestConfiguration(factory, NoOpClassificationStrategy::new, Collections.singleton(generator),
                null, generationReporter, null);
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(3)
                .build();
        final ConstraintGeneratingSequentialCombinatorialTestManager testInputGenerator = new ConstraintGeneratingSequentialCombinatorialTestManager(configuration, testModel);
        
        final List<int[]> generatedTestInputs = testInputGenerator.generateInitialTests();
        final Exception exception = new ErrorConstraintException();
        Assertions.assertEquals(IntArrayWrapper.wrapToSet(testInputs), IntArrayWrapper.wrapToSet(generatedTestInputs));
        assertEquals(Collections.emptyList(), testInputGenerator.generateAdditionalTestInputsWithResult(testInputs.get(0), TestResult.failure(exception)));
        
        final List<int[]> generatedCharacterizationTestInputs = testInputGenerator.generateAdditionalTestInputsWithResult(testInputs.get(1), TestResult.success());
        
        Assertions.assertEquals(IntArrayWrapper.wrapToSet(characterizationTestInputs), IntArrayWrapper.wrapToSet(generatedCharacterizationTestInputs));
        
        final ArgumentCaptor<Map<int[], TestResult>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(algorithm, times(1)).computeNextTestInputs(mapCaptor.capture());
        verify(factory, times(1)).create(characterizationConfiguration);
        final Map<int[], TestResult> map = mapCaptor.getValue();
        
        final List<int[]> keys = new ArrayList<>(map.keySet());
        assertEquals(2, keys.size());
        assertTrue(map.containsValue(TestResult.success()));
        assertTrue(map.containsValue(TestResult.failure(exception)));
        Assertions.assertTrue(IntArrayWrapper.wrapToSet(keys).contains(IntArrayWrapper.wrap(testInputs.get(0))));
        Assertions.assertTrue(IntArrayWrapper.wrapToSet(keys).contains(IntArrayWrapper.wrap(testInputs.get(1))));
    }

    @Test
    void testMissingClassificationStrategy() {
        final GeneratingFaultCharacterizationAlgorithm algorithm = Mockito.mock(GeneratingFaultCharacterizationAlgorithm.class);
        final FaultCharacterizationAlgorithmFactory factory = Mockito.mock(FaultCharacterizationAlgorithmFactory.class);
        when(factory.create(any())).thenReturn(algorithm);

        final TestInputGroup group = new TestInputGroup("test", Collections.emptyList(), null);
        final Supplier<TestInputGroup> groupSupplier = () -> group;
        final Set<Supplier<TestInputGroup>> allGroups = Collections.singleton(groupSupplier);
        final TestInputGroupGenerator generator = Mockito.mock(TestInputGroupGenerator.class);
        when(generator.generate(any(), any())).thenReturn(allGroups);

        final SequentialCombinatorialTestConfiguration configuration = new SequentialCombinatorialTestConfiguration(factory, null, Collections.singleton(generator),
                null, generationReporter, null);
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(3)
                .build();

        assertThrows(Coffee4JException.class, () -> new ConstraintGeneratingSequentialCombinatorialTestManager(configuration, testModel));
    }

    @Test
    void testInvalidCharacterizationStrategy() {
        final FaultCharacterizationAlgorithm algorithm = Mockito.mock(FaultCharacterizationAlgorithm.class);
        final FaultCharacterizationAlgorithmFactory factory = Mockito.mock(FaultCharacterizationAlgorithmFactory.class);
        when(factory.create(any())).thenReturn(algorithm);

        final TestInputGroup group = new TestInputGroup("test", Collections.emptyList(), null);
        final Supplier<TestInputGroup> groupSupplier = () -> group;
        final Set<Supplier<TestInputGroup>> allGroups = Collections.singleton(groupSupplier);
        final TestInputGroupGenerator generator = Mockito.mock(TestInputGroupGenerator.class);
        when(generator.generate(any(), any())).thenReturn(allGroups);

        final SequentialCombinatorialTestConfiguration configuration = new SequentialCombinatorialTestConfiguration(factory, null, Collections.singleton(generator),
                null, generationReporter, null);
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(3)
                .build();

        assertThrows(Coffee4JException.class, () -> new ConstraintGeneratingSequentialCombinatorialTestManager(configuration, testModel));
    }

    @Test
    void testClassification() {
        final GeneratingFaultCharacterizationAlgorithm algorithm = Mockito.mock(GeneratingFaultCharacterizationAlgorithm.class);
        when(algorithm.computeExceptionInducingCombinations()).thenReturn(new HashSet<>());
        when(algorithm.computeFailureInducingCombinations()).thenReturn(new ArrayList<>());
        final FaultCharacterizationAlgorithmFactory factory = Mockito.mock(FaultCharacterizationAlgorithmFactory.class);
        when(factory.create(any())).thenReturn(algorithm);
        final ClassificationStrategy classificationStrategy = Mockito.mock(ClassificationStrategy.class);
        when(classificationStrategy.startClassification(new HashMap<>(), new ArrayList<>(), new HashSet<>()))
                .thenReturn(Optional.empty());
        when(classificationStrategy.getClassifiedExceptionInducingCombinations())
                .thenReturn(new HashMap<>());
        when(classificationStrategy.generateNextTestInputForClassification(null, null))
                .thenReturn(Optional.empty());
        final ClassificationStrategyFactory classificationStrategyFactory = Mockito.mock(ClassificationStrategyFactory.class);
        when(classificationStrategyFactory.create(any())).thenReturn(classificationStrategy);

        final TestInputGroup group = new TestInputGroup("test", Collections.emptyList(), null);
        final Supplier<TestInputGroup> groupSupplier = () -> group;
        final Set<Supplier<TestInputGroup>> allGroups = Collections.singleton(groupSupplier);
        final TestInputGroupGenerator generator = Mockito.mock(TestInputGroupGenerator.class);
        when(generator.generate(any(), any())).thenReturn(allGroups);

        final SequentialCombinatorialTestConfiguration configuration = new SequentialCombinatorialTestConfiguration(factory, classificationStrategyFactory, Collections.singleton(generator),
                null, generationReporter, null);
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(3)
                .build();

        GeneratingSequentialCombinatorialTestManager manager = new ConstraintGeneratingSequentialCombinatorialTestManager(configuration, testModel);

        Optional<int[]> optNextTestInput = manager.initializeClassification(new HashMap<>());
        assertFalse(optNextTestInput.isPresent());

        optNextTestInput = manager.generateNextTestInputForClassification(null, null);
        assertFalse(optNextTestInput.isPresent());
    }
}
