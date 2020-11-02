package de.rwth.swc.coffee4j.algorithmic.conflict;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis.ConflictDiagnostician;
import de.rwth.swc.coffee4j.algorithmic.conflict.diagnosis.NoConflictDiagnostician;
import de.rwth.swc.coffee4j.algorithmic.conflict.explanation.ConflictExplainer;
import de.rwth.swc.coffee4j.algorithmic.conflict.explanation.NoConflictExplainer;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.Objects;
import java.util.function.Supplier;

public class ConflictDetectionConfiguration {

    public static ConflictDetectionConfiguration disable() {
        return new ConflictDetectionConfiguration(false, false, false, () -> null, false, () -> null);
    }

    private final boolean conflictDetectionEnabled;
    private final boolean shouldAbort;
    private final boolean conflictExplanationEnabled;
    private final Supplier<? extends ConflictExplainer> conflictExplainerSupplier;
    private final boolean conflictDiagnosisEnabled;
    private final Supplier<? extends ConflictDiagnostician> conflictDiagnosticianSupplier;

    private boolean implies(boolean a, boolean b) {
        return !a || b;
    }

    public ConflictDetectionConfiguration(boolean conflictDetectionEnabled, boolean shouldAbort,
            boolean conflictExplanationEnabled, Supplier<? extends ConflictExplainer> conflictExplainerSupplier,
            boolean conflictDiagnosisEnabled, Supplier<? extends ConflictDiagnostician> conflictDiagnosticianSupplier) {
        
        Preconditions.check(implies(conflictExplanationEnabled, conflictDetectionEnabled));
        Preconditions.check(implies(conflictExplanationEnabled, conflictExplainerSupplier != null));
        Preconditions.check(implies(conflictDiagnosisEnabled, conflictExplanationEnabled));
        Preconditions.check(implies(conflictDiagnosisEnabled, conflictDiagnosticianSupplier != null));

        this.conflictDetectionEnabled = conflictDetectionEnabled;
        this.shouldAbort = shouldAbort;
        this.conflictExplanationEnabled = conflictExplanationEnabled;

        if(!conflictExplanationEnabled) {
            this.conflictExplainerSupplier = NoConflictExplainer::new;
        } else  {
            this.conflictExplainerSupplier = conflictExplainerSupplier;
        }

        this.conflictDiagnosisEnabled = conflictDiagnosisEnabled;

        if(!conflictDiagnosisEnabled) {
            this.conflictDiagnosticianSupplier = NoConflictDiagnostician::new;
        } else {
            this.conflictDiagnosticianSupplier = conflictDiagnosticianSupplier;
        }
    }

    public boolean isConflictDetectionEnabled() {
        return conflictDetectionEnabled;
    }

    public boolean shouldAbort() {
        return shouldAbort;
    }

    public boolean isConflictExplanationEnabled() {
        return conflictExplanationEnabled;
    }

    public boolean isConflictDiagnosisEnabled() {
        return conflictDiagnosisEnabled;
    }

    public ConflictExplainer createConflictExplainer() {
        return conflictExplainerSupplier.get();
    }

    public ConflictDiagnostician createConflictDiagnostician() {
        return conflictDiagnosticianSupplier.get();
    }

    public TestModelExpander createTestModelExpander(CompleteTestModel testModel) {
        return new TestModelExpander(testModel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConflictDetectionConfiguration that = (ConflictDetectionConfiguration) o;
        return conflictDetectionEnabled == that.conflictDetectionEnabled &&
                shouldAbort == that.shouldAbort &&
                conflictExplanationEnabled == that.conflictExplanationEnabled &&
                conflictDiagnosisEnabled == that.conflictDiagnosisEnabled &&
                Objects.equals(conflictExplainerSupplier, that.conflictExplainerSupplier) &&
                Objects.equals(conflictDiagnosticianSupplier, that.conflictDiagnosticianSupplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conflictDetectionEnabled, shouldAbort, conflictExplanationEnabled,
                conflictExplainerSupplier, conflictDiagnosisEnabled, conflictDiagnosticianSupplier);
    }

    @Override
    public String toString() {
        return "ConflictDetectionConfiguration{" +
                "conflictDetectionEnabled=" + conflictDetectionEnabled +
                ", shouldAbort=" + shouldAbort +
                ", conflictExplanationEnabled=" + conflictExplanationEnabled +
                ", conflictExplainerSupplier=" + conflictExplainerSupplier +
                ", conflictDiagnosisEnabled=" + conflictDiagnosisEnabled +
                ", conflictDiagnosticianSupplier=" + conflictDiagnosticianSupplier +
                '}';
    }
    
}
