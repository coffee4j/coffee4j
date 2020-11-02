package de.rwth.swc.coffee4j.engine.process.report.interleaving;

import de.rwth.swc.coffee4j.engine.process.report.util.JavaFormatter;

/**
 * Reporter transforming the found exception-inducing combinations into java-code that can be directly integrated into
 * an IPM using a logger
 */
public class LoggingInterleavingExecutionReporterForGenerationJava extends LoggingInterleavingExecutionReporterForGeneration {
    public LoggingInterleavingExecutionReporterForGenerationJava() {
        super();
        formatter = new JavaFormatter();
    }
}
