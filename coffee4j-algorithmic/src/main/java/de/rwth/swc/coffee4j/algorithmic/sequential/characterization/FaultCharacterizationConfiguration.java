package de.rwth.swc.coffee4j.algorithmic.sequential.characterization;

import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.report.Reporter;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;

import java.util.Objects;

/**
 * Class containing all information needed to perform fault characterization for combinatorial tests.
 */
public class FaultCharacterizationConfiguration {
    
    private final TestModel model;
    private final Reporter reporter;
    
    /**
     * Creates a new configuration out of an IPM and a reporter. It is not guaranteed that the
     * constraints checker in the model will be respected by an algorithm.
     *
     * @param model containing all parameters of the combinatorial test
     * @param reporter to give information to users during fault characterization execution
     */
    public FaultCharacterizationConfiguration(TestModel model, Reporter reporter) {
        this.model = Preconditions.notNull(model);
        this.reporter = Preconditions.notNull(reporter);
    }
    
    public TestModel getModel() {
        return model;
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
        return Objects.equals(model, other.model)
                && Objects.equals(reporter, other.reporter);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(model, reporter);
    }
    
    @Override
    public String toString() {
        return "FaultCharacterizationConfiguration{" + "testModel=" + model + ", reporter=" + reporter + '}';
    }
    
}
