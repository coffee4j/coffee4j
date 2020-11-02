package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.classification;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationConfiguration;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * {@link ClassificationStrategyFactoryProvider} using the constructor of the provided classification strategy.
 */
class ConstructorBasedClassificationStrategyProvider implements ClassificationStrategyFactoryProvider, AnnotationConsumer<EnableClassification> {
    private Class<? extends ClassificationStrategy> classificationStrategyClass;

    @Override
    public ClassificationStrategyFactory provide(Method method) {
        try {
            final Constructor<? extends ClassificationStrategy> constructor = classificationStrategyClass.getConstructor(ClassificationConfiguration.class);

            return classificationConfiguration -> {
                try {
                    return constructor.newInstance(classificationConfiguration);
                } catch (Exception e) {
                    throw new Coffee4JException(
                            e,
                            "Could not create a new instance of %s with constructor %s",
                            classificationStrategyClass.getName(),
                            constructor.getName()
                    );
                }
            };

        } catch (NoSuchMethodException e) {
            throw new Coffee4JException(
                    e,
                    "The class %s must have public constructor which accepts a %s",
                    classificationStrategyClass.getName(),
                    ClassificationConfiguration.class.getSimpleName()
            );
        }
    }

    @Override
    public void accept(EnableClassification classification) {
        classificationStrategyClass = classification.value();
    }
}
