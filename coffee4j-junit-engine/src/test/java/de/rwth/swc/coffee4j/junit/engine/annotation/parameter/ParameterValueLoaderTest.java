package de.rwth.swc.coffee4j.junit.engine.annotation.parameter;

import de.rwth.swc.coffee4j.algorithmic.Coffee4JException;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.Map;

import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.Value.value;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParameterValueLoaderTest {
    
    private static final Combination COMBINATION = Combination.of(Map.of(
            parameter("first").values(0, 1).build(), value(0, 0),
            parameter("second").values(2, 3).build(), value(1, 3),
            parameter("third").values(4, 5).build(), value(0, 4)));
    
    @Test
    @SuppressWarnings("ConstantConditions")
    void doesNotAcceptNullContext() {
        assertThrows(NullPointerException.class, () -> new ParameterValueLoader().load(null));
    }
    
    @Test
    void throwsExceptionIfParameterHasNoParameterValueSource() throws NoSuchMethodException {
        final ParameterContext context = getContextForParameterAtIndex(0);
        final ParameterValueLoader loader = new ParameterValueLoader();
        
        assertThrows(Coffee4JException.class, () -> loader.load(context));
    }
    
    @Test
    void loadsValueFromNormalAnnotation() throws NoSuchMethodException {
        final ParameterContext context = getContextForParameterAtIndex(1);
        final ParameterValueLoader loader = new ParameterValueLoader();
        
        final Object providedValue = loader.load(context);
        
        assertEquals(0, providedValue);
        assertEquals(context, CustomParameterValueSource.getGivenParameterContext());
    }
    
    @Test
    void loadsValueFromCustomAnnotation() throws NoSuchMethodException {
        final ParameterContext context = getContextForParameterAtIndex(2);
        final ParameterValueLoader loader = new ParameterValueLoader();
        
        final Object providedValue = loader.load(context);
        
        assertEquals("3someValue", providedValue);
        assertEquals(context, CustomAnnotationConsumerBasedProvider.getGivenParameterContext());
    }
    
    @Test
    void loadsNullValue() throws NoSuchMethodException {
        final ParameterContext context = getContextForParameterAtIndex(3);
        final ParameterValueLoader loader = new ParameterValueLoader();
    
        final Object providedValue = loader.load(context);
        
        assertNull(providedValue);
    }
    
    private ParameterContext getContextForParameterAtIndex(int index) throws NoSuchMethodException {
        final Parameter parameter = getClass()
                .getMethod("testMethod", String.class, String.class, String.class, String.class)
                .getParameters()[index];
        return ParameterContext.of(parameter, COMBINATION);
    }
    
    @SuppressWarnings("unused")
    public void testMethod(String parameterWithoutAnnotation,
            @ParameterValueSource(CustomParameterValueSource.class) String parameterWithNormalAnnotation,
            @CustomAnnotation("someValue") String parameterWithCustomAnnotation,
            @ParameterValueSource(NullSource.class) String parameterWithNullValueProvider) {
        // only used as target for reflection
    }
    
    public static class CustomParameterValueSource implements ParameterValueProvider {
        
        private static ParameterContext givenParameterContext;
        
        public static ParameterContext getGivenParameterContext() {
            return givenParameterContext;
        }
    
        @Override
        public Object provideValue(ParameterContext parameterContext) {
            givenParameterContext = parameterContext;
            
            return parameterContext.getCombination().getRawValue("first");
        }
    
    }
    
    @Documented
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @ParameterValueSource(CustomAnnotationConsumerBasedProvider.class)
    public @interface CustomAnnotation {
        
        String value();
    
    }
    
    public static class CustomAnnotationConsumerBasedProvider
            implements AnnotationConsumer<CustomAnnotation>, ParameterValueProvider {
    
        private static ParameterContext givenParameterContext;
    
        private String value;
    
        public static ParameterContext getGivenParameterContext() {
            return givenParameterContext;
        }
    
        @Override
        public void accept(CustomAnnotation customAnnotation) {
            value = customAnnotation.value();
        }
    
        @Override
        public Object provideValue(ParameterContext parameterContext) {
            givenParameterContext = parameterContext;
            
            return parameterContext.getCombination().getRawValue("second") + value;
        }
    
    }
    
    public static class NullSource implements ParameterValueProvider {
    
        @Override
        public Object provideValue(ParameterContext parameterContext) {
            return null;
        }
    
    }
    
}
