package io.github.coffee4j.algorithmic.conflict;

import io.github.coffee4j.algorithmic.util.Preconditions;

import java.util.List;

public record DiagnosisSet(List<DiagnosisElement> diagnosisElements) {
    public DiagnosisSet {
        Preconditions.notNull(diagnosisElements);
        Preconditions.check(!diagnosisElements.isEmpty());
    }
}
