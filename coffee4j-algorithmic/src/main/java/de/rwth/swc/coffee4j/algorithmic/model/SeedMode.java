package de.rwth.swc.coffee4j.algorithmic.model;

/**
 * Possible modes for a (partial) seed test case.
 */
public enum SeedMode {
    
    /**
     * A normal (partial) seed test case which must appear somewhere in the final combinatorial test suite.
     *
     * <p>A normal seed is just a combination which should be tested and is no more suspicious of producing a failure
     * than any other t-way combination which should be covered in the test suite. As a result, multiple partial seed
     * test cases can be combined with each other into a single test case in the final combinatorial test suite if
     * possible. For example, consider the seeds [1, 2, 3, -, -] and [1, -, 3, -, 2], where "-" means that a value is
     * not set. Since both have either the same value at each position or one seed does not have a value where the other
     * one has a value, they can be combined to the test case [1, 2, 3, ARBITRARY_VALUE, 2].
     */
    NON_EXCLUSIVE,
    
    /**
     * An exclusive (partial) seed test case which must appear somewhere in the final combinatorial test suite.
     *
     * <p>If two seeds are both in this mode, an generation algorithm should not attempt to put both of them in one
     * test case if that is possible.
     *
     * <p>For example, this can be used with combinations that are likely to be failure-inducing. Since multiple
     * failure-inducing combinations in one test case can make failure-detection harder, each failure-inducing
     * combination should appear in one test case on its own.
     *
     * <p>Exclusive seeds can always be combined with {@link #NON_EXCLUSIVE} seeds, just not with other
     * exclusive ones.
     */
    EXCLUSIVE
    
}
