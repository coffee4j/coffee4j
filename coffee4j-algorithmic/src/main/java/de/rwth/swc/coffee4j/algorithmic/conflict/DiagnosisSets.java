package de.rwth.swc.coffee4j.algorithmic.conflict;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.List;
import java.util.Objects;

public class DiagnosisSets implements ConflictExplanation {

    private final ConflictSet root;
    private final List<DiagnosisSet> sets;

    DiagnosisSets(ConflictSet root, List<DiagnosisSet> sets) {
        Preconditions.notNull(root);
        Preconditions.notNull(sets);
        Preconditions.check(!sets.isEmpty());

        this.root = root;
        this.sets = sets;
    }

    public ConflictSet getRootConflictSet() {
        return root;
    }

    public List<DiagnosisSet> getDiagnosisSets() {
        return sets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiagnosisSets that = (DiagnosisSets) o;
        return root.equals(that.root) &&
                sets.equals(that.sets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root, sets);
    }

    @Override
    public String toString() {
        return "DiagnosisSets{" +
                "root=" + root +
                ", sets=" + sets +
                '}';
    }
}
