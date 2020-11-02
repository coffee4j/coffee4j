package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.converter;

import de.rwth.swc.coffee4j.algorithmic.report.ArgumentConverter;
import de.rwth.swc.coffee4j.engine.report.CombinationArgumentConverter;
import de.rwth.swc.coffee4j.engine.report.ParameterArgumentConverter;
import de.rwth.swc.coffee4j.engine.report.TupleListArgumentConverter;
import de.rwth.swc.coffee4j.engine.report.ValueArgumentConverter;
import de.rwth.swc.coffee4j.junit.engine.annotation.AnnotationConsumerInitializer;
import de.rwth.swc.coffee4j.junit.engine.annotation.Loader;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class for loading multiple {@link ArgumentConverter} via {@link ConverterProvider}. These providers are
 * discovered using the {@link ConverterSource} repeatable annotations. As such, multiple {@link ArgumentConverter}
 * provided by each {@link ConverterSource} are aggregated into one single list in this loader.
 * <p>
 * Per default, {@link CombinationArgumentConverter}, {@link ParameterArgumentConverter},
 * {@link TupleListArgumentConverter}, and {@link ValueArgumentConverter} are added to the end of the list.
 * Due to the way these argument converters are used inside the framework, it is possible to "overwrite"
 * argument conversion for any type which would be converted by the default converters by just specifying one.
 * The default argument converters will always be last.
 */
public class ConverterLoader implements Loader<List<ArgumentConverter>> {

    private static final List<ArgumentConverter> DEFAULT_ARGUMENT_RESOLVERS = List.of(
            new CombinationArgumentConverter(),
            new ParameterArgumentConverter(),
            new TupleListArgumentConverter(),
            new ValueArgumentConverter()
    );

    @Override
    public List<ArgumentConverter> load(Method method) {
        return Stream.concat(
            AnnotationSupport.findRepeatableAnnotations(method, ConverterSource.class).stream()
                .map(ConverterSource::value)
                .map(ReflectionSupport::newInstance)
                .map(provider -> AnnotationConsumerInitializer.initialize(method, provider))
                .map(provider -> provider.provide(method))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull),
            DEFAULT_ARGUMENT_RESOLVERS.stream()
        ).collect(Collectors.toList());
    }
}
