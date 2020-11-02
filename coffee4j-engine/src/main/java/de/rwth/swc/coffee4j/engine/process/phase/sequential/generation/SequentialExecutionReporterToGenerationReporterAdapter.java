package de.rwth.swc.coffee4j.engine.process.phase.sequential.generation;

import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.algorithmic.sequential.report.GenerationReporter;
import de.rwth.swc.coffee4j.algorithmic.report.Report;
import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.TestInputGroupContext;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.report.SequentialExecutionReporter;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This is an exact copy of the DelegatingModelBasedArgumentConverter from the Model Module
 */
public class SequentialExecutionReporterToGenerationReporterAdapter implements GenerationReporter {
    
    private final SequentialExecutionReporter reporter;
    
    private final ArgumentConverter argumentConverter;
    
    private final ModelConverter modelConverter;
    
    private final Map<Object, TestInputGroupContext> testInputGroupContexts = new HashMap<>();

    public SequentialExecutionReporterToGenerationReporterAdapter(SequentialExecutionReporter reporter, ArgumentConverter argumentConverter, ModelConverter modelConverter) {
        this.reporter = Preconditions.notNull(reporter);
        this.argumentConverter = Preconditions.notNull(argumentConverter);
        this.modelConverter = Preconditions.notNull(modelConverter);
    }
    
    @Override
    public void testInputGroupGenerated(TestInputGroup testInputGroup, TestInputGroupGenerator generator) {
        initializeContext(testInputGroup, generator);
        reporter.testInputGroupGenerated(convertTestInputGroup(testInputGroup), convertCombinations(testInputGroup.getTestInputs()));
    }
    
    private void initializeContext(TestInputGroup testInputGroup, TestInputGroupGenerator generator) {
        final Object identifier = testInputGroup.getIdentifier();
        final Object convertedIdentifier = argumentConverter.canConvert(identifier) ? argumentConverter.convert(identifier) : identifier;
        
        testInputGroupContexts.put(identifier, new TestInputGroupContext(convertedIdentifier, generator));
    }
    
    private TestInputGroupContext convertTestInputGroup(TestInputGroup testInputGroup) {
        return testInputGroupContexts.get(testInputGroup.getIdentifier());
    }
    
    private List<Combination> convertCombinations(Collection<int[]> combinations) {
        return combinations
                .stream()
                .map(modelConverter::convertCombination)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    private Combination convertCombination(int[] combination) {
        return modelConverter.convertCombination(combination);
    }
    
    @Override
    public void testInputGroupFinished(TestInputGroup testInputGroup) {
        reporter.testInputGroupFinished(convertTestInputGroup(testInputGroup));
    }
    
    @Override
    public void faultCharacterizationStarted(TestInputGroup testInputGroup, FaultCharacterizationAlgorithm algorithm) {
        reporter.faultCharacterizationStarted(convertTestInputGroup(testInputGroup), algorithm);
    }
    
    @Override
    public void faultCharacterizationFinished(TestInputGroup testInputGroup, Map<int[], Class<? extends Throwable>> exceptionInducingCombinations, Set<int[]> possiblyFailureInducingCombinations) {
        reporter.faultCharacterizationFinished(convertTestInputGroup(testInputGroup),
                exceptionInducingCombinations
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                entry -> convertCombination(entry.getKey()),
                                Map.Entry::getValue)),
                convertCombinations(possiblyFailureInducingCombinations));
    }
    
    @Override
    public void faultCharacterizationTestInputsGenerated(TestInputGroup testInputGroup, List<int[]> testInputs) {
        reporter.faultCharacterizationTestInputsGenerated(convertTestInputGroup(testInputGroup), new ArrayList<>(convertCombinations(new HashSet<>(testInputs))));
    }
    
    @Override
    public void report(ReportLevel level, Report report) {
        Preconditions.notNull(level);
        
        if (level.isWorseThanOrEqualTo(reporter.getReportLevel())) {
            report.convertArguments(argumentConverter);
            reporter.report(level, report);
        }
    }
    
    @Override
    public void report(ReportLevel level, Supplier<Report> reportSupplier) {
        Preconditions.notNull(level);
        Preconditions.notNull(reportSupplier);
        
        if (level.isWorseThanOrEqualTo(reporter.getReportLevel())) {
            final Report report = reportSupplier.get();
            report.convertArguments(argumentConverter);
            reporter.report(level, report);
        }
    }
    
}
