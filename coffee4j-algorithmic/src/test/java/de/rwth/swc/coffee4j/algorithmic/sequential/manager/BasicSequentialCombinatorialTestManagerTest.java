package de.rwth.swc.coffee4j.algorithmic.sequential.manager;

import de.rwth.swc.coffee4j.algorithmic.configuration.execution.ExecutionMode;
import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.prioritization.TestInputPrioritizer;
import de.rwth.swc.coffee4j.algorithmic.sequential.report.GenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;
import de.rwth.swc.coffee4j.algorithmic.util.IntArrayWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BasicSequentialCombinatorialTestManagerTest {
    
    private GenerationReporter generationReporter;
    
    @BeforeEach
    void instantiateMocks() {
        generationReporter = Mockito.mock(GenerationReporter.class);
    }
    
    @Test
    void preconditions() {
        assertThrows(NullPointerException.class, () -> new BasicSequentialCombinatorialTestManager(null, simpleModel()));
        assertThrows(NullPointerException.class, () -> new BasicSequentialCombinatorialTestManager(simpleConfiguration(), null));
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
    void returnsPrioritizedListOfTestInputsFromOneGeneratorInInitialGeneration() {
        final List<int[]> testInputs = Arrays.asList(new int[]{0}, new int[]{1});
        final FaultCharacterizationConfiguration characterizationConfiguration = new FaultCharacterizationConfiguration(
                Mockito.mock(TestModel.class), Mockito.mock(Reporter.class));
        final TestInputGroup group = new TestInputGroup("test", testInputs, characterizationConfiguration);
        final Supplier<TestInputGroup> groupSupplier = () -> group;
        final Set<Supplier<TestInputGroup>> allGroups = Collections.singleton(groupSupplier);
        final TestInputGroupGenerator generator = Mockito.mock(TestInputGroupGenerator.class);
        when(generator.generate(any(), any())).thenReturn(allGroups);
        
        final SequentialCombinatorialTestConfiguration configuration = new SequentialCombinatorialTestConfiguration(
                null, null, Collections.singleton(generator),
                new ReversingTestInputPrioritzer(), generationReporter, null);
        final CompleteTestModel model = simpleModel();
        final BasicSequentialCombinatorialTestManager testInputGenerator = new BasicSequentialCombinatorialTestManager(configuration, model);
        
        final List<int[]> generatedTestInputs = testInputGenerator.generateInitialTests();
        
        assertEquals(2, generatedTestInputs.size());
        assertArrayEquals(testInputs.get(1), generatedTestInputs.get(0));
        assertArrayEquals(testInputs.get(0), generatedTestInputs.get(1));
        assertEquals(Collections.emptyList(), testInputGenerator.generateAdditionalTestInputsWithResult(testInputs.get(0), TestResult.failure(new IllegalArgumentException())));
        assertEquals(Collections.emptyList(), testInputGenerator.generateAdditionalTestInputsWithResult(testInputs.get(1), TestResult.failure(new IllegalArgumentException())));
        
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
        
        final FaultCharacterizationAlgorithm algorithm = Mockito.mock(FaultCharacterizationAlgorithm.class);
        final FaultCharacterizationAlgorithmFactory factory = Mockito.mock(FaultCharacterizationAlgorithmFactory.class);
        when(factory.create(any())).thenReturn(algorithm);
        when(algorithm.computeNextTestInputs(any())).thenReturn(characterizationTestInputs);
        
        final SequentialCombinatorialTestConfiguration configuration = new SequentialCombinatorialTestConfiguration(factory, null, Collections.singleton(generator),
                null, generationReporter, null);
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(3)
                .build();
        final BasicSequentialCombinatorialTestManager testInputGenerator = new BasicSequentialCombinatorialTestManager(configuration, testModel);
        
        final List<int[]> generatedTestInputs = testInputGenerator.generateInitialTests();
        final Exception exception = new IllegalArgumentException();
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
    @SuppressWarnings("unchecked")
    void directlyRunsFaultCharacterizationAfterFailedInputInFailFastMode() {
        final List<int[]> testInputs = Arrays.asList(new int[]{0}, new int[]{1});
        final List<int[]> firstCharacterizationTestInputs = List.of(new int[]{2}, new int[] {3});
        final List<int[]> secondCharacterizationTestInputs = List.of(new int[]{4}, new int[] {5});
        final FaultCharacterizationConfiguration characterizationConfiguration = new FaultCharacterizationConfiguration(Mockito.mock(
                CompleteTestModel.class), Mockito.mock(Reporter.class));
        final TestInputGroup group = new TestInputGroup("test", testInputs, characterizationConfiguration);
        final Supplier<TestInputGroup> groupSupplier = () -> group;
        final Set<Supplier<TestInputGroup>> allGroups = Collections.singleton(groupSupplier);
        final TestInputGroupGenerator generator = Mockito.mock(TestInputGroupGenerator.class);
        when(generator.generate(any(), any())).thenReturn(allGroups);
    
        final FaultCharacterizationAlgorithm algorithm = Mockito.mock(FaultCharacterizationAlgorithm.class);
        final FaultCharacterizationAlgorithmFactory factory = Mockito.mock(FaultCharacterizationAlgorithmFactory.class);
        when(factory.create(any())).thenReturn(algorithm);
        when(algorithm.computeNextTestInputs(any()))
                .thenReturn(firstCharacterizationTestInputs, secondCharacterizationTestInputs, List.of());
    
        final SequentialCombinatorialTestConfiguration configuration = new SequentialCombinatorialTestConfiguration(
                factory, null, Collections.singleton(generator),
                null, generationReporter, ExecutionMode.FAIL_FAST);
        final CompleteTestModel testModel = CompleteTestModel.builder()
                .positiveTestingStrength(1)
                .parameterSizes(6)
                .build();
        final BasicSequentialCombinatorialTestManager testInputGenerator = new BasicSequentialCombinatorialTestManager(configuration, testModel);
    
        final List<int[]> generatedTestInputs = testInputGenerator.generateInitialTests();
        final Exception exception = new IllegalArgumentException();
        
        final List<int[]> firstAdditionalInputs = testInputGenerator.generateAdditionalTestInputsWithResult(
                generatedTestInputs.get(0), TestResult.failure(exception));
        Assertions.assertEquals(IntArrayWrapper.wrapToSet(firstCharacterizationTestInputs),
                IntArrayWrapper.wrapToSet(firstAdditionalInputs));
        
        final List<int[]> secondAdditionalInputs = testInputGenerator.generateAdditionalTestInputsWithResult(
                firstAdditionalInputs.get(0), TestResult.failure(exception));
        assertEquals(List.of(), secondAdditionalInputs);
    
        final List<int[]> thirdAdditionalInputs = testInputGenerator.generateAdditionalTestInputsWithResult(
                firstAdditionalInputs.get(1), TestResult.failure(exception));
        Assertions.assertEquals(IntArrayWrapper.wrapToSet(secondCharacterizationTestInputs),
                IntArrayWrapper.wrapToSet(thirdAdditionalInputs));
        
        final List<int[]> fourthAdditionalInputs = testInputGenerator.generateAdditionalTestInputsWithResult(
                thirdAdditionalInputs.get(0), TestResult.failure(exception));
        assertEquals(List.of(), fourthAdditionalInputs);
        final List<int[]> fifthAdditionalInputs = testInputGenerator.generateAdditionalTestInputsWithResult(
                thirdAdditionalInputs.get(1), TestResult.failure(exception));
        assertEquals(List.of(), fifthAdditionalInputs);
    }
    
    private static final class ReversingTestInputPrioritzer implements TestInputPrioritizer {
    
        @Override
        public List<int[]> prioritize(Collection<int[]> testCases, TestModel model) {
            final List<int[]> copy = new ArrayList<>(testCases);
            Collections.reverse(copy);
            
            return copy;
        }
    
    }
    
}
