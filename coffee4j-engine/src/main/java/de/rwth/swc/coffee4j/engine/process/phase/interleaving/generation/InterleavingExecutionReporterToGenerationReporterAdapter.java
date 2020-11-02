package de.rwth.swc.coffee4j.engine.process.phase.interleaving.generation;

import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.algorithmic.report.Report;
import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import de.rwth.swc.coffee4j.engine.process.phase.sequential.generation.SequentialExecutionReporterToGenerationReporterAdapter;
import de.rwth.swc.coffee4j.algorithmic.interleaving.InterleavingCombinatorialTestGroup;
import de.rwth.swc.coffee4j.algorithmic.interleaving.report.InterleavingGenerationReporter;
import de.rwth.swc.coffee4j.engine.report.InterleavingExecutionReporter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Interleaving version of {@link SequentialExecutionReporterToGenerationReporterAdapter}
 */
public class InterleavingExecutionReporterToGenerationReporterAdapter implements InterleavingGenerationReporter {
    
    private final InterleavingExecutionReporter reporter;
    private final ArgumentConverter argumentConverter;
    private final ModelConverter modelConverter;

    /**
     * @param reporter execution reporter to use.
     * @param argumentConverter argument converter used to convert between internal and external representation.
     * @param converter converter used to convert combinations from internal into the external format and vice versa.
     */
    public InterleavingExecutionReporterToGenerationReporterAdapter(InterleavingExecutionReporter reporter,
                                                             ArgumentConverter argumentConverter,
                                                             ModelConverter converter) {
        this.reporter = Preconditions.notNull(reporter);
        this.argumentConverter = Preconditions.notNull(argumentConverter);
        this.modelConverter = Preconditions.notNull(converter);
    }

    private Combination convertCombination(int[] combination) {
        return modelConverter.convertCombination(combination);
    }

    private Set<Combination> convertCombinations(Collection<int[]> combinations) {
        return combinations.stream()
                .map(modelConverter::convertCombination)
                .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
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

    @Override
    public void interleavingGroupGenerated(InterleavingCombinatorialTestGroup group) {
        reporter.interleavingGroupGenerated(group);
    }

    @Override
    public void interleavingGroupFinished(InterleavingCombinatorialTestGroup group, Map<int[], Class<? extends Throwable>> exceptionInducingCombinations, Set<int[]> possibleFailureInducingCombinations) {
        reporter.interleavingGroupFinished(group,
                exceptionInducingCombinations
                        .entrySet()
                        .stream()
                        .map(entry -> Map.entry(convertCombination(entry.getKey()), entry.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                possibleFailureInducingCombinations
                        .stream()
                        .map(modelConverter::convertCombination)
                        .collect(Collectors.toSet()));
    }

    @Override
    public void identificationStarted(InterleavingCombinatorialTestGroup group, int[] failingTestInput) {
        reporter.identificationStarted(group, convertCombination(failingTestInput));
    }

    @Override
    public void identificationFinished(InterleavingCombinatorialTestGroup group, Set<int[]> exceptionInducingCombinations, Set<int[]> failureInducingCombinations) {
        reporter.identificationFinished(group, convertCombinations(exceptionInducingCombinations), convertCombinations(failureInducingCombinations));
    }

    @Override
    public void identificationTestInputGenerated(InterleavingCombinatorialTestGroup group, int[] testInput) {
        reporter.identificationTestInputGenerated(group, convertCombination(testInput));
    }

    @Override
    public void checkingStarted(InterleavingCombinatorialTestGroup group, int[] failureInducingCombination) {
        reporter.checkingStarted(group, convertCombination(failureInducingCombination));
    }

    @Override
    public void checkingFinished(InterleavingCombinatorialTestGroup group, int[] failureInducingCombination, boolean isFailureInducing) {
        reporter.checkingFinished(group, convertCombination(failureInducingCombination), isFailureInducing);
    }
    
}
