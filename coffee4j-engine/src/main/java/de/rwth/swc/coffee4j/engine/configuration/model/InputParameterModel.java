package de.rwth.swc.coffee4j.engine.configuration.model;

import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.Buildable;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.Constraint;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An representation of a input parameter testModel for combinatorial testing. Consists of a testing strength,
 * readable name for identification, parameter, and exclusion and error constraints.
 * This testModel defines all important aspects of one combinatorial test.
 */
public final class InputParameterModel {

    private final int positiveTestingStrength;
    private final int negativeTestingStrength;

    private final String name;
    
    private final List<Parameter> parameters;
    
    private final List<Constraint> exclusionConstraints;
    private final List<Constraint> errorConstraints;
    
    private final List<Seed> positiveSeeds;
    private final Map<String, List<Seed>> negativeSeeds;
    
    private final List<StrengthGroup> positiveMixedStrengthGroups;
    
    private InputParameterModel(Builder builder) {
        Preconditions.notNull(builder, "builder must not be null");
        Preconditions.notNull(builder.name);
        Preconditions.notNull(builder.parameters);
        Preconditions.notNull(builder.exclusionConstraints);
        Preconditions.notNull(builder.errorConstraints);
        Preconditions.notNull(builder.positiveSeeds);
        Preconditions.notNull(builder.negativeSeeds);
        Preconditions.notNull(builder.positiveMixedStrengthGroups);
        Preconditions.check(builder.positiveTestingStrength >= 0);
        Preconditions.check(builder.positiveTestingStrength <= builder.parameters.size());
        Preconditions.check(builder.negativeTestingStrength >= 0);
        Preconditions.check(builder.negativeTestingStrength <= builder.parameters.size());
        Preconditions.check(!builder.parameters.contains(null));
        Preconditions.check(!builder.exclusionConstraints.contains(null));
        Preconditions.check(!builder.errorConstraints.contains(null));
        checkParameterDoesNotContainDuplicateName(builder.parameters);
        checkConstraintsOnlyReferenceValidParameters(
                builder.parameters, builder.exclusionConstraints, builder.errorConstraints);
        checkSeedsForCorrectParameters(builder.positiveSeeds, builder.parameters);
        checkNegativeSeedsForCorrectParametersAndConstraintNames(builder.negativeSeeds, builder.errorConstraints,
                builder.parameters);
        checkMixedStrengthGroupsForCorrectParameters(builder.positiveMixedStrengthGroups, builder.parameters);
        
        countAnonymousConstraints(builder.exclusionConstraints, builder.errorConstraints);

        this.positiveTestingStrength = builder.positiveTestingStrength;
        this.negativeTestingStrength = builder.negativeTestingStrength;
        this.name = builder.name;
        this.parameters = new ArrayList<>(builder.parameters);
        this.exclusionConstraints = new ArrayList<>(builder.exclusionConstraints);
        this.errorConstraints = new ArrayList<>(builder.errorConstraints);
        this.positiveSeeds = new ArrayList<>(builder.positiveSeeds);
        this.negativeSeeds = new HashMap<>(builder.negativeSeeds);
        this.positiveMixedStrengthGroups = new ArrayList<>(builder.positiveMixedStrengthGroups);
    }
    
    private static void countAnonymousConstraints(Collection<Constraint> exclusionConstraints,
                                           Collection<Constraint> errorConstraints) {
        int count = 0;

        for(Constraint constraint : exclusionConstraints) {
            if(constraint.getName().equals(Constraint.ConstraintConstants.ANONYMOUS_CONSTRAINT)) {
                constraint.setName("unknown-" + ++count);
            }
        }

        for(Constraint constraint : errorConstraints) {
            if(constraint.getName().equals(Constraint.ConstraintConstants.ANONYMOUS_CONSTRAINT)) {
                constraint.setName("unknown-" + ++count);
            }
        }
    }

    private static void checkParameterDoesNotContainDuplicateName(List<Parameter> parameters) {
        final Set<String> parameterNames = new HashSet<>();
        
        for (Parameter parameter : parameters) {
            final String parameterName = parameter.getName();
            if (parameterNames.contains(parameterName)) {
                throw new IllegalArgumentException("Parameter name " + parameterName + " appears twice");
            }
            parameterNames.add(parameterName);
        }
    }
    
    private static void checkConstraintsOnlyReferenceValidParameters(List<Parameter> parameters,
            Collection<Constraint> exclusionConstraints, Collection<Constraint> errorConstraints) {
        
        final Set<String> parameterNames = parameters.stream()
                .map(Parameter::getName)
                .collect(Collectors.toSet());
        
        for (Constraint constraint : exclusionConstraints) {
            for (String parameterName : constraint.getParameterNames()) {
                if (!parameterNames.contains(parameterName)) {
                    throw new IllegalArgumentException(
                            "Exclusion constraint \"" + constraint.getName() + "\"references unknown parameter \""
                            + parameterName + "\".");
                }
            }
        }
        
        for (Constraint constraint : errorConstraints) {
            for (String parameterName : constraint.getParameterNames()) {
                if (!parameterNames.contains(parameterName)) {
                    throw new IllegalArgumentException(
                            "Error constraint \"" + constraint.getName() + "\"references unknown parameter \""
                            + parameterName + "\".");
                }
            }
        }
    }
    
    private static void checkSeedsForCorrectParameters(List<Seed> seeds, List<Parameter> parameters) {
        for (Seed seed : seeds) {
            final Set<Parameter> seedParameters = seed.getCombination().getParameterValueMap().keySet();
            Preconditions.check(parameters.containsAll(seedParameters),
                    "All parameters used in seed test cases need to be present in the model");
        }
    }
    
    private void checkNegativeSeedsForCorrectParametersAndConstraintNames(Map<String, List<Seed>> negativeSeeds,
            List<Constraint> errorConstraints, List<Parameter> parameters) {
        
        for (Map.Entry<String, List<Seed>> errorConstraintSeeds : negativeSeeds.entrySet()) {
            final String constraintName = errorConstraintSeeds.getKey();
            Preconditions.check(errorConstraints.stream()
                    .anyMatch(constraint -> constraint.getName().equals(constraintName)),
                    "No matching error constraint for seed group " + constraintName);
            
            checkSeedsForCorrectParameters(errorConstraintSeeds.getValue(), parameters);
        }
    }
    
    private static void checkMixedStrengthGroupsForCorrectParameters(Collection<StrengthGroup> strengthGroups,
            Collection<Parameter> parameters) {
        
        for (StrengthGroup strengthGroup : strengthGroups) {
            for (Parameter parameter : strengthGroup.getParameters()) {
                Preconditions.check(parameters.contains(parameter), "Parameter " + parameter + " is contained "
                        + "in strength group " + strengthGroup + " but not in model parameters " + parameter);
            }
        }
    }
    
    public Builder toBuilder() {
        return new Builder(this);
    }
    
    /**
     * @return the positive testing strength
     */
    public int getPositiveTestingStrength() {
        return positiveTestingStrength;
    }

    /**
     * @return the negative testing strength
     */
    public int getNegativeTestingStrength() {
        return negativeTestingStrength;
    }
    
    /**
     * @return the descriptive name of the testModel
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return a copy of the list of all parameters of this testModel
     */
    public List<Parameter> getParameters() {
        return parameters;
    }
    
    /**
     * @return a copy of the list of all exclusion constraints. Test inputs may never violate those constraints as they
     * define combinations which are not possible testable (like testing safari on windows is not possible)
     */
    public List<Constraint> getExclusionConstraints() {
        return exclusionConstraints;
    }
    
    /**
     * @return a copy of the list of all error constraints. Test inputs may violate these constraints but they define
     * inputs on which the system under test should react in a destructive way like raising exception
     * (like trying to parse "asfd" as a number)
     */
    public List<Constraint> getErrorConstraints() {
        return errorConstraints;
    }
    
    /**
     * @return a copy of the list of all (partial) seed test cases. A test suite for this model should always include
     *     the seed test cases at least once
     */
    public List<Seed> getPositiveSeeds() {
        return positiveSeeds;
    }
    
    /**
     * @return a copy of the map of error constraint names to all (partial) seed tests cases for that constraint.
     *     A test suite for this model must always include the seed test cases at least once
     */
    public Map<String, List<Seed>> getNegativeSeeds() {
        return negativeSeeds;
    }
    
    /**
     * @return a list of all parameter strength groups which must be included in the final positive combinatorial
     *     test suite
     */
    public List<StrengthGroup> getPositiveMixedStrengthGroups() {
        return positiveMixedStrengthGroups;
    }
    
    /**
     * @return the number of parameters
     */
    public int size() {
        return parameters.size();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        final InputParameterModel model = (InputParameterModel) o;
        return positiveTestingStrength == model.positiveTestingStrength
                && negativeTestingStrength == model.negativeTestingStrength
                && Objects.equals(name, model.name)
                && Objects.equals(parameters, model.parameters)
                && Objects.equals(exclusionConstraints, model.exclusionConstraints)
                && Objects.equals(errorConstraints, model.errorConstraints)
                && Objects.equals(positiveSeeds, model.positiveSeeds)
                && Objects.equals(negativeSeeds, model.negativeSeeds)
                && Objects.equals(positiveMixedStrengthGroups, model.positiveMixedStrengthGroups);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(positiveTestingStrength, negativeTestingStrength, name, parameters,
                exclusionConstraints, errorConstraints, positiveSeeds, negativeSeeds, positiveMixedStrengthGroups);
    }
    
    @Override
    public String toString() {
        return "InputParameterModel{"
                + "positive testing strength=" + positiveTestingStrength
                + ", negative testing strength=" + negativeTestingStrength
                + ", name='" + name + '\''
                + ", parameters=" + parameters
                + ", exclusionConstraints=" + exclusionConstraints
                + ", errorConstraints=" + errorConstraints
                + ", positiveSeeds=" + positiveSeeds
                + ", negativeSeeds=" + negativeSeeds
                + ", positiveMixedStrengthGroups=" + positiveMixedStrengthGroups + '}';
    }
    
    public static Builder inputParameterModel(String name) {
        return new Builder(name);
    }
    
    /**
     * Realization of the builder pattern for constructing a {@link InputParameterModel}. Entry point is
     * {@link #inputParameterModel(String)}.
     */
    public static final class Builder implements
            Buildable<InputParameterModel> {
        
        private int positiveTestingStrength = 1;
        private int negativeTestingStrength = 0;

        private String name;
        
        private final List<Parameter> parameters = new ArrayList<>();
        
        private final List<Constraint> exclusionConstraints = new ArrayList<>();
        private final List<Constraint> errorConstraints = new ArrayList<>();
    
        private final List<Seed> positiveSeeds = new ArrayList<>();
        private final Map<String, List<Seed>> negativeSeeds = new HashMap<>();
        
        private final List<StrengthGroup> positiveMixedStrengthGroups = new ArrayList<>();
        
        private Builder(String name) {
            this.name = name;
        }
        
        private Builder(InputParameterModel model) {
            this.positiveTestingStrength = model.positiveTestingStrength;
            this.negativeTestingStrength = model.negativeTestingStrength;
            this.name = model.name;
            this.parameters.addAll(model.parameters);
            this.exclusionConstraints.addAll(model.exclusionConstraints);
            this.errorConstraints.addAll(model.errorConstraints);
            this.positiveSeeds.addAll(model.positiveSeeds);
            this.negativeSeeds.putAll(model.negativeSeeds);
        }
        
        /**
         * Sets the positive testing strength. Default is one.
         *
         * @param strength the testing strength
         * @return this
         */
        public Builder positiveTestingStrength(int strength) {
            this.positiveTestingStrength = strength;
            
            return this;
        }

        /**
         * Sets the negative testing strength. Default is zero.
         *
         * @param strength the testing strength
         * @return this
         */
        public Builder negativeTestingStrength(int strength) {
            this.negativeTestingStrength = strength;

            return this;
        }
        
        /**
         * Sets the name. This may not be {@code null} when {@link #build()} is called.
         *
         * @param name a descriptive name for the testModel
         * @return this
         */
        public Builder name(String name) {
            this.name = name;
            
            return this;
        }
        
        /**
         * Adds a parameter to the testModel.
         *
         * @param parameter the parameter. Must not be {@code null}
         * @return this
         */
        public Builder parameter(Parameter parameter) {
            Preconditions.notNull(parameter);
            
            parameters.add(parameter);
            
            return this;
        }
        
        /**
         * Adds the parameter builders to the testModel by building them. This is a convenience method as now the user
         * does not have to call {@link Parameter.Builder#build()} himself, therefore creating more readable code.
         *
         * @param parameter the parameter to be build. Must not be {@code null}
         * @return this
         */
        public Builder parameter(Parameter.Builder parameter) {
            Preconditions.notNull(parameter);
            
            parameters.add(parameter.build());
            
            return this;
        }
        
        /**
         * Adds all parameters to the testModel.
         *
         * @param parameters all parameters to be added. Must not be, nor contain {@code null}
         * @return this
         */
        public Builder parameters(Parameter... parameters) {
            Preconditions.notNull(parameters);
            
            this.parameters.clear();
            for (Parameter parameter : parameters) {
                parameter(parameter);
            }
            
            return this;
        }
    
        /**
         * Adds all parameters to the testModel.
         *
         * @param parameters all parameters to be added. Must not be, nor contain {@code null}
         * @return this
         */
        public Builder parameters(Collection<Parameter> parameters) {
            Preconditions.notNull(parameters);
    
            this.parameters.clear();
            for (Parameter parameter : parameters) {
                parameter(parameter);
            }
            
            return this;
        }
        
        /**
         * Builds all given builders and adds the result parameters to the testModel.
         *
         * @param parameters the parameters to be added. Must not be, nor contain {@code null}
         * @return this
         */
        public Builder parameters(Parameter.Builder... parameters) {
            Preconditions.notNull(parameters);
    
            this.parameters.clear();
            for (Parameter.Builder parameter : parameters) {
                parameter(parameter);
            }
            
            return this;
        }
        
        /**
         * Adds a exclusion constraint to the testModel.
         *
         * @param exclusionConstraint the exclusion constraint to be added. Must not be {@code null}
         * @return this
         */
        public Builder exclusionConstraint(Constraint exclusionConstraint) {
            Preconditions.notNull(exclusionConstraint);
            
            exclusionConstraints.add(exclusionConstraint);
            
            return this;
        }
        
        /**
         * Adds all exclusion constraints to the testModel.
         *
         * @param exclusionConstraints the exclusion constraints to be added. Must not be, nor contain {@code null}
         * @return this
         */
        public Builder exclusionConstraints(Constraint... exclusionConstraints) {
            Preconditions.notNull(exclusionConstraints);
            
            for (Constraint constraint : exclusionConstraints) {
                exclusionConstraint(constraint);
            }
            
            return this;
        }
    
        /**
         * Removes all exclusion constraints added until now.
         *
         * @return this
         */
        public Builder removeExclusionConstraints() {
            exclusionConstraints.clear();
            
            return this;
        }
        
        /**
         * Adds a error constraint to the testModel.
         *
         * @param errorConstraint the error constraint to be added. Must not be {@code null}
         * @return this
         */
        public Builder errorConstraint(Constraint errorConstraint) {
            Preconditions.notNull(errorConstraint);
            
            errorConstraints.add(errorConstraint);
            
            return this;
        }
        
        /**
         * Adds all error constraints to the testModel.
         *
         * @param errorConstraints the error constraints to be added. Must not be, nor contain {@code null}
         * @return this
         */
        public Builder errorConstraints(Constraint... errorConstraints) {
            Preconditions.notNull(errorConstraints);
            
            for (Constraint constraint : errorConstraints) {
                errorConstraint(constraint);
            }
            
            return this;
        }
    
        /**
         * Adds all error constraints to the testModel.
         *
         * @param errorConstraints the error constraints to be added. Must not be, nor contain {@code null}
         * @return this
         */
        public Builder errorConstraints(Collection<Constraint> errorConstraints) {
            Preconditions.notNull(errorConstraints);
            
            for (Constraint constraint : errorConstraints) {
                errorConstraint(constraint);
            }
            
            return this;
        }
    
        /**
         * Adds a seed test case for positive combinatorial testing to the testModel. A seed test will always be
         * included in the final test suite.
         *
         * <p>This method needs to be called <b>after</b> all parameters have been added.
         *
         * @param seed the (partial) seed test case. Must not be {@code null}
         * @return this
         */
        public Builder seed(Seed.Builder seed) {
            Preconditions.notNull(seed);
            
            positiveSeeds.add(seed.build(parameters));
            
            return this;
        }
    
        /**
         * Adds a seed test case for the negative combinatorial test of one error constraint to the testModel. A seed
         * test will always be included in the final test suite.
         *
         * <p>This method needs to be called after all parameter have been added.
         *
         * @param errorConstraintName the unique name of the error constraint to which's negative combinatorial test
         *     this seed is added
         * @param seed the (partial) seed test case. Must not be {@code null}
         * @return this
         */
        public Builder seed(String errorConstraintName, Seed.Builder seed) {
            Preconditions.notNull(seed);
        
            negativeSeeds.computeIfAbsent(errorConstraintName, key -> new ArrayList<>())
                    .add(seed.build(parameters));
        
            return this;
        }
    
        /**
         * Adds multiple seed test cases for positive combinatorial testing to the testModel. A seed test will always
         * be included in the final test suite.
         *
         * <p>This method needs to be called after all parameter have been added.
         *
         * @param seeds the (partial) seed test cases. Must not be {@code null}
         * @return this
         */
        public Builder seeds(Seed.Builder... seeds) {
            Preconditions.notNull(seeds);
            
            for (Seed.Builder seed : seeds) {
                seed(seed);
            }
            
            return this;
        }
    
        /**
         * Adds multiple seed test cases for the negative combinatorial test of one error constraint to the testModel
         * A seed test will always be included in the final test suite.
         *
         * <p>This method needs to be called after all parameter have been added.
         *
         * @param errorConstraintName the unique name of the error constraint to which's negative combinatorial test
         *     this seeds are added
         * @param seeds the (partial) seed test cases. Must not be {@code null}
         * @return this
         */
        public Builder seeds(String errorConstraintName, Seed.Builder... seeds) {
            Preconditions.notNull(seeds);
        
            final List<Seed> errorConstraintSeeds = negativeSeeds.computeIfAbsent(errorConstraintName,
                    key -> new ArrayList<>());
            
            for (Seed.Builder seed : seeds) {
                errorConstraintSeeds.add(seed.build(parameters));
            }
        
            return this;
        }
    
        /**
         * Adds the given mixed strength group for positive combinatorial testing to the model. A positive combinatorial
         * test suite must cover each mixed strength group at the given higher strength.
         *
         * <p>This method needs to be called after all parameters have been added.
         *
         * @param mixedStrengthGroup the mixed strength group to add for positive combinatorial testing
         * @return this
         */
        public Builder mixedStrengthGroup(StrengthGroup.Builder mixedStrengthGroup) {
            positiveMixedStrengthGroups.add(mixedStrengthGroup.build(parameters));
            
            return this;
        }
    
        /**
         * Adds the given mixed strength groups for positive combinatorial testing to the model. A positive
         * combinatorial test suite must cover each mixed strength group at the given higher strength.
         *
         * <p>This method needs to be called after all parameters have been added.
         *
         * @param mixedStrengthGroups the mixed strength groups to add for positive combinatorial testing
         * @return this
         */
        public Builder mixedStrengthGroups(StrengthGroup.Builder... mixedStrengthGroups) {
            for (StrengthGroup.Builder mixedStrengthGroup : mixedStrengthGroups) {
                positiveMixedStrengthGroups.add(mixedStrengthGroup.build(parameters));
            }
            
            return this;
        }
        
        /**
         * Builds the testModel. Add least one parameter needs to have been added by now.
         *
         * @return the constructed testModel
         */
        public InputParameterModel build() {
            return new InputParameterModel(this);
        }
        
    }
    
}
