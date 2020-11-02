package de.rwth.swc.coffee4j.algorithmic.interleaving.identification;

/**
 * Since an {@link IdentificationStrategy} is used to identify failure- as well as exception-inducing combinations,
 * this enum is used to differentiate the identified combinations. A combinations is either {@link #EXCEPTION_INDUCING}
 * or {@link #FAILURE_INDUCING}.
 */
public enum CombinationType {
    EXCEPTION_INDUCING,
    FAILURE_INDUCING
}
