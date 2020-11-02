package de.rwth.swc.coffee4j.junit.engine.annotation.util;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import org.apache.commons.lang3.StringUtils;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ModifierSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Utility class that provides helper methods for reflection related tasks
 */
public class ReflectionUtils {

    private ReflectionUtils() {}

    /**
     * Extracts the input for a method from a {@link Combination}
     *
     * @param combination the combination from which to extract the input
     * @param method the method for which to extract the inputs.
     *               Its {@link Parameter} parameters should be annotated with {@link InputParameter}
     * @return the extracted input for the method
     * @see #extractMethodParameterName(Parameter)
     */
    public static Object[] getInputForMethod(Combination combination, Method method) {
        return Arrays.stream(method.getParameters())
                .map(ReflectionUtils::extractMethodParameterName)
                .map(combination::getRawValue)
                .toArray();
    }

    /**
     * Extracts the assignment of {@link de.rwth.swc.coffee4j.engine.configuration.model.Parameter input parameters}
     * to the {@link Parameter method parameters}
     *
     * @param method the method from which to extract the map from
     * @return the map, which assigns every {@link de.rwth.swc.coffee4j.engine.configuration.model.Parameter input parameter}
     * an position in the {@link Parameter} list of a {@link Method} according to the names of the
     * {@link de.rwth.swc.coffee4j.engine.configuration.model.Parameter input parameters}
     */
    public static Map<String, Integer> extractMethodParameterPositionMap(Method method) {
        final List<String> parameterNames = ReflectionUtils.extractMethodParameterNames(method);
        return IntStream.range(0, parameterNames.size())
                .boxed()
                .collect(Collectors.toMap(
                        parameterNames::get,
                        Function.identity()
                ));
    }

    /**
     * Extracts the parameter names from all {@link Parameter} of a {@link Method}
     * according to {@link #extractMethodParameterName(Parameter)}
     *
     * @param method the method whose parameter names should be extracts
     * @return the names of the parameters
     */
    public static List<String> extractMethodParameterNames(Method method) {
        return Stream.of(method.getParameters())
                .map(ReflectionUtils::extractMethodParameterName)
                .collect(Collectors.toList());
    }

    /**
     * Extracts the name of the {@link de.rwth.swc.coffee4j.engine.configuration.model.Parameter input parameter}
     * from a {@link Parameter method parameter}
     *
     * @param parameter the method parameter
     * @return the name of the {@link de.rwth.swc.coffee4j.engine.configuration.model.Parameter input parameter}
     * @see InputParameter
     */
    public static String extractMethodParameterName(Parameter parameter) {
        return AnnotationSupport.findAnnotation(parameter, InputParameter.class)
                .map(InputParameter::value)
                .or(() -> Optional.ofNullable(parameter)
                        .filter(Parameter::isNamePresent)
                        .map(Parameter::getName)
                )
                .orElseThrow(() -> new Coffee4JException(
                        "Couldn't load name of parameter from method %s in class %s",
                        parameter.getDeclaringExecutable().getName(),
                        parameter.getDeclaringExecutable().getDeclaringClass().getName()
                ));
    }

    /**
     * Creates a new instance of a provided class.
     * <p>
     *     Wrapper around {@link ReflectionSupport#newInstance(Class, Object...)}
     *     for more meaningful exceptions in case of failure
     * </p>
     *
     * @param clazz the class to instantiate
     * @param constructorArgs the arguments of the constructor
     * @param <T> the type of the class to instantiate
     * @return the instantiated class
     * @see ReflectionSupport#newInstance(Class, Object...)
     */
    public static <T> T createNewInstance(Class<T> clazz, Object... constructorArgs) {
        try {
            return ReflectionSupport.newInstance(clazz, constructorArgs);
        } catch (Exception exception) {
            if (constructorArgs.length == 0)
                throw new Coffee4JException(
                    exception,
                    "Could not create a new instance of %s with a default constructor",
                    clazz.getSimpleName()
                );
            else
                throw new Coffee4JException(
                        exception,
                        "Could not create a new instance of %s with the given arguments %s",
                        clazz.getSimpleName(),
                        Arrays.toString(constructorArgs)
                );
        }
    }

    /**
     * Invokes the method on the target with the provided arguments
     * <p>
     *     Wrapper around {@link ReflectionSupport#invokeMethod(Method, Object, Object...)}
     *     for more meaningful exceptions in case of failure
     * </p>
     *
     * @param method the method to invoked
     * @param target the instance on which to invoke the method
     * @param args the arguments of the method
     * @return the value the method returns
     * @see ReflectionSupport#invokeMethod(Method, Object, Object...)
     */
    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return ReflectionSupport.invokeMethod(method, target, args);
        } catch (Exception exception) {
            if (args.length == 0)
                throw new Coffee4JException(
                    exception,
                    "Cannot invoke method '%s' on instance %s of class %s",
                    method.getName(),
                    target == null ? "null" : target.toString(),
                    target == null ? "null" : target.getClass().getName()
                );
            else
                throw new Coffee4JException(
                        exception,
                        "Cannot invoke method '%s' on instance %s of class %s with given arguments %s",
                        method.getName(),
                        target == null ? "null" : target.toString(),
                        target == null ? "null" : target.getClass().getName(),
                        Arrays.toString(args)
                );
        }
    }

    /**
     * Finds a method with provided parameter type in a class
     * <p>
     *     Wrapper around {@link ReflectionSupport#findMethod(Class, String, Class[])}
     *     for more meaningful exceptions in case of failure
     * </p>
     * @param clazz the class where to find the method
     * @param methodName the name of the method
     * @param parameterTypes the type of the parameters of the method
     * @return the searched method
     */
    public static Method findRequiredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return ReflectionSupport.findMethod(clazz, methodName, parameterTypes)
                .orElseThrow(() -> {
                    if (parameterTypes.length == 0)
                        return new Coffee4JException(
                                "The given method %s could not be found in given class %s",
                                methodName,
                                clazz.getName()
                        );
                    else
                        return new Coffee4JException(
                                "The method %s could not be found in class %s with the parameter types %s",
                                methodName,
                                clazz.getName(),
                                Arrays.toString(parameterTypes)
                        );
                });
    }

    /**
     * Gets the object returned by a method by potentially instantiating its declaring class
     * in case the method is not {@code static}
     *
     * @param method the method from which to get the object from
     * @return the object return by the method
     * @see #createNewInstance(Class, Object...)
     * @see #invokeMethod(Method, Object, Object...)
     */
    public static Object getObjectReturnedByMethod(Method method) {
        Object testInstance;

        if (!ModifierSupport.isStatic(method)) {
            testInstance = ReflectionUtils.createNewInstance(method.getDeclaringClass());
        }
        else
            testInstance = null;

        return ReflectionUtils.invokeMethod(method, testInstance);
    }

    /**
     * Attempts to find the method defined by the given name.
     * If the methodName is blank, a method in the class with the same name as the test method is returned.
     * Otherwise, either the method with the given name in the class (if the methodName is not fully qualified)
     * or the method in another class (if the method name is fully qualified) is returned.
     * To fully qualify a method name, put the class name before the method name and separate them with a"#".
     *
     * @param clazz the class where a non-qualified method is found
     * @param methodName the name of the method which should be returned
     * @return the method defined by the name with the rules explained above
     */
    public static Method findQualifiedMethod(Class<?> clazz, String methodName) {
        if (!StringUtils.isBlank(methodName)) {
            if (methodName.contains("#"))
                return getFullyQualifiedMethod(methodName);
            else
                return ReflectionUtils.findRequiredMethod(clazz, methodName);
        }
        else
            return getBlankMethod(clazz);
    }

    private static Method getFullyQualifiedMethod(String fullyQualifiedName) {
        final String[] methodParts = parseFullyQualifiedMethodName(fullyQualifiedName);
        final String className = methodParts[0];
        final String unqualifiedMethodName = methodParts[1];

        final Method method = getNamedMethod(className, unqualifiedMethodName);
        if (ModifierSupport.isStatic(method))
            return method;
        else
            throw new Coffee4JException(
                    "Model method %s from class %s is not static, but should be",
                    unqualifiedMethodName,
                    className
            );
    }

    /**
     * This is an adapted copy from {@link org.junit.platform.commons.util.ReflectionUtils} from the JUnit 5 Framework.
     */
    private static String[] parseFullyQualifiedMethodName(String fullyQualifiedName) {
        int indexOfFirstHashtag = fullyQualifiedName.indexOf('#');
        boolean validSyntax = (indexOfFirstHashtag > 0)
                && (indexOfFirstHashtag < fullyQualifiedName.length() - 1);

        if (!validSyntax)
            throw new Coffee4JException(
                    "[%s] is not a valid fully qualified method name: "
                            + "it must start with a fully qualified class name followed by a '#' "
                            + "and then the method name.",
                    fullyQualifiedName
            );

        String className = fullyQualifiedName.substring(0, indexOfFirstHashtag);
        String methodPart = fullyQualifiedName.substring(indexOfFirstHashtag + 1);
        String methodName = methodPart;

        if (methodPart.endsWith("()"))
            methodName = methodPart.substring(0, methodPart.length() -2);
        else if (methodPart.endsWith(")"))
            throw new Coffee4JException(
                    "Model method %s from class %s must not declare formal parameters",
                    methodPart,
                    className
            );

        return new String[] {className, methodName};
    }

    private static Method getNamedMethod(String className, String unqualifiedMethodName) {
        final Class<?> clazz = ReflectionSupport.tryToLoadClass(className)
                .getOrThrow(exception -> new Coffee4JException(
                        exception,
                        "Could not load class %s",
                        className
                ));
        return ReflectionUtils.findRequiredMethod(clazz, unqualifiedMethodName);
    }

    private static Method getBlankMethod(Class<?> clazz) {
        return ReflectionUtils.findRequiredMethod(clazz, clazz.getSimpleName());
    }

}
