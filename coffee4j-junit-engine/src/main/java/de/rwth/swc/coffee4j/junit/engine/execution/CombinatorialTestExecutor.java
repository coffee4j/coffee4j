package de.rwth.swc.coffee4j.junit.engine.execution;

import de.rwth.swc.coffee4j.engine.configuration.TestMethodConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.execution.SequentialExecutionConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.extension.Extension;
import de.rwth.swc.coffee4j.engine.process.manager.sequential.DefaultTestingSequentialPhaseManager;
import de.rwth.swc.coffee4j.engine.process.manager.sequential.SequentialPhaseManagerConfiguration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.execution.configuration.ConfigurationLoader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.extension.ExtensionLoader;
import de.rwth.swc.coffee4j.junit.engine.annotation.test.TestConfigurationLoader;
import de.rwth.swc.coffee4j.junit.engine.descriptor.*;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.interleaving.EnableInterleavingConstraintGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.EnableInterleavingGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration.GeneratingInterleavingConfigurationLoader;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.execution.configuration.InterleavingConfigurationLoader;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.engine.process.interleaving.*;
import de.rwth.swc.coffee4j.engine.process.manager.sequential.DefaultGeneratingSequentialPhaseManager;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * Initiates the process automation of the combinatorial tests via a supplied {@link ExecutionRequest}
 */
public class CombinatorialTestExecutor implements CombinatorialTestDescriptorVisitor {

    private final ExecutionContext executionContext;

    /**
     * Creates a new {@link CombinatorialTestExecutor} based upon the supplied {@link EngineExecutionListener}.
     *
     * @param executionContext the current execution context
     */
    public CombinatorialTestExecutor(ExecutionContext executionContext) {
        this.executionContext = Objects.requireNonNull(executionContext);
    }

    @Override
    public void visitCombinatorialTestEngineDescriptor(CombinatorialTestEngineDescriptor descriptor) {
        executionContext.getExecutionListener().executionStarted(descriptor);

        for (TestDescriptor child : descriptor.getChildren()) {
            if (child instanceof CombinatorialTestClassDescriptor) {
                final CombinatorialTestClassDescriptor classDescriptor = (CombinatorialTestClassDescriptor) child;

                classDescriptor.accept(this);
            } else {
                throw new JUnitException("All children below the engine descriptor must be of type "
                        + CombinatorialTestClassDescriptor.class.getCanonicalName()
                        + " but was " + child.getClass().getCanonicalName());
            }
        }

        executionContext.getExecutionListener().executionFinished(descriptor, TestExecutionResult.successful());
    }

    @Override
    public void visitCombinatorialTestClassDescriptor(CombinatorialTestClassDescriptor descriptor) {
        executionContext.getExecutionListener().executionStarted(descriptor);

        final Class<?> testClass = descriptor.getTestClass();
        final Object testInstance = createTestInstance(executionContext.getTestInstance().orElse(null), testClass);
        final LifecycleExecutor lifecycleExecutor = new LifecycleExecutor();
        final ExecutionContext classContext = executionContext
                .withTestInstance(testInstance)
                .withLifecycleExecutor(lifecycleExecutor);

        for (TestDescriptor child : descriptor.getChildren()) {
            if (child instanceof CombinatorialTestClassDescriptor || child instanceof CombinatorialTestMethodDescriptor) {
                final CombinatorialTestDescriptor combinatorialDescriptor = (CombinatorialTestDescriptor) child;

                lifecycleExecutor.executeBeforeClass(testClass);
                combinatorialDescriptor.accept(new CombinatorialTestExecutor(classContext));
                lifecycleExecutor.executeAfterClass(testClass);
            } else {
                throw new JUnitException("All children below the class descriptor must of of type "
                        + CombinatorialTestClassDescriptor.class.getCanonicalName() + " or "
                        + CombinatorialTestMethodDescriptor.class.getCanonicalName()
                        + " but was " + child.getClass().getCanonicalName());
            }
        }

        executionContext.getExecutionListener().executionFinished(descriptor, TestExecutionResult.successful());
    }

    private Object createTestInstance(Object outerClassInstance, Class<?> testClass) {
        if (outerClassInstance == null) {
            try {
                return createNewInstance(testClass.getDeclaredConstructor(), new Object[0]);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new JUnitException("Could not create test instance", e);
            }
        } else {
            try {
                return createNewInstance(testClass.getDeclaredConstructor(outerClassInstance.getClass()),
                        new Object[]{outerClassInstance});
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new JUnitException("Could not create inner class test instance", e);
            }
        }

    }

    private Object createNewInstance(Constructor<?> constructor, Object[] arguments)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        boolean canAccessBefore = constructor.canAccess(null);
        constructor.setAccessible(true);
        final Object instance = constructor.newInstance(arguments);
        constructor.setAccessible(canAccessBefore);

        return instance;
    }

    @Override
    public void visitCombinatorialTestMethodDescriptor(CombinatorialTestMethodDescriptor descriptor) {
        executionContext.getExecutionListener().executionStarted(descriptor);

        final ExecutionContext methodContext = executionContext.withMethodDescriptor(descriptor);
        final LifecycleExecutor lifecycleExecutor = executionContext.getRequiredLifecycleExecutor();
        final Object testInstance = executionContext.getRequiredTestInstance();

        lifecycleExecutor.executeBeforeMethod(testInstance);
        executeMethod(methodContext);
        lifecycleExecutor.executeAfterMethod(testInstance);

        executionContext.getExecutionListener().executionFinished(descriptor, TestExecutionResult.successful());
    }

    private void executeMethod(ExecutionContext methodContext) {
        final CombinationExecutor executor = new CombinationExecutor(methodContext);
        final Method testMethod = methodContext.getRequiredMethodDescriptor().getMethod();
        final TestMethodConfiguration testConfiguration = new TestConfigurationLoader(executor).load(testMethod);
        final List<Extension> extensions = new ExtensionLoader().load(testMethod);

        if (testMethod.isAnnotationPresent(EnableInterleavingGeneration.class)) {
            executeInterleavingTestOrGeneration(testConfiguration, extensions, testMethod);
        } else if (testMethod.isAnnotationPresent(EnableGeneration.class)) {
            executeSequentialTest(testConfiguration, extensions, testMethod);
        }
    }

    private void executeInterleavingTestOrGeneration(
            TestMethodConfiguration testConfiguration, List<Extension> extensions, Method testMethod) {

        if (AnnotationSupport.isAnnotated(testMethod, EnableInterleavingConstraintGeneration.class)) {
            executeInterleavingTest(testConfiguration, extensions,
                    new GeneratingInterleavingConfigurationLoader().load(testMethod));
        } else {
            executeInterleavingTest(testConfiguration, extensions,
                    new InterleavingConfigurationLoader(false).load(testMethod));
        }
    }

    private void executeInterleavingTest(
            TestMethodConfiguration testConfiguration, List<Extension> extensions,
            InterleavingExecutionConfiguration executionConfiguration) {

        final InterleavingPhaseManagerConfiguration configuration = InterleavingPhaseManagerConfiguration.phaseManagerConfiguration()
                .executionConfiguration(executionConfiguration)
                .testMethodConfiguration(testConfiguration)
                .extensions(extensions)
                .build();

        if (executionConfiguration.isGenerating()) {
            new DefaultGeneratingInterleavingPhaseManager(configuration).run();
        } else {
            new DefaultTestingInterleavingPhaseManager(configuration).run();
        }
    }

    private void executeSequentialTest(
            TestMethodConfiguration testConfiguration, List<Extension> extensions, Method testMethod) {

        final SequentialExecutionConfiguration executionConfiguration
                = new ConfigurationLoader().load(testMethod);

        final SequentialPhaseManagerConfiguration configuration = SequentialPhaseManagerConfiguration.phaseManagerConfiguration()
                .executionConfiguration(executionConfiguration)
                .testMethodConfiguration(testConfiguration)
                .extensions(extensions)
                .build();

        if (executionConfiguration.isConstraintGenerator()) {
            new DefaultGeneratingSequentialPhaseManager(configuration).run();
        } else {
            new DefaultTestingSequentialPhaseManager(configuration).run();
        }
    }
}
