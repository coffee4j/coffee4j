package de.rwth.swc.coffee4j.engine.configuration.execution;

import de.rwth.swc.coffee4j.algorithmic.classification.ClassificationStrategyFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.algorithmic.conflict.ConflictDetectionConfiguration;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.engine.MockingTest;
import de.rwth.swc.coffee4j.engine.converter.model.ModelConverterFactory;
import de.rwth.swc.coffee4j.engine.report.SequentialExecutionReporter;
import de.rwth.swc.coffee4j.engine.process.manager.sequential.CombinatorialTestManagerFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SequentialTestExecutionConfigurationTest implements MockingTest {

    private static final String managerFactoryString = "managerFactoryString";
    private static final String modelConverterFactoryString = "modelConverterFactoryString";
    private static final String detectionConfigurationString = "detectionConfigurationString";
    private static final String characterizationAlgorithmFactoryString = "characterizationAlgorithmFactoryString";
    private static final String classificationStrategyFactoryString = "classificationStrategyFactoryString";
    private static final String reporterString = "reporterString";
    private static final String argumentConverterString = "argumentConverterString";
    private static final String generatorString = "generatorString";

    private static SequentialExecutionConfiguration configuration;

    private static final CombinatorialTestManagerFactory managerFactory = mock(CombinatorialTestManagerFactory.class);
    private static final ModelConverterFactory modelConverterFactory = mock(ModelConverterFactory.class);
    private static final FaultCharacterizationAlgorithmFactory characterizationAlgorithmFactory = mock(FaultCharacterizationAlgorithmFactory.class);
    private static final ClassificationStrategyFactory classificationStrategyFactory = mock(ClassificationStrategyFactory.class);
    private static final ConflictDetectionConfiguration detectionConfiguration = mock(ConflictDetectionConfiguration.class);
    private static final SequentialExecutionReporter reporter = mock(SequentialExecutionReporter.class);
    private static final ArgumentConverter argumentConverter = mock(ArgumentConverter.class);
    private static final TestInputGroupGenerator generator = mock(TestInputGroupGenerator.class);
    private static SequentialExecutionConfiguration copy;

    @BeforeAll
    static void prepareTests() {
        configuration = SequentialExecutionConfiguration.executionConfiguration()
                .managerFactory(managerFactory)
                .modelConverterFactory(modelConverterFactory)
                .faultCharacterizationAlgorithmFactory(characterizationAlgorithmFactory)
                .classificationStrategyFactory(classificationStrategyFactory)
                .conflictDetectionConfiguration(detectionConfiguration)
                .executionReporter(reporter)
                .argumentConverter(argumentConverter)
                .generator(generator)
                .build();

        copy = SequentialExecutionConfiguration.executionConfiguration()
                .managerFactory(managerFactory)
                .modelConverterFactory(modelConverterFactory)
                .faultCharacterizationAlgorithmFactory(characterizationAlgorithmFactory)
                .classificationStrategyFactory(classificationStrategyFactory)
                .conflictDetectionConfiguration(detectionConfiguration)
                .executionReporter(reporter)
                .argumentConverter(argumentConverter)
                .generator(generator)
                .build();

        when(managerFactory.toString())
                .thenReturn(managerFactoryString);
        when(modelConverterFactory.toString())
                .thenReturn(modelConverterFactoryString);
        when(detectionConfiguration.toString())
                .thenReturn(detectionConfigurationString);
        when(characterizationAlgorithmFactory.toString())
                .thenReturn(characterizationAlgorithmFactoryString);
        when(classificationStrategyFactory.toString())
                .thenReturn(classificationStrategyFactoryString);
        when(reporter.toString())
                .thenReturn(reporterString);
        when(argumentConverter.toString())
                .thenReturn(argumentConverterString);
        when(generator.toString())
                .thenReturn(generatorString);
    }

    @Test
    void setsManagerFactory() {
        assertThat(configuration.getManagerFactory())
                .isEqualTo(managerFactory);
    }

    @Test
    void setsModelConverterFactory() {
        Assertions.assertThat(configuration.getModelConverterFactory())
                .isEqualTo(modelConverterFactory);
    }

    @Test
    void setsConflictDetectionConfiguration() {
        assertThat(configuration.getConflictDetectionConfiguration())
                .isEqualTo(detectionConfiguration);
    }

    @Test
    void setsCharacterizationAlgorithmFactory() {
        assertThat(configuration.getCharacterizationAlgorithmFactory())
                .hasValue(characterizationAlgorithmFactory);
    }

    @Test
    void setsClassificationStrategyFactory() {
        assertThat(configuration.getClassificationStrategyFactory())
                .isEqualTo(classificationStrategyFactory);
    }

    @Test
    void setsGenerator() {
        assertThat(configuration.getGenerators())
                .containsExactly(generator);
    }

    @Test
    void setsExecutionReporter() {
        Assertions.assertThat(configuration.getExecutionReporters())
                .containsExactly(reporter);
    }

    @Test
    void setsArgumentConverter() {
        assertThat(configuration.getArgumentConverters())
                .containsExactly(argumentConverter);
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

    @Test
    void testToString() {
        final String formattedString = "CombinatorialTestExecutionConfiguration{" +
                "managerFactory=" + managerFactoryString +
                ", modelConverterFactory=" + modelConverterFactoryString +
                ", conflictDetectionConfiguration=" + detectionConfigurationString +
                ", characterizationAlgorithmFactory=" + characterizationAlgorithmFactoryString +
                ", classificationStrategyFactory=" + classificationStrategyFactoryString +
                ", generators=[" + generatorString + "]" +
                ", executionReporters=[" + reporterString + "]" +
                ", argumentConverters=[" + argumentConverterString + "]" +
                ", executionMode=EXECUTE_ALL" +
                ", isConstraintGenerator=false" +
                "}";

        assertThat(configuration.toString())
                .isEqualTo(formattedString);
    }

}