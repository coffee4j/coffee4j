package de.rwth.swc.coffee4j.junit.engine.discovery;

import de.rwth.swc.coffee4j.junit.engine.UniqueIdGenerator;
import de.rwth.swc.coffee4j.engine.process.util.ReversedUnmodifiableListView;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.discovery.UniqueIdSelector;
import org.junit.platform.engine.support.discovery.SelectorResolver;

import java.util.List;
import java.util.Set;

public class UniqueIdSelectorResolver implements SelectorResolver {
    
    private static final String INVALID_ID_MESSAGE = "Invalid uniqueId %s: %s.";
    
    @Override
    public SelectorResolver.Resolution resolve(UniqueIdSelector selector, SelectorResolver.Context context) {
        final UniqueId uniqueId = selector.getUniqueId();
        final List<UniqueId.Segment> segments = uniqueId.getSegments();
        
        if (segments.size() < 2) {
            return Resolution.unresolved();
        }
        
        String className = null;
        String methodName = null;
        
        for (UniqueId.Segment segment : ReversedUnmodifiableListView.of(segments)) {
            if (isMethodSegment(segment)) {
                if (methodName == null) {
                    methodName = segment.getValue();
                } else {
                    throw new JUnitException(String.format(INVALID_ID_MESSAGE, uniqueId, "Multiple method segments"));
                }
            } else if (isClassSegment(segment)) {
                className = segment.getValue();
                break;
            } else if (!isCombinationSegment(segment)) {
                throw new JUnitException(String.format(INVALID_ID_MESSAGE, uniqueId, "Invalid segment type"));
            }
        }
        
        if (className == null) {
            throw new JUnitException(String.format(INVALID_ID_MESSAGE, uniqueId, "Could not extract class name"));
        } else if (methodName == null) {
            return Resolution.selectors(Set.of(DiscoverySelectors.selectClass(className)));
        } else {
            return Resolution.selectors(Set.of(DiscoverySelectors.selectMethod(className, methodName)));
        }
    }
    
    private static boolean isCombinationSegment(UniqueId.Segment segment) {
        return segment.getType().equalsIgnoreCase(UniqueIdGenerator.SEGMENT_TYPE_COMBINATION);
    }
    
    private static boolean isMethodSegment(UniqueId.Segment segment) {
        return segment.getType().equalsIgnoreCase(UniqueIdGenerator.SEGMENT_TYPE_METHOD);
    }
    
    private static boolean isClassSegment(UniqueId.Segment segment) {
        return segment.getType().equalsIgnoreCase(UniqueIdGenerator.SEGMENT_TYPE_CLASS);
    }
    
}
