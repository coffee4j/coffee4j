package de.rwth.swc.coffee4j.engine.characterization;

import de.rwth.swc.coffee4j.engine.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.engine.constraint.NoConstraintChecker;
import de.rwth.swc.coffee4j.engine.InputParameterModel;
import de.rwth.swc.coffee4j.engine.report.Reporter;
import de.rwth.swc.coffee4j.engine.util.Preconditions;

import java.util.Objects;

/**
 * Class containing all information needed to perform fault characterization for combinatorial tests.
 */
public final class FaultCharacterizationConfiguration {
    
    private final InputParameterModel model;
    private final ConstraintChecker checker;
    private final Reporter reporter;
    
    /**
     * Creates a new configuration out of an IPM and a reporter. As no {@link ConstraintChecker} is given,
     * {@link #getChecker()} will return a {@link NoConstraintChecker}.
     *
     * @param model    containing all parameters of the combinatorial test
     * @param reporter to give information to users during fault characterization execution
     */
    public FaultCharacterizationConfiguration(InputParameterModel model, Reporter reporter) {
        this(model, new NoConstraintChecker(), reporter);
    }
    
    /**
     * Creates a new configuration out of an IPM, reporter and constraints checker. It is not guaranteed that the
     * constraints checker will be respected by an algorithm.
     *
     * @param model    containing all parameters of the combinatorial test
     * @param checker  to define which combinations are not allowed
     * @param reporter to give information to users during fault characterization execution
     */
    public FaultCharacterizationConfiguration(InputParameterModel model, ConstraintChecker checker, Reporter reporter) {
        this.model = Preconditions.notNull(model);
        this.checker = Preconditions.notNull(checker);
        this.reporter = Preconditions.notNull(reporter);
    }
    
    public InputParameterModel getModel() {
        return model;
    }
    
    public ConstraintChecker getChecker() {
        return checker;
    }
    
    public Reporter getReporter() {
        return reporter;
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        FaultCharacterizationConfiguration other = (FaultCharacterizationConfiguration) object;
        return Objects.equals(model, other.model) && Objects.equals(checker, other.checker) && Objects.equals(reporter, other.reporter);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(model, checker, reporter);
    }
    
    @Override
    public String toString() {
        return "FaultCharacterizationConfiguration{" + "model=" + model + ", checker=" + checker + ", reporter=" + reporter + '}';
    }
    
}
