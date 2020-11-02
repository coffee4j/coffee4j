package de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog;

import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveStrengthGroup;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.rwth.swc.coffee4j.algorithmic.util.Combinator.computeParameterCombinations;

/**
 * The "normal" strategy for covering all t-value-combinations for combinatorial test with testing strength t.
 * This means that all combinations of previous parameters with strength t - 1 are returned, as the current parameter
 * is added to every combination as described in {@link ParameterCombinationFactory}.
 */
public class MixedStrengthParameterCombinationFactory implements ParameterCombinationFactory {
    
    @Override
    public List<IntSet> create(int[] oldParameters, int nextParameter, TestModel model) {
        final Set<IntSet> mixedStrengthGroups = calculateMixedStrengthGroups(oldParameters, nextParameter, model);
        final Set<IntSet> defaultStrengthGroups = calculateMissingDefaultStrengthGroups(
                oldParameters, model.getDefaultTestingStrength(), mixedStrengthGroups);
        
        final List<IntSet> strengthGroups = new ArrayList<>(mixedStrengthGroups.size() + defaultStrengthGroups.size());
        strengthGroups.addAll(mixedStrengthGroups);
        strengthGroups.addAll(defaultStrengthGroups);
        
        return strengthGroups;
    }
    
    private Set<IntSet> calculateMixedStrengthGroups(int[] oldParameters, int nextParameter, TestModel model) {
        final IntSet relevantParameters = new IntOpenHashSet(oldParameters);
        relevantParameters.add(nextParameter);
    
        return model.getMixedStrengthGroups().stream()
                .map(PrimitiveStrengthGroup::getAllSubGroups)
                .flatMap(Collection::stream)
                .filter(relevantParameters::containsAll)
                .filter(set -> set.contains(nextParameter))
                .map(IntOpenHashSet::new)
                .map(set -> removeParameterFromSet(set, nextParameter))
                .collect(Collectors.toSet());
    }
    
    private IntSet removeParameterFromSet(IntSet set, int parameterToRemove) {
        set.remove(parameterToRemove);
        
        return set;
    }
    
    private Set<IntSet> calculateMissingDefaultStrengthGroups(int[] oldParameters, int strength,
            Set<IntSet> mixedStrengthGroups) {
    
        if (strength == 1 && mixedStrengthGroups.isEmpty()) {
            // strength == 1 => only empty set needed, but only if mixedStrengthGroups empty since otherwise contained
            return Set.of(IntSets.EMPTY_SET);
        } else if (strength == 0 || strength == 1) {
            // either strength zero, or strength one but empty set would already be contained in mixedStrengthGroups
            return Set.of();
        } else {
            return computeParameterCombinations(oldParameters, strength - 1).stream()
                    .filter(defaultStrengthGroup -> mixedStrengthGroups.stream()
                            .noneMatch(mixedStrengthGroup -> mixedStrengthGroup.containsAll(defaultStrengthGroup)))
                    .collect(Collectors.toSet());
        }
    }
    
}
