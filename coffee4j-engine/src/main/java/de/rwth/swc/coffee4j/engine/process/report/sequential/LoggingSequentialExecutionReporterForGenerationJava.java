package de.rwth.swc.coffee4j.engine.process.report.sequential;

import de.rwth.swc.coffee4j.engine.process.report.util.JavaFormatter;

/**
 * Reporter transforming the found exception-inducing combinations into java-code that can be directly integrated into
 * an IPM using a logger
 */
public class LoggingSequentialExecutionReporterForGenerationJava extends LoggingSequentialExecutionReporterForGeneration {
    /**
     * Creates a new {@link LoggingSequentialExecutionReporter} using a logger corresponding to this class
     */
    public LoggingSequentialExecutionReporterForGenerationJava() {
        super();
        formatter = new JavaFormatter();
    }
}
