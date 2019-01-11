package de.rwth.swc.coffee4j.junit.provider.configuration.algorithm;

import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junit.platform.commons.JUnitException;

import java.lang.reflect.Constructor;

class ConstructorBasedProvider implements CharacterizationAlgorithmFactoryProvider, AnnotationConsumer<CharacterizationAlgorithm> {
    
    private Class<? extends FaultCharacterizationAlgorithm> algorithmClass;
    
    @Override
    public void accept(CharacterizationAlgorithm characterizationAlgorithm) {
        algorithmClass = characterizationAlgorithm.value();
    }
    
    @Override
    public FaultCharacterizationAlgorithmFactory provide(ExtensionContext extensionContext) {
        final Constructor<? extends FaultCharacterizationAlgorithm> constructor = getRequiredConstructor();
        
        return faultCharacterizationConfiguration -> {
            try {
                return constructor.newInstance(faultCharacterizationConfiguration);
            } catch (Exception e) {
                final String message = "Could not create a new instance of the given constructor " + constructor;
                throw new JUnitException(message, e);
            }
        };
    }
    
    private Constructor<? extends FaultCharacterizationAlgorithm> getRequiredConstructor() {
        try {
            return algorithmClass.getConstructor(FaultCharacterizationConfiguration.class);
        } catch (NoSuchMethodException e) {
            final String message = "The class " + algorithmClass.getName() + " must have a constructor which accepts a " + FaultCharacterizationConfiguration.class.getSimpleName();
            throw new JUnitException(message, e);
        }
    }
    
}
