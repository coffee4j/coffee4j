package de.rwth.swc.coffee4j.junit.engine.annotation.util.configuration;

import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumer;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class ConfigurationBasedInstanceCreatorTest {

    @Test
    void preconditions() {
        assertThrows(NullPointerException.class,
                () -> ConfigurationBasedInstanceCreator.create(null, mock(Method.class)));
        assertThrows(NullPointerException.class,
                () -> ConfigurationBasedInstanceCreator.create(getClass(), null));
    }
    
    @Test
    void createsInstanceUsingNoArgsConstructorIfNoConfigurationSource() throws NoSuchMethodException {
        final Method method = getClass().getMethod("methodWithoutConfigurationSource");
        final ExampleClass instance = ConfigurationBasedInstanceCreator.create(ExampleClass.class, method);
        
        assertNotNull(instance);
        assertEquals("default", instance.someField);
    }
    
    @Test
    void createsInstanceUsingNoArgsConstructorIfNonMatchingConfigurationSource() throws NoSuchMethodException {
        final Method method = getClass().getMethod("methodWithConfigurationSource");
        final ConfigurationBasedInstanceCreatorTest instance = ConfigurationBasedInstanceCreator.create(
                ConfigurationBasedInstanceCreatorTest.class, method);
    
        assertNotNull(instance);
    }
    
    @Test
    void createsInstanceUsingConfigurationSourceIfPresent() throws NoSuchMethodException {
        final Method method = getClass().getMethod("methodWithConfigurationSource");
        final ExampleClass instance = ConfigurationBasedInstanceCreator.create(ExampleClass.class, method);
        
        assertNotNull(instance);
        assertEquals("firstValue", instance.someField);
    }
    
    @Test
    void createsInstanceUsingAnnotationBasedConfigurationSourceIfPresent() throws NoSuchMethodException {
        final Method method = getClass().getMethod("methodWithAnnotationConsumerBasedProvider");
        final ExampleClass instance = ConfigurationBasedInstanceCreator.create(ExampleClass.class, method);
        
        assertNotNull(instance);
        assertEquals("secondValue", instance.someField);
    }
    
    public void methodWithoutConfigurationSource() {
    }
    
    @ConfigurationSource(
            provider = SimpleConfigurationProvider.class,
            configurable = ExampleClass.class,
            configuration = String.class)
    public void methodWithConfigurationSource() {
    }
    
    @ConfigurationSource(
            provider = AnnotationConsumerBasedConfigurationProvider.class,
            configurable = ExampleClass.class,
            configuration = String.class)
    @ExampleAnnotation("secondValue")
    public void methodWithAnnotationConsumerBasedProvider() {
    }
    
    public static final class ExampleClass {
        
        private final String someField;
        
        public ExampleClass() {
            someField = "default";
        }
        
        public ExampleClass(String someField) {
            this.someField = someField;
        }
        
    }
    
    public static final class SimpleConfigurationProvider implements ConfigurationProvider<String> {
    
        @Override
        public String provide(Method method) {
            return "firstValue";
        }
    
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ExampleAnnotation {
        
        String value();
        
    }
    
    public static final class AnnotationConsumerBasedConfigurationProvider
            implements ConfigurationProvider<String>, AnnotationConsumer<ExampleAnnotation> {
    
        private String value;
    
        @Override
        public void accept(ExampleAnnotation exampleAnnotation) {
            value = exampleAnnotation.value();
        }
        
        @Override
        public String provide(Method method) {
            return value;
        }
    
    }

}

