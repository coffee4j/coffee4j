package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class ConstructorBasedFaultCharacterizationProvider
        implements FaultCharacterizationAlgorithmFactoryProvider, AnnotationConsumer<EnableFaultCharacterization> {

    private Class<? extends FaultCharacterizationAlgorithm> algorithmClass;

    @Override
    public void accept(EnableFaultCharacterization enableFaultCharacterization) {
        algorithmClass = enableFaultCharacterization.algorithm();
    }

    @Override
    public FaultCharacterizationAlgorithmFactory provide(Method method) {
        final Constructor<? extends FaultCharacterizationAlgorithm> constructor = getRequiredConstructor(algorithmClass);

        return faultCharacterizationConfiguration -> {
            try {
                return constructor.newInstance(faultCharacterizationConfiguration);
            } catch (Exception exception) {
                throw new Coffee4JException(
                        exception,
                        "Could not create a new instance of %s the given constructor %s",
                        algorithmClass.getName(),
                        constructor.getName()
                );
            }
        };
    }

    private static Constructor<? extends FaultCharacterizationAlgorithm> getRequiredConstructor(
            Class<? extends FaultCharacterizationAlgorithm> algorithmClass) {
        try {
            return algorithmClass.getConstructor(FaultCharacterizationConfiguration.class);
        } catch (NoSuchMethodException exception) {
            throw new Coffee4JException(
                    exception,
                    "The class %s must have public constructor which accepts a %s",
                    algorithmClass.getName(),
                    FaultCharacterizationConfiguration.class.getSimpleName()
            );
        }
    }

}
