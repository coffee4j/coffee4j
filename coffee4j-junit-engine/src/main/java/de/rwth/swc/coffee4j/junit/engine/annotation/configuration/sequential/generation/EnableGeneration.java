package de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation;

import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.Ipog;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;

import java.lang.annotation.*;

/**
 * This is a {@link GeneratorSource} using the {@link ConstructorBasedGeneratorProvider}
 * to create new instances of a {@link TestInputGroupGenerator} via a no-args constructor.
 * Since multiple generators are allowed,
 * just return multiple classes in the {@link #algorithms()} method to register more generators, or use any other
 * {@link GeneratorSource} since {@link GeneratorSource} is a repeatable annotation.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@GeneratorSource(ConstructorBasedGeneratorProvider.class)
public @interface EnableGeneration {

    /**
     * Gets the classes of the {@link TestInputGroupGenerator TestInputGroupGenerators}
     *
     * @return the classes used to generate {@link de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroup TestInputGroups}
     * for a {@link CombinatorialTest}.
     * Need to have a no-args constructor
     */
    Class<? extends TestInputGroupGenerator>[] algorithms() default { Ipog.class };
}
