package de.rwth.swc.coffee4j.algorithmic.interleaving.report;

import de.rwth.swc.coffee4j.algorithmic.report.Report;
import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;

import java.util.function.Supplier;

/**
 * Empty {@link InterleavingGenerationReporter} reporting nothing.
 */
public class EmptyInterleavingGenerationReporter implements InterleavingGenerationReporter {
    @Override
    public void report(ReportLevel level, Report report) {
        // empty
    }

    @Override
    public void report(ReportLevel level, Supplier<Report> reportSupplier) {
        // empty
    }
}
