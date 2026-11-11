package io.github.coffee4j.engine.report;

import io.github.coffee4j.algorithmic.report.ArgumentConverter;
import io.github.coffee4j.algorithmic.report.CombinationArgument;
import io.github.coffee4j.engine.configuration.model.Combination;

/**
 * A {@link ArgumentConverter} cor converting {@link CombinationArgument}s
 * into {@link Combination} instances for reporting.
 */
public class CombinationArgumentConverter extends ModelBasedArgumentConverter {
    
    @Override
    public boolean canConvert(Object argument) {
        return argument instanceof CombinationArgument;
    }
    
    @Override
    public Object convert(Object argument) {
        final CombinationArgument combinationArgument = (CombinationArgument) argument;
        
        return modelConverter.convertCombination(combinationArgument.getCombination());
    }
    
}
