package de.rwth.swc.coffee4j.engine.process.phase.model;

import de.rwth.swc.coffee4j.algorithmic.conflict.ConflictDetectionConfiguration;
import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.engine.MockingTest;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverterFactory;
import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.algorithmic.interleaving.feedback.FeedbackCheckingStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.TestInputGenerationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.IdentificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.ExecutingInterleavingManagerFactory;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.GeneratingInterleavingManagerFactory;
import de.rwth.swc.coffee4j.engine.report.InterleavingExecutionReporter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InterleavingExecutionConfigurationTest implements MockingTest {
    private static InterleavingExecutionConfiguration configuration;
    private static InterleavingExecutionConfiguration generatingConfiguration;
    private static InterleavingExecutionConfiguration copy;

    private static final ExecutingInterleavingManagerFactory managerFactory = mock(ExecutingInterleavingManagerFactory.class);
    private static final ModelConverterFactory modelConverterFactory = mock(ModelConverterFactory.class);
    private static final ConflictDetectionConfiguration detectionConfiguration = mock(ConflictDetectionConfiguration.class);
    private static final TestInputGenerationStrategyFactory generationStrategyFactory = mock(TestInputGenerationStrategyFactory.class);
    private static final IdentificationStrategyFactory identificationStrategyFactory = mock(IdentificationStrategyFactory.class);
    private static final FeedbackCheckingStrategyFactory feedbackCheckingStrategyFactory = mock(FeedbackCheckingStrategyFactory.class);
    private static final ClassificationStrategyFactory classificationStrategyFactory = mock(ClassificationStrategyFactory.class);
    private static final ConstraintCheckerFactory constraintCheckerFactory = mock(ConstraintCheckerFactory.class);
    private static final InterleavingExecutionReporter reporter = mock(InterleavingExecutionReporter.class);
    private static final ArgumentConverter argumentConverter = mock(ArgumentConverter.class);

    private static final GeneratingInterleavingManagerFactory generatingManagerFactory = mock(GeneratingInterleavingManagerFactory.class);

    @BeforeAll
    static void prepareTests() {
        configuration = InterleavingExecutionConfiguration.executionConfiguration()
                .modelConverterFactory(modelConverterFactory)
                .conflictDetectionConfiguration(detectionConfiguration)
                .testInputGenerationStrategyFactory(generationStrategyFactory)
                .identificationStrategyFactory(identificationStrategyFactory)
                .feedbackCheckingStrategyFactory(feedbackCheckingStrategyFactory)
                .classificationStrategyFactory(classificationStrategyFactory)
                .constraintCheckingFactory(constraintCheckerFactory)
                .executionReporter(reporter)
                .argumentConverter(argumentConverter)
                .managerFactory(managerFactory)
                .isGenerating(false)
                .build();

        copy = InterleavingExecutionConfiguration.executionConfiguration()
                .modelConverterFactory(modelConverterFactory)
                .conflictDetectionConfiguration(detectionConfiguration)
                .testInputGenerationStrategyFactory(generationStrategyFactory)
                .identificationStrategyFactory(identificationStrategyFactory)
                .feedbackCheckingStrategyFactory(feedbackCheckingStrategyFactory)
                .classificationStrategyFactory(classificationStrategyFactory)
                .constraintCheckingFactory(constraintCheckerFactory)
                .executionReporter(reporter)
                .argumentConverter(argumentConverter)
                .managerFactory(managerFactory)
                .isGenerating(false)
                .build();

        generatingConfiguration = InterleavingExecutionConfiguration.generatingExecutionConfiguration()
                .modelConverterFactory(modelConverterFactory)
                .conflictDetectionConfiguration(detectionConfiguration)
                .testInputGenerationStrategyFactory(generationStrategyFactory)
                .identificationStrategyFactory(identificationStrategyFactory)
                .feedbackCheckingStrategyFactory(feedbackCheckingStrategyFactory)
                .classificationStrategyFactory(classificationStrategyFactory)
                .constraintCheckingFactory(constraintCheckerFactory)
                .executionReporter(reporter)
                .argumentConverter(argumentConverter)
                .managerFactory(generatingManagerFactory)
                .isGenerating(true)
                .build();
    }

    @Test
    void setsManagerFactory() {
        assertThat(configuration.getManagerFactory())
                .isEqualTo(managerFactory);

        assertThat(generatingConfiguration.getManagerFactory())
                .isEqualTo(generatingManagerFactory);
    }

    @Test
    void setsModelConverterFactory() {
        Assertions.assertThat(configuration.getModelConverterFactory())
                .isEqualTo(modelConverterFactory);

        Assertions.assertThat(generatingConfiguration.getModelConverterFactory())
                .isEqualTo(modelConverterFactory);
    }

    @Test
    void setsConflictDetectionConfiguration() {
        assertThat(configuration.getConflictDetectionConfiguration())
                .isEqualTo(detectionConfiguration);

        assertThat(generatingConfiguration.getConflictDetectionConfiguration())
                .isEqualTo(detectionConfiguration);
    }

    @Test
    void setsTestInputGenerationStrategyFactory() {
        assertThat(configuration.getTestInputGenerationStrategyFactory())
                .isEqualTo(generationStrategyFactory);

        assertThat(generatingConfiguration.getTestInputGenerationStrategyFactory())
                .isEqualTo(generationStrategyFactory);
    }

    @Test
    void setsIdentificationStrategyFactory() {
        assertThat(configuration.getIdentificationStrategyFactory())
                .isEqualTo(identificationStrategyFactory);

        assertThat(generatingConfiguration.getIdentificationStrategyFactory())
                .isEqualTo(identificationStrategyFactory);
    }

    @Test
    void setsCheckingStrategyFactory() {
        assertThat(configuration.getFeedbackCheckingStrategyFactory())
                .isEqualTo(feedbackCheckingStrategyFactory);

        assertThat(generatingConfiguration.getFeedbackCheckingStrategyFactory())
                .isEqualTo(feedbackCheckingStrategyFactory);
    }

    @Test
    void setsClassificationStrategyFactory() {
        assertThat(configuration.getClassificationStrategyFactory())
                .isEqualTo(classificationStrategyFactory);

        assertThat(generatingConfiguration.getClassificationStrategyFactory())
                .isEqualTo(classificationStrategyFactory);
    }

    @Test
    void setsCheckerFactory() {
        assertThat(configuration.getConstraintCheckerFactory())
                .isEqualTo(constraintCheckerFactory);

        assertThat(generatingConfiguration.getConstraintCheckerFactory())
                .isEqualTo(constraintCheckerFactory);
    }


    @Test
    void setsExecutionReporter() {
        Assertions.assertThat(configuration.getExecutionReporters())
                .containsExactly(reporter);

        Assertions.assertThat(generatingConfiguration.getExecutionReporters())
                .containsExactly(reporter);
    }

    @Test
    void setsArgumentConverter() {
        assertThat(configuration.getArgumentConverters())
                .containsExactly(argumentConverter);

        assertThat(generatingConfiguration.getArgumentConverters())
                .containsExactly(argumentConverter);
    }

    @Test
    void setsIsGenerating() {
        assertThat(configuration.isGenerating())
                .isEqualTo(false);

        assertThat(generatingConfiguration.isGenerating())
                .isEqualTo(true);
    }

    @Test
    void testEquals() {
        assertThat(copy)
                .isNotSameAs(configuration);

        assertThat(configuration)
                .isEqualTo(copy);
    }

    @Test
    void testHashCode() {
        assertThat(copy)
                .isNotSameAs(configuration);

        assertThat(configuration.hashCode())
                .isEqualTo(copy.hashCode());
    }
}