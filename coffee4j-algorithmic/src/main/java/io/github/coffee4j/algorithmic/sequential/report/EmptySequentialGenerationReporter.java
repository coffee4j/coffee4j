package io.github.coffee4j.algorithmic.sequential.report;

import io.github.coffee4j.algorithmic.report.Report;
import io.github.coffee4j.algorithmic.report.ReportLevel;

import java.util.function.Supplier;

public class EmptySequentialGenerationReporter implements GenerationReporter {
    @Override
    public void report(ReportLevel level, Report report) {
        // empty
    }

    @Override
    public void report(ReportLevel level, Supplier<Report> reportSupplier) {
        // empty
    }
}
