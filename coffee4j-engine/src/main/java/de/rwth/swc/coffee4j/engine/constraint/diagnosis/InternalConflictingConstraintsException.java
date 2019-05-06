package de.rwth.swc.coffee4j.engine.constraint.diagnosis;

import de.rwth.swc.coffee4j.engine.util.Preconditions;

import java.util.List;

public class InternalConflictingConstraintsException extends IllegalStateException {

    private final List<InternalConflict> internalConflicts;

    public InternalConflictingConstraintsException(List<InternalConflict> internalConflicts) {
        Preconditions.notNull(internalConflicts);
        Preconditions.check(!internalConflicts.isEmpty());

        this.internalConflicts = internalConflicts;
    }

    public List<InternalConflict> getInternalConflicts() {
        return internalConflicts;
    }

    @Override
    public String getMessage() {
        return internalConflicts.size() + " conflicts identified";
    }
}
