package io.github.coffee4j.junit.engine.annotation.parameter.combination;

import io.github.coffee4j.algorithmic.Coffee4JException;
import io.github.coffee4j.engine.configuration.model.Combination;
import io.github.coffee4j.junit.engine.annotation.parameter.ParameterContext;
import io.github.coffee4j.junit.engine.annotation.parameter.ParameterValueProvider;

/**
 * Provides the whole combination as a value for a combinatorial test method parameter.
 *
 * <p>This should only be automatically used by {@link InputCombination}.
 */
class InputCombinationValueProvider implements ParameterValueProvider {
    
    @Override
    public Object provideValue(ParameterContext parameterContext) {
        if (!parameterContext.getParameter().getType().isAssignableFrom(Combination.class)) {
            throw new Coffee4JException("The parameter annotated with " + InputCombination.class
                    + " must be assignable from type " + Combination.class
                    + " but it had type " + parameterContext.getParameter().getType() + ".");
        }
        
        return parameterContext.getCombination();
    }
    
}
