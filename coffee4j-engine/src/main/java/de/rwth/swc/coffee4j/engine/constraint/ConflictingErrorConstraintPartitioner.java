package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.InputParameterModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class ConflictingErrorConstraintPartitioner {
    
    private final InputParameterModel inputParameterModel;
    private final Collection<InternalConstraint> exclusionConstraints;
    private final Collection<InternalConstraint> errorConstraints;
    private final TupleList negatedErrorTuples;
    
    private final List<InternalConstraint> hardConstraints;
    private final List<InternalConstraint> softConstraints;
    private final IntSet ignoredConstraintIds;
    private final Int2ObjectMap<IntList> valueBasedConflicts;
    
    ConflictingErrorConstraintPartitioner(InputParameterModel inputParameterModel, Collection<InternalConstraint> exclusionConstraints, Collection<InternalConstraint> errorConstraints, TupleList negatedErrorTuples) {
        this.inputParameterModel = inputParameterModel;
        this.exclusionConstraints = exclusionConstraints;
        this.errorConstraints = errorConstraints;
        this.negatedErrorTuples = negatedErrorTuples;
        
        this.hardConstraints = new ArrayList<>(exclusionConstraints);
        this.softConstraints = new ArrayList<>();
        this.ignoredConstraintIds = new IntOpenHashSet();
        
        this.valueBasedConflicts = identifyValueBasedConflicts();
        
        partitionConstraints(valueBasedConflicts);
    }
    
    public List<InternalConstraint> getHardConstraints() {
        return hardConstraints;
    }
    
    public List<InternalConstraint> getSoftConstraints() {
        return softConstraints;
    }
    
    public IntSet getIgnoredConstraintIds() {
        return ignoredConstraintIds;
    }
    
    public Int2ObjectMap<IntList> getValueBasedConflicts() {
        return valueBasedConflicts;
    }
    
    private Int2ObjectMap<IntList> identifyValueBasedConflicts() {
        final ConflictingErrorConstraintSearcher searcher = new ConflictingErrorConstraintSearcher(inputParameterModel, exclusionConstraints, errorConstraints);
        
        Int2ObjectMap<IntList> foundConflicts = new Int2ObjectOpenHashMap<>(negatedErrorTuples.getTuples().size());
        
        for (int i = 0; i < negatedErrorTuples.getTuples().size(); i++) {
            int[] negatedTuple = negatedErrorTuples.getTuples().get(i);
            IntList conflicting = searcher.diagnoseValueBasedConflict(negatedErrorTuples.getInvolvedParameters(), negatedTuple);
            
            if (!conflicting.isEmpty()) {
                foundConflicts.put(i, conflicting);
            }
        }
        
        return foundConflicts;
    }
    
    private void partitionConstraints(Int2ObjectMap<IntList> valueBasedConflicts) {
        if (valueBasedConflicts.isEmpty()) {
            hardConstraints.addAll(errorConstraints);
        } else {
            ignoredConstraintIds.addAll(intersection(valueBasedConflicts.values()));
            
            // hard-constraints: all exclusion-constraints + all error-constraints that do not appear in any conflict
            // ignored-constraints: error-constraints that do appear in all conflicts
            // soft-constraints: error-constraints that do appear in at least one conflict
            int size = errorConstraints.size() - ignoredConstraintIds.size();
            
            if (size > 0) {
                for (InternalConstraint constraint : errorConstraints) {
                    if (constraint.getId() == negatedErrorTuples.getId()) {
                        // the negated constraint must always be satisfied
                        hardConstraints.add(constraint);
                    } else if (ignoredConstraintIds.contains(constraint.getId())) {
                        // ignore; error-constraint appears in every conflict
                    } else if (anyContains(valueBasedConflicts, constraint.getId())) {
                        // soft; error-constraint appears in some conflicts
                        softConstraints.add(constraint);
                    } else {
                        // hard; error-constraint does not appear in any conflict
                        hardConstraints.add(constraint);
                    }
                }
            }
        }
    }
    
    private IntSet intersection(ObjectCollection<IntList> lists) {
        Iterator<IntList> iterator = lists.iterator();
        IntSet intersection = new IntOpenHashSet(iterator.next());
        
        while (iterator.hasNext()) {
            intersection.retainAll(iterator.next());
            
            if (intersection.isEmpty()) {
                break;
            }
        }
        
        return intersection;
    }
    
    private boolean anyContains(Int2ObjectMap<IntList> valueBasedConflicts, int id) {
        for (IntList list : valueBasedConflicts.values()) {
            if (list.contains(id)) {
                return true;
            }
        }
        
        return false;
    }
}
