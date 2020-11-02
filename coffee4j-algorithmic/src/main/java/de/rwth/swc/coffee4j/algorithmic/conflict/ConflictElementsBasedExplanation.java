package de.rwth.swc.coffee4j.algorithmic.conflict;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.List;

public abstract class ConflictElementsBasedExplanation implements ConflictExplanation {

    protected final List<ConflictElement> conflictElements;

    protected ConflictElementsBasedExplanation(List<ConflictElement> conflictElements) {
        Preconditions.notNull(conflictElements);
        Preconditions.check(!conflictElements.isEmpty());

        this.conflictElements = conflictElements;
    }

    public List<ConflictElement> getConflictElements() {
        return conflictElements;
    }

}
