package de.rwth.swc.coffee4j.junit.engine.descriptor;

import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

import java.lang.reflect.Method;

/**
 * JUnit description of a combination during execution
 */
public class CombinationDescriptor extends AbstractTestDescriptor {

    /**
     * Creates a new {@link CombinationDescriptor} with the supplied parent, {@link Combination}, and display name
     *
     * @param id the unique id for the combination
     * @param displayName the display name to set
     * @param testMethod the method which is responsible for executing the combination. This is done to enable
     *     the "Jump to Source" feature in many common IDEs
     */
    public CombinationDescriptor(UniqueId id, String displayName, Method testMethod) {
        super(id, displayName, MethodSource.from(testMethod));
    }

    @Override
    public Type getType() {
        return Type.TEST;
    }
    
}
