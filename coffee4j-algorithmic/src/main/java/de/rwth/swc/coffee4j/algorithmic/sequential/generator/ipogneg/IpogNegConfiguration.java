package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.constraint.HardConstraintCheckerFactory;

import java.util.Objects;

public class IpogNegConfiguration {

    public static IpogNegConfiguration defaultConfiguration() {
        return new IpogNegConfiguration(new HardConstraintCheckerFactory(), 0);
    }

    private final ConstraintCheckerFactory constraintCheckerFactory;
    private final int strengthA;

    public IpogNegConfiguration(ConstraintCheckerFactory constraintCheckerFactory, int strengthA) {
        this.constraintCheckerFactory = constraintCheckerFactory;
        this.strengthA = strengthA;
    }

    public ConstraintCheckerFactory getConstraintCheckerFactory() {
        return constraintCheckerFactory;
    }

    public int getStrengthA() {
        return strengthA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpogNegConfiguration that = (IpogNegConfiguration) o;
        return strengthA == that.strengthA &&
                Objects.equals(constraintCheckerFactory, that.constraintCheckerFactory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(constraintCheckerFactory, strengthA);
    }
}
