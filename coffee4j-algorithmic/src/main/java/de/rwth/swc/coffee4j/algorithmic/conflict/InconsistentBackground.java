package de.rwth.swc.coffee4j.algorithmic.conflict;

import java.util.List;
import java.util.Objects;

public class InconsistentBackground extends ConflictElementsBasedExplanation {

    public InconsistentBackground(List<ConflictElement> conflictElements) {
        super(conflictElements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InconsistentBackground that = (InconsistentBackground) o;
        return conflictElements.equals(that.conflictElements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conflictElements);
    }

    @Override
    public String toString() {
        return "InconsistentBackground{" +
                "conflictElements=" + conflictElements +
                '}';
    }
}
