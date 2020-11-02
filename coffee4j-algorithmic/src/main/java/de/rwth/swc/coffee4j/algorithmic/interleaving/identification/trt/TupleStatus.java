package de.rwth.swc.coffee4j.algorithmic.interleaving.identification.trt;

/**
 *  Enum used to classify {@link TupleNode}s used by {@link TupleRelationshipStrategy}.
 */
enum TupleStatus {
    HEALTHY,
    FAULTY,
    EXCEPTIONAL_COMBINATION,
    UNKNOWN
}
