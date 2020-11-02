package de.rwth.swc.coffee4j.engine.report;

import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverter;
import org.junit.platform.commons.function.Try;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DelegatingModelBasedArgumentConverter extends ModelBasedArgumentConverter {
    
    private final List<ArgumentConverter> argumentConverters;

    public DelegatingModelBasedArgumentConverter(Collection<ArgumentConverter> argumentConverters) {
        Preconditions.notNull(argumentConverters);
        Preconditions.check(Try.call(() -> !argumentConverters.contains(null)).toOptional().orElse(true));

        this.argumentConverters = new ArrayList<>(argumentConverters);
    }
    
    @Override
    public void initialize(ModelConverter modelConverter) {
        for (ArgumentConverter argumentConverter : argumentConverters) {
            if (argumentConverter instanceof ModelBasedArgumentConverter) {
                ((ModelBasedArgumentConverter) argumentConverter).initialize(modelConverter);
            }
        }
    }
    
    @Override
    public boolean canConvert(Object argument) {
        for (ArgumentConverter argumentConverter : argumentConverters) {
            if (argumentConverter.canConvert(argument)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public Object convert(Object argument) {
        for (ArgumentConverter argumentConverter : argumentConverters) {
            if (argumentConverter.canConvert(argument)) {
                return argumentConverter.convert(argument);
            }
        }
        
        throw new IllegalStateException("This method should not be called if canConcert returns false");
    }
    
}
