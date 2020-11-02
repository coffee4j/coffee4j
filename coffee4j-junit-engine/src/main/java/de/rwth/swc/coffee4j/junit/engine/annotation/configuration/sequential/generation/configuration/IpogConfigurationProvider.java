package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.configuration;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.IpogConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.ReflectionUtils;
import de.rwth.swc.coffee4j.junit.engine.annotation.util.configuration.ConfigurationProvider;

import java.lang.reflect.Method;

public class IpogConfigurationProvider
        implements ConfigurationProvider<IpogConfiguration>, AnnotationConsumer<ConfigureIpog> {

    private Class<? extends ConstraintCheckerFactory> constraintCheckerFactoryClass;

    @Override
    public void accept(ConfigureIpog configureIpog) {
        constraintCheckerFactoryClass = configureIpog.constraintCheckerFactory();
    }

    @Override
    public IpogConfiguration provide(Method method) {
        final ConstraintCheckerFactory constraintCheckerFactory = ReflectionUtils.createNewInstance(constraintCheckerFactoryClass);

        return new IpogConfiguration(constraintCheckerFactory);
    }
    
}
