package io.github.coffee4j.engine.configuration.model.constraints.methodbased;

import io.github.coffee4j.algorithmic.util.Preconditions;

import java.util.List;

/**
 * A helper function used to define constraints which involves one parameter.
 *
 * @param <A> type of the first parameter
 */
@FunctionalInterface
public interface BooleanFunction1<A> extends ConstraintFunction {
    
    @SuppressWarnings("unchecked")
    default boolean check(List<?> arguments) {
        Preconditions.notNull(arguments);
        Preconditions.check(arguments.size() == 1);
        
        final A a = (A) arguments.getFirst();
        
        return apply(a);
    }
    
    /**
     * Checks whether the value give for the parameter is allowed.
     *
     * @param a the value for the first parameter
     * @return whether the value is allowed
     */
    boolean apply(A a);
    
}
