package de.rwth.swc.coffee4j.engine.process.report.util;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.report.ExecutionReporter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class used by {@link ExecutionReporter}s to create output strings.
 */
public class ReportUtility {
    private static final String NO_EXCEPTION_INDUCING_COMBINATIONS = "No exception-inducing combinations were found!";
    private static final String NO_FAILURE_INDUCING_COMBINATIONS = "No failure-inducing combinations were found!";
    private static final String EXCEPTION_INDUCING_COMBINATIONS_FOUND = "Following exception-inducing combinations were found:\n";
    private static final String FAILURE_INDUCING_COMBINATIONS_FOUND = "Following failure-inducing combinations were found:\n";
    private static final String WARNING_MESSAGE = "Those combinations caused errors during the identification process and must not" +
            " be modeled as error-constraints!";

    private static Map<Class<? extends Throwable>, List<Combination>> groupCombinations(Map<Combination, Class<? extends Throwable>> exceptionInducingCombinations) {
        return exceptionInducingCombinations
                .entrySet()
                .stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
    }

    /**
     * creates output that can be used by {@link ExecutionReporter}s for a set of exception-inducing combinations
     * grouped by the exceptions they trigger.
     *
     * @param exceptionInducingCombinations set of found exception-inducing combinations together with their associated
     *                                      exceptions to output.
     * @param formatter The formatter to use to process the found combinations.
     * @return returns a String that can be directly printed by the {@link ExecutionReporter}s.
     */
    public static String getFormattedExceptionInducingCombinations(Map<Combination, Class<? extends Throwable>> exceptionInducingCombinations,
                                                                   CombinationFormatter formatter) {
        if (exceptionInducingCombinations == null || exceptionInducingCombinations.isEmpty()) {
            return NO_EXCEPTION_INDUCING_COMBINATIONS;
        } else {
            StringBuilder builder = new StringBuilder().append("Following error-constraint(s) was/were found:\n");

            for (Map.Entry<Class<? extends Throwable>, List<Combination>> entry : groupCombinations(exceptionInducingCombinations).entrySet()) {
                if (entry.getKey().equals(ErrorConstraintException.class)) {
                    builder.append("Not classified / No classification possible:\n");
                } else {
                    builder.append("Type ").append(entry.getKey().getSimpleName()).append(":\n");
                }

                entry.getValue().forEach(combination -> builder.append(formatter.format(combination)).append("\n"));
                builder.append("\n");
            }

            return builder.toString();
        }
    }

    /**
     * creates output that can be used by {@link ExecutionReporter}s for a set of exception-inducing combinations.
     *
     * @param exceptionInducingCombinations set of found exception-inducing combinations to output.
     * @return returns a String that can be directly printed by the {@link ExecutionReporter}s.
     */
    public static String getFormattedExceptionInducingCombinations(Set<Combination> exceptionInducingCombinations) {
        if (exceptionInducingCombinations == null || exceptionInducingCombinations.isEmpty()) {
            return NO_EXCEPTION_INDUCING_COMBINATIONS;
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(EXCEPTION_INDUCING_COMBINATIONS_FOUND);
            exceptionInducingCombinations.forEach(fic -> builder.append(fic).append("\n"));
            return builder.toString();
        }
    }

    /**
     * creates output that can be used by {@link ExecutionReporter}s for a set of failure-inducing combinations.
     *
     * @param possiblyFailureInducingCombinations set of found failure-inducing combinations to output.
     * @return returns a String that can be directly printed by the {@link ExecutionReporter}s.
     */
    public static String getFormattedFailureInducingCombinations(
            Collection<Combination> possiblyFailureInducingCombinations) {
        if (possiblyFailureInducingCombinations == null || possiblyFailureInducingCombinations.isEmpty()) {
            return NO_FAILURE_INDUCING_COMBINATIONS;
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(FAILURE_INDUCING_COMBINATIONS_FOUND);
            possiblyFailureInducingCombinations.forEach(fic -> builder.append(fic.toString()).append("\n"));
            return builder.toString();
        }
    }

    /**
     * @return returns a warning-message that indicates that the found failure-inducing combinations must not be modeled
     * as error-constraints in the error-constraint generation process.
     */
    public static String getWarningForErrorConstraintGeneration() {
        return WARNING_MESSAGE;
    }
    
    private ReportUtility() {
        // empty private constructor for utility class
    }
    
}
