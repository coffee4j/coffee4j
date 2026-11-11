package io.github.coffee4j.algorithmic.sequential.generator.ipog;

import io.github.coffee4j.algorithmic.constraint.ConstraintCheckerFactory;
import io.github.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesCheckerFactory;

public record IpogConfiguration(ConstraintCheckerFactory constraintCheckerFactory) {

    public static IpogConfiguration defaultConfiguration() {
        return new IpogConfiguration(new MinimalForbiddenTuplesCheckerFactory());
    }
}
