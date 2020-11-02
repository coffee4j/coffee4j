package de.rwth.swc.coffee4j.junit.engine.execution;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Formats a display name using a name pattern and a {@link Combination}.
 *
 * This class is more a less a copy of {@code org.junit.jupiter.params.ParameterizedTestNameFormatter} from the
 * junit-jupiter-params project.
 */
public class DisplayNameFormatter {

    private DisplayNameFormatter() {}

    private static final String COMBINATION_REPLACEMENT_PATTERN = "{combination}";
    /**
     * The default name pattern: {combination}
     */
    public static final String DEFAULT_NAME_PATTERN = COMBINATION_REPLACEMENT_PATTERN;

    /**
     * Formats the combination into a String using a defined name pattern.
     * <p>
     * Multiple placeholders are supported:
     * -{index}: given the current invocation index of the test starting with 1
     * -{combination}: the complete {@link Combination} which is tested by the test
     * -{PARAMETER_NAME}: the value of the {@link Parameter} with the given name in the
     * currently tested {@link Combination}
     * <p>

     *
     * @param namePattern the name pattern to format with
     * @param combination the combination to format with
     * @return the formatted string
     */
    public static String format(String namePattern, Combination combination) {
        return Optional.of(namePattern)
                .map(name -> replaceParameterNamesWithValues(name, combination))
                .map(name -> name.replace(COMBINATION_REPLACEMENT_PATTERN, combination.toString()))
                .orElseThrow(() -> new Coffee4JException(
                        "Test Name formatting was unsuccessful as it returned null"
                ));
    }

    private static String replaceParameterNamesWithValues(String name, Combination combination) {
        for (Map.Entry<Parameter, Value> mapping : combination.getParameterValueMap().entrySet()) {
            final String currentParameterName = mapping.getKey().getName();
            final String valueAsString = nullSafeToString(mapping.getValue().get());
            name = name.replace('{' + currentParameterName + '}', valueAsString);
        }
        return name;
    }

    /**
     * This is more or less a copy of {@link org.junit.platform.commons.util.StringUtils#nullSafeToString} from the
     * junit-jupiter-params project.
     */
    private static String nullSafeToString(Object obj) {
        if (obj == null) {
            return "null";
        }
        else {
            try {
                if (obj.getClass().isArray()) {
                    if (obj.getClass().getComponentType().isPrimitive()) {
                        return primitiveArrayToString(obj);
                    }
                    return Arrays.deepToString((Object[]) obj);
                }
                else
                    return obj.toString();
            }
            catch (Exception exception) {
                return defaultToString(obj);
            }
        }
    }

    private static String primitiveArrayToString(Object obj) {
        if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        }
        else if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        }
        else if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        }
        else if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        }
        else if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        }
        else if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        }
        else if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        }
        else if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        }
        else {
            throw new Coffee4JException("Something went wrong in nullSafeToString");
        }
    }

    /**
     * This is more or less a copy of {@link org.junit.platform.commons.util.StringUtils} from the
     * junit-jupiter-params project.
     */
    private static String defaultToString(Object obj) {
        if (obj == null) {
            return "null";
        }

        return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
    }

}
