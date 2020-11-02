package de.rwth.swc.coffee4j.engine.report;

import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.algorithmic.report.ParameterArgument;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;

/**
 * A {@link ArgumentConverter} cor converting {@link ParameterArgument}s
 * into {@link Parameter} instances for reporting.
 */
public class ParameterArgumentConverter extends ModelBasedArgumentConverter {
    
    @Override
    public boolean canConvert(Object argument) {
        return argument instanceof ParameterArgument;
    }
    
    @Override
    public Object convert(Object argument) {
        final ParameterArgument parameterArgument = (ParameterArgument) argument;
        
        return modelConverter.convertParameter(parameterArgument.getParameter());
    }
    
}
