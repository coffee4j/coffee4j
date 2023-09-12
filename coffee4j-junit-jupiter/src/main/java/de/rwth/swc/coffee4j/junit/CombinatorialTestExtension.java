package de.rwth.swc.coffee4j.junit;

import de.rwth.swc.coffee4j.junit.provider.configuration.ConfigurationLoader;
import de.rwth.swc.coffee4j.junit.provider.model.ModelLoader;
import de.rwth.swc.coffee4j.model.Combination;
import de.rwth.swc.coffee4j.model.InputParameterModel;
import de.rwth.swc.coffee4j.model.manager.CombinatorialTestConsumerManager;
import de.rwth.swc.coffee4j.model.manager.CombinatorialTestConsumerManagerConfiguration;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.util.Preconditions;

import java.lang.reflect.Method;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;
import static org.junit.platform.commons.util.AnnotationUtils.isAnnotated;

/**
 * The extension handling a {@link CombinatorialTest}. Creates one {@link CombinatorialTestInvocationContext}
 * for each test input.
 * <p>
 * This extension is more or less a copy of {@link org.junit.jupiter.params.ParameterizedTestExtension} in the
 * junit-jupiter-params project.
 */
public class CombinatorialTestExtension implements TestTemplateInvocationContextProvider {
    
    @Override
    public boolean supportsTestTemplate(ExtensionContext extensionContext) {
        if (!extensionContext.getTestMethod().isPresent()) {
            return false;
        }
        
        final Method testMethod = extensionContext.getRequiredTestMethod();
        if (!isAnnotated(testMethod, CombinatorialTest.class)) {
            return false;
        }
        
        return CombinatorialTestMethodContext.checkAggregatorOrder(testMethod);
    }
    
    public static Store getStore(ExtensionContext extensionContext) {
        return extensionContext.getStore(Namespace.create(CombinatorialTestExtension.class, extensionContext.getRequiredTestMethod(), extensionContext.getRequiredTestClass()));
    }
    
    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext extensionContext) {
        final Method testMethod = extensionContext.getRequiredTestMethod();
        final CombinatorialTestConsumerManagerConfiguration configuration = new ConfigurationLoader().load(extensionContext);
        final TestInputIterator iterator = new TestInputIterator();
        final InputParameterModel model = new ModelLoader().load(extensionContext);
        final CombinatorialTestConsumerManager manager = new CombinatorialTestConsumerManager(configuration, iterator::add, model);

        diagnoseConstraints(configuration, manager);

        manager.generateInitialTests();
        
        getStore(extensionContext).put(CombinatorialTestExecutionCallback.REPORTERS_KEY, configuration.getExecutionReporters());
        getStore(extensionContext).put(CombinatorialTestExecutionCallback.MANAGER_KEY, manager);
        
        final CombinatorialTestMethodContext methodContext = new CombinatorialTestMethodContext(testMethod, model);
        final CombinatorialTestNameFormatter nameFormatter = createNameFormatter(testMethod);
        
        Preconditions.condition(iterator.hasNext(), "Error: no test inputs were generated!");
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .map(testInput -> createInvocationContext(nameFormatter, methodContext, testInput));
    }

    private void diagnoseConstraints(CombinatorialTestConsumerManagerConfiguration configuration, CombinatorialTestConsumerManager manager) {
        if(configuration.getConflictDetectionConfiguration().isConflictDetectionEnabled()) {
            final boolean isConflictFree = manager.checkConstraintsForConflicts();

            if(configuration.getConflictDetectionConfiguration().shouldAbort()) {
                Preconditions.condition(isConflictFree, "Error: conflicts among constraints detected");
            }
        }
    }

    private CombinatorialTestNameFormatter createNameFormatter(Method testMethod) {
        final CombinatorialTest combinatorialTest = findAnnotation(testMethod, CombinatorialTest.class).orElseThrow(() -> new JUnitException("Illegal state: could not find combinatorial test annotation"));
        final String name = Preconditions.notBlank(combinatorialTest.name().trim(), () -> "Configuration error: @" + CombinatorialTest.class.getSimpleName() + " on method " + testMethod.getName() + " must be declared with a non-empty name.");
        return new CombinatorialTestNameFormatter(name);
    }
    
    private TestTemplateInvocationContext createInvocationContext(CombinatorialTestNameFormatter nameFormatter, CombinatorialTestMethodContext methodContext, Combination testInput) {
        return new CombinatorialTestInvocationContext(nameFormatter, methodContext, testInput);
    }
}
