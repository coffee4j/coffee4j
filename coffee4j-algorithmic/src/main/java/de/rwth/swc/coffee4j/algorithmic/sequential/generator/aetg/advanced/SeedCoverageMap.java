package de.rwth.swc.coffee4j.algorithmic.sequential.generator.aetg.advanced;

import de.rwth.swc.coffee4j.algorithmic.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveSeed;
import de.rwth.swc.coffee4j.algorithmic.model.SeedMode;
import de.rwth.swc.coffee4j.algorithmic.model.TestModel;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.rwth.swc.coffee4j.algorithmic.util.PredicateUtil.not;

class SeedCoverageMap {
    
    private final List<PrimitiveSeed> missingPrioritizedSeeds;
    private final List<PrimitiveSeed> missingUnprioritizedSeeds;
    private final int numberOfParameters;
    private final ConstraintChecker constraintChecker;
    
    SeedCoverageMap(TestModel model) {
        numberOfParameters = model.getNumberOfParameters();
        this.constraintChecker = model.getConstraintChecker();
        
        missingPrioritizedSeeds = model.getSeeds().stream()
                .filter(PrimitiveSeed::hasPriority)
                .sorted(Comparator.comparingDouble(PrimitiveSeed::getPriority).reversed())
                .collect(Collectors.toList());
        missingUnprioritizedSeeds = model.getSeeds().stream()
                .filter(not(PrimitiveSeed::hasPriority))
                .sorted(Comparator.comparingInt(seed -> CombinationUtil.numberOfSetParameters(seed.getCombination())))
                .collect(Collectors.toList());
    }
    
    int[] getMostImportantPartialTestCase() {
        final int[] testCase = CombinationUtil.emptyCombination(numberOfParameters);
        boolean testCaseAddedInLastIteration = true;
        boolean containsSuspiciousSeed = false;
        
        while ((!missingPrioritizedSeeds.isEmpty() || !missingUnprioritizedSeeds.isEmpty())
                && testCaseAddedInLastIteration) {
            
            final Optional<PrimitiveSeed> nextSeed = getMostImportantCompatibleTestCase(
                    testCase, containsSuspiciousSeed);

            if (nextSeed.isPresent()) {
                final PrimitiveSeed seed = nextSeed.get();
                
                CombinationUtil.add(testCase, seed.getCombination());
                missingPrioritizedSeeds.remove(seed);
                missingUnprioritizedSeeds.remove(seed);
                
                testCaseAddedInLastIteration = true;
                containsSuspiciousSeed = seed.getMode() == SeedMode.EXCLUSIVE;
            } else {
                testCaseAddedInLastIteration = false;
            }
        }
        
        return testCase;
    }
    
    private Optional<PrimitiveSeed> getMostImportantCompatibleTestCase(int[] testCase, boolean containsSuspiciousSeed) {
        for (PrimitiveSeed seed : missingPrioritizedSeeds) {
            if (!(seed.getMode() == SeedMode.EXCLUSIVE && containsSuspiciousSeed)
                    && CombinationUtil.canBeAdded(testCase, seed.getCombination(), constraintChecker)) {
                
                return Optional.of(seed);
            }
        }
        
        for (PrimitiveSeed seed : missingUnprioritizedSeeds) {
            if (!(seed.getMode() == SeedMode.EXCLUSIVE && containsSuspiciousSeed)
                    && CombinationUtil.canBeAdded(testCase, seed.getCombination(), constraintChecker)) {
        
                return Optional.of(seed);
            }
        }
        
        return Optional.empty();
    }
    
}
