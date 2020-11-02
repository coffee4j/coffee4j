package de.rwth.swc.coffee4j.algorithmic.conflict.explanation;

import de.rwth.swc.coffee4j.algorithmic.conflict.InternalExplanation;
import de.rwth.swc.coffee4j.algorithmic.conflict.choco.ChocoModel;

import java.util.Optional;

public class NoConflictExplainer implements ConflictExplainer {

    @Override
    public Optional<InternalExplanation> getMinimalConflict(ChocoModel model,
                                                            int[] background,
                                                            int[] relaxable) {
        return Optional.empty();
    }
}
