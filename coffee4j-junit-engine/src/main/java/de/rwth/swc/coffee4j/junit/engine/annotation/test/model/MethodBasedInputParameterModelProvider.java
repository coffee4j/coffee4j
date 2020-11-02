package de.rwth.swc.coffee4j.junit.engine.annotation.test.model;

import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.ReflectionUtils;

import java.lang.reflect.Method;

import static de.rwth.swc.coffee4j.junit.engine.annotation.MethodBasedExtractionUtil.extractTypedObjectFromMethod;

/**
 * A provider loading a class from a method as described in {@link CombinatorialTest}.
 *
 * <p>This is a more or less direct copy of org.junit.jupiter.params.provider.MethodArgumentsProvider from the
 * junit-jupiter-params project.
 */
public class MethodBasedInputParameterModelProvider implements InputParameterModelProvider, AnnotationConsumer<CombinatorialTest> {

    private String methodName;

    @Override
    public void accept(CombinatorialTest combinatorialTest) {
        methodName = combinatorialTest.inputParameterModel();
    }

    @Override
    public InputParameterModel provide(Method method) {
        final Method modelMethod = ReflectionUtils.findQualifiedMethod(method.getDeclaringClass(), methodName);
        return extractModelFromMethod(modelMethod);
    }

    static InputParameterModel extractModelFromMethod(Method method) {
        return extractTypedObjectFromMethod(method,
                InputParameterModel.class,
                InputParameterModel.Builder.class
        );
    }
}
