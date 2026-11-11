package io.github.coffee4j.algorithmic.conflict.explanation;

import io.github.coffee4j.algorithmic.conflict.InternalExplanation;
import io.github.coffee4j.algorithmic.conflict.choco.ChocoModel;

import java.util.Optional;

public interface ConflictExplainer {

    Optional<InternalExplanation> getMinimalConflict(ChocoModel model, int[] background, int[] relaxable);
}
