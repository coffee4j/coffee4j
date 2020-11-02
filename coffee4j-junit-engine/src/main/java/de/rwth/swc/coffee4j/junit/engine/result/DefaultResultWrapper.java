package de.rwth.swc.coffee4j.junit.engine.result;

import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.interleaving.EnableInterleavingConstraintGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.sequential.EnableSequentialConstraintGeneration;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Class used to wrap results during constraint generation. This class is able to differentiate between results of the
 * normal control-flow and thrown {@link Exception}s.
 */
public class DefaultResultWrapper implements ResultWrapper {
    private final boolean isConstraintGenerationEnabled;

    /**
     * Constructor using a flag to activate wrapped results for constraint generation or testing process as the
     * equals-method behaves different in the two modes.
     *
     * @param isConstraintGenerationEnabled true iff the wrapped result is used for error-constraint generation.
     */
    public DefaultResultWrapper(boolean isConstraintGenerationEnabled) {
        this.isConstraintGenerationEnabled = isConstraintGenerationEnabled;
    }

    /**
     * Constructor using the test method to activate wrapped results for constraint generation or testing process as the
     * equals-method behaves different in the two modes.
     *
     * @param method annotated test method. If {@link EnableInterleavingConstraintGeneration} or
     *     {@link EnableSequentialConstraintGeneration} is present, the wrapped result is used for error-constraint
     *     generation.
     */
    public DefaultResultWrapper(Method method) {
        isConstraintGenerationEnabled = method.isAnnotationPresent(EnableInterleavingConstraintGeneration.class)
                || method.isAnnotationPresent(EnableSequentialConstraintGeneration.class);
    }

    @Override
    public ExecutionResult runTestFunction(Callable<?> callable) {
        try {
            return new ValueResult(callable.call());
        } catch (Exception exception) {
            return new ExceptionResult(exception, isConstraintGenerationEnabled);
        }
    }
}
