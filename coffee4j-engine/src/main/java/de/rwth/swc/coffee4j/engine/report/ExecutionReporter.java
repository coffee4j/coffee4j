package de.rwth.swc.coffee4j.engine.report;

import de.rwth.swc.coffee4j.algorithmic.model.TestResult;
import de.rwth.swc.coffee4j.algorithmic.report.Report;
import de.rwth.swc.coffee4j.algorithmic.report.ReportLevel;
import de.rwth.swc.coffee4j.engine.configuration.extension.model.ModelModifier;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;

public interface ExecutionReporter {
    /**
     * Indicates the start of a test input execution.
     *
     * @param testInput the started test input
     */
    default void testInputExecutionStarted(Combination testInput) {
    }

    /**
     * Indicates the end of a test input execution.
     *
     * @param testInput the finished test input
     * @param result    the result of the test input
     */
    default void testInputExecutionFinished(Combination testInput, TestResult result) {
    }

    /**
     * Specifies the level of reports this reporter wants to get. Only reports with an equal of higher
     * {@link ReportLevel} will be passed to {@link #report(ReportLevel, Report)}.
     *
     * @return the desired level of reports. The default method returns trace
     */
    default ReportLevel getReportLevel() {
        return ReportLevel.TRACE;
    }

    /**
     * Called if any algorithm made a report for and event not covered by any of the life cycle callback methods.
     *
     * @param level  the level of the report. Always higher than or equal to {@link #getReportLevel()}
     * @param report the actual report with resolved arguments
     */
    default void report(ReportLevel level, Report report) {
    }
    
    /**
     * Called if any {@link ModelModifier} changed the model.
     *
     * @param original the model before modification
     * @param modified the model after modification.
     *   May be {@code null} if the {@link ModelModifier} returned {@code null}
     */
    default void modelModified(InputParameterModel original, InputParameterModel modified) {
    }
    
}
