package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesCheckerFactory;

import java.util.Objects;

public class IpogConfiguration {

    public static IpogConfiguration defaultConfiguration() {
        return new IpogConfiguration(new MinimalForbiddenTuplesCheckerFactory());
    }

    private final ConstraintCheckerFactory constraintCheckerFactory;

    public IpogConfiguration(ConstraintCheckerFactory constraintCheckerFactory) {
        this.constraintCheckerFactory = constraintCheckerFactory;
    }

    public ConstraintCheckerFactory getConstraintCheckerFactory() {
        return constraintCheckerFactory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpogConfiguration that = (IpogConfiguration) o;
        return constraintCheckerFactory.equals(that.constraintCheckerFactory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(constraintCheckerFactory);
    }
}
