package io.github.coffee4j.algorithmic.sequential.generator.ipogneg;

import io.github.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import io.github.coffee4j.algorithmic.constraint.HardConstraintCheckerFactory;

public record IpogNegConfiguration(ConstraintCheckerFactory constraintCheckerFactory, int strengthA) {

    public static IpogNegConfiguration defaultConfiguration() {
        return new IpogNegConfiguration(new HardConstraintCheckerFactory(), 0);
    }
}
