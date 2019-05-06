package de.rwth.swc.coffee4j.model.diagnosis;

import java.util.Objects;

public class ConstraintDiagnosisConfiguration {

    public static ConstraintDiagnosisConfiguration disable() {
        return new ConstraintDiagnosisConfiguration(false, false);
    }

    public static ConstraintDiagnosisConfiguration enable() {
        return new ConstraintDiagnosisConfiguration(true, true);

    }

    public static ConstraintDiagnosisConfiguration enableButDoNotSkip() {
        return new ConstraintDiagnosisConfiguration(true, false);

    }

    private final boolean enabled;
    private final boolean shouldSkip;

    public ConstraintDiagnosisConfiguration(boolean enabled, boolean shouldSkip) {
        this.enabled = enabled;
        this.shouldSkip = shouldSkip;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean shouldSkip() {
        return shouldSkip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstraintDiagnosisConfiguration that = (ConstraintDiagnosisConfiguration) o;
        return enabled == that.enabled &&
                shouldSkip == that.shouldSkip;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, shouldSkip);
    }

    @Override
    public String toString() {
        return "ConstraintDiagnosisConfiguration{" +
                "enabled=" + enabled +
                ", shouldSkip=" + shouldSkip +
                '}';
    }
}
