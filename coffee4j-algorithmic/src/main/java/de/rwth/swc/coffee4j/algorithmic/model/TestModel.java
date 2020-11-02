package de.rwth.swc.coffee4j.algorithmic.model;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.algorithmic.constraint.NoConstraintChecker;

import java.util.List;

/**
 * Defines all important information needed for a combinatorial test.
 *
 * <p>This is in an interface to hide different possible implementations to the algorithms which use an instance of
 * the implementations. Possible implementations include the {@link CompleteTestModel} and other models which only
 * map to part of the {@link CompleteTestModel}. It is therefore suited for use in {@link TestInputGroupGenerator}
 * when multiple different groups are generated by the same generation algorithm for different subspaces in the
 * parameter space (e.g. different groups for positive and negative testing).
 *
 * <p>All implementations must be immutable with regards to the values returned by all methods given in this interface.
 */
public interface TestModel {
    
    /**
     * Gets the desired default testing strength configuration for the combinatorial test.
     *
     * <p>An testing strength of 2 indicates that all combinations between the values of any two parameters need to
     * be covered in at least one test cases in a combinatorial test suite. The same principle applies to higher
     * testing strengths.
     *
     * <p>The testing strength can be extended with several {@link #getMixedStrengthGroups() mixed strength groups}.
     * In this case certain subsets of parameters may be tested with a higher strength.
     *
     * @return the testing strength. Always needs to be positive ({@literal >}0)
     */
    int getDefaultTestingStrength();
    
    /**
     * Gets the mixed strength groups which extend the {@link #getDefaultTestingStrength() default testing strength}.
     *
     * <p>Each {@link PrimitiveStrengthGroup} indicates that the defined subset of parameters must be tested at the given
     * higher strength. This can be used if subsets of parameters are known to cause failures involving a high number
     * of parameters, or if certain parameters are especially important. Additionally, this feature can be used to
     * model independent sub-systems inside on model.
     *
     * <p>The strength of each {@link PrimitiveStrengthGroup} <b>must</b> be higher than the {@link #getDefaultTestingStrength()
     * default testing strength}. Otherwise, some algorithms might not work correctly anymore.
     *
     * @return the set of groups for using mixed strength combinatorial testing. Must never be {@code null}, instead an
     *     empty {@link List} should be returned if no groups are defined
     */
    List<PrimitiveStrengthGroup> getMixedStrengthGroups();
    
    /**
     * Gets the parameters used in the combinatorial testing by their respective number of values.
     *
     * <p>For example, a configuration of [2, 3, 4, 2, 7] indicates that the first parameter has two values,
     * the second one has three values, and so on. Test cases generated for this {@link TestModel} should always contain
     * the parameters in the same order as this configuration.
     *
     * <p>This algorithmic layer of combinatorial testing does <b>not</b> deal with concrete values. Such a translation
     * can be done at a higher level in the framework.
     *
     * @return an array containing the individual sizes of the parameters. Must never be {@code null} and all entries
     *         must be greater than or equal to two ({@literal >=}2) (i.e. each parameter must have at least two values)
     */
    int[] getParameterSizes();
    
    /**
     * Gets the number of parameter defined by this {@link TestModel}.
     *
     * <p>This must always be the same as the length of the array returned by {@link #getParameterSizes()}. While one
     * could also just always get the length of the array, this is meant as a convenience method to make the code more
     * readable.
     *
     * @return the number of parameters defined in this {@link TestModel}. Must be at least 1 ({@literal >=}1)
     */
    default int getNumberOfParameters() {
        return getParameterSizes().length;
    }
    
    /**
     * Gets the size (number of values) of one specific parameter.
     *
     * <p>Must always return the same as the array value returned by {@link #getParameterSizes()} at the given index.
     *
     * @param parameter the index of the parameter starting at zero
     * @return the number of values the given parameter has. Will always be at least 2 ({@literal >=}2)
     */
    default int getParameterSize(int parameter) {
        return getParameterSizes()[parameter];
    }
    
    /**
     * Gets the weight of a specific parameter value.
     *
     * <p>Values with higher weights should appear more frequently and/or earlier in a test suite.
     *
     * @param parameter the index of the parameter starting at zero
     * @param value the index of the value in the parameter starting at zero
     * @param defaultWeight a weight which is returned if no weight is specified
     * @return the weight for the value. {@code defaultWeight} if no other weight is specified
     */
    double getWeight(int parameter, int value, double defaultWeight);
    
    /**
     * Gets the weight of a specific parameter value.
     *
     * <p>Values with higher weights should appear more frequently and/or earlier in a test suite.
     *
     * @param parameter the index of the parameter starting at zero
     * @param value the index of the value in the parameter starting at zero
     * @return the weight for the value. Zero if no weight is given
     */
    default double getWeight(int parameter, int value) {
        return getWeight(parameter, value, 0);
    }
    
    /**
     * Gets a list of seed test cases which must be included in the final test suite.
     *
     * <p>Each seed test case array must have the same length as the one returned by {@link #getParameterSizes()} and
     * the parameter must appear in the same order. The values for each parameter are indicted with an integer number,
     * with 0 being the first value. The value must always be lower ({@literal <}) than the number returned by
     * {@link #getParameterSizes()} for the same parameter (e.g. if {@link #getParameterSizes()} returns [2, 3, 4] then
     * [2, 1, 0] would not be a valid seed since 2 is not smaller than 2 but [1, 2, 3] would be valid). The values must
     * also never be negative, except if they indicate a missing value in a partial seed test. Then -1 is allowed as
     * a representation of the missing value.
     *
     * <p>Algorithms which use these seeds should always count then towards the coverage criterion defined by
     * {@link #getDefaultTestingStrength()}.
     *
     * <p>Must not contain any seed test case which is already invalid according to the
     * {@link #getConstraintChecker() ConstraintChecker} or which cannot be extended to a valid test case
     *
     * @return the (partial) seed test cases for the model. Will never be {@code null} and no entry will be
     *         {@code null}. Furthermore the conditions described above must always hold
     */
    List<PrimitiveSeed> getSeeds();
    
    /**
     * Gets a {@link ConstraintChecker} which describes what value combinations of the parameters returned by
     * {@link #getParameterSizes()} are valid.
     *
     * <p>A combinatorial test suite generated from this model must never contain a test case which is invalid according
     * to this {@link ConstraintChecker}.
     *
     * @return the constraint checker which must be used to check whether a test case is valid. Must never be
     *         {@code null}. Instead, a {@link NoConstraintChecker} should be returned
     */
    ConstraintChecker getConstraintChecker();
}