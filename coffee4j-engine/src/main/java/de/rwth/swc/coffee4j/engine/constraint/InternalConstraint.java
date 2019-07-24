package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.util.Preconditions;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.util.Objects;
import java.util.function.Function;

public class InternalConstraint {
    
    private final int id;
    private final Function<Model, Constraint> function;
    private final boolean markedAsCorrect;

    public InternalConstraint(int id, Function<Model, Constraint> function) {
        this(id, function, false);
    }

    public InternalConstraint(int id, Function<Model, Constraint> function, boolean markedAsCorrect) {
        Preconditions.check(id > 0);
        Preconditions.notNull(function);
        
        this.id = id;
        this.function = function;
        this.markedAsCorrect = markedAsCorrect;
    }
    
    InternalConstraint(InternalConstraint constraint) {
        Preconditions.notNull(constraint);
        
        this.id = constraint.id;
        this.function = constraint.function;
        this.markedAsCorrect = constraint.markedAsCorrect;
    }
    
    public int getId() {
        return id;
    }

    public boolean isMarkedAsCorrect() {
        return markedAsCorrect;
    }

    public Constraint apply(final Model model) {
        return function.apply(model);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalConstraint that = (InternalConstraint) o;
        return id == that.id &&
                markedAsCorrect == that.markedAsCorrect &&
                function.equals(that.function);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, function, markedAsCorrect);
    }

    @Override
    public String toString() {
        return "InternalConstraint{" +
                "id=" + id +
                ", function=" + function +
                ", markedAsCorrect=" + markedAsCorrect +
                '}';
    }
}