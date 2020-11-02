package de.rwth.swc.coffee4j.algorithmic.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Class which can be used to simulate a reporter in testing. Just prints
 * all reported information to the standard output with the log level.
 */
public class LoggingReporter implements Reporter {
    
    private static final Logger LOG = LoggerFactory.getLogger(LoggingReporter.class);
    
    @Override
    public void report(ReportLevel level, Report report) {
        LOG.info("Report for level {}: {}", level, report);
    }
    
    @Override
    public void report(ReportLevel level, Supplier<Report> reportSupplier) {
        LOG.info("Report for level {}: {}", level, reportSupplier.get());
    }
}
