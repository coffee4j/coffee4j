package de.rwth.swc.coffee4j.engine.converter.model;

import de.rwth.swc.coffee4j.algorithmic.model.CompleteTestModel;
import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveSeed;
import de.rwth.swc.coffee4j.algorithmic.model.PrimitiveStrengthGroup;
import de.rwth.swc.coffee4j.algorithmic.model.TupleList;
import de.rwth.swc.coffee4j.algorithmic.util.CombinationUtil;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Seed;
import de.rwth.swc.coffee4j.engine.configuration.model.StrengthGroup;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.configuration.model.constraints.Constraint;
import de.rwth.swc.coffee4j.engine.converter.constraints.IndexBasedConstraintConverter;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.rwth.swc.coffee4j.engine.configuration.model.StrengthGroup.mixedStrengthGroup;

/**
 * A {@link ModelConverter} based on using the indices of supplied {@link Parameter} and {@link Value}. This means
 * that the first parameter in the list of {@link InputParameterModel#getParameters()} is translated to 0, the second
 * one to 1 and so on and so fourth. The same is done with values per parameter.
 * These integers are then used for {@link TupleList} and {@link Combination}, so [0, 1] is a combination
 * where the first parameter is mapped to it's first value and the second one to its second value.
 */
public class IndexBasedModelConverter implements ModelConverter {
    
    private final InputParameterModel model;
    
    private final Object2IntMap<Parameter> parameterToIdMap = new Object2IntOpenHashMap<>();
    private final Map<Parameter, Object2IntMap<Value>> parameterValueToIdMap = new HashMap<>();
    private final Map<Constraint, TupleList> constraintToTuplesListMap = new HashMap<>();
    private final Map<TupleList, Constraint> tuplesListToConstraintMap = new HashMap<>();
    
    private final CompleteTestModel convertedModel;
    
    /**
     * Creates and initializes a new converter with the given testModel and constraints converter.
     *
     * @param model the testModel which is converted. Must not be {@code null}
     */
    public IndexBasedModelConverter(InputParameterModel model) {
        this.model = Preconditions.notNull(model);
        
        initializeConversionMaps();
        convertTuplesLists();
        
        convertedModel = createConvertedModel();
    }
    
    private void initializeConversionMaps() {
        for (int parameterId = 0; parameterId < model.size(); parameterId++) {
            final Parameter correspondingParameter = model.getParameters().get(parameterId);
            final Object2IntMap<Value> valueToIdMap = parameterValueToIdMap.computeIfAbsent(correspondingParameter, parameter -> new Object2IntOpenHashMap<>());
            
            parameterToIdMap.put(correspondingParameter, parameterId);
            for (int valueId = 0; valueId < correspondingParameter.size(); valueId++) {
                valueToIdMap.put(correspondingParameter.getValues().get(valueId), valueId);
            }
        }
    }
    
    private void convertTuplesLists() {
        final List<Constraint> allConstraints = new ArrayList<>(model.getExclusionConstraints());
        allConstraints.addAll(model.getErrorConstraints());
        
        final List<TupleList> correspondingTupleLists = new ArrayList<>();
        int id = 0;

        for (Constraint constraint : allConstraints) {
            IndexBasedConstraintConverter converter = constraint.getConverterFactory().create(model.getParameters());
            correspondingTupleLists.add(converter.convert(constraint, id++));
        }
        
        for (int i = 0; i < allConstraints.size(); i++) {
            final Constraint constraint = allConstraints.get(i);
            final TupleList tupleList = correspondingTupleLists.get(i);
            
            constraintToTuplesListMap.put(constraint, tupleList);
            tuplesListToConstraintMap.put(tupleList, constraint);
        }
    }
    
    private CompleteTestModel createConvertedModel() {
        int[] parameterSizes = IntStream.range(0, model.size())
                .map(parameterId -> model.getParameters().get(parameterId).size())
                .toArray();
        final Int2ObjectMap<List<PrimitiveSeed>> seeds = convertSeeds(model);
        final Int2ObjectMap<List<PrimitiveStrengthGroup>> mixedStrengthGroups = convertMixedStrengthGroups(model);
        
        return CompleteTestModel.builder()
                .positiveTestingStrength(model.getPositiveTestingStrength())
                .negativeTestingStrength(model.getNegativeTestingStrength())
                .parameterSizes(parameterSizes)
                .exclusionTupleLists(model.getExclusionConstraints().stream()
                        .map(constraintToTuplesListMap::get)
                        .collect(Collectors.toSet()))
                .errorTupleLists(model.getErrorConstraints().stream()
                        .map(constraintToTuplesListMap::get)
                        .collect(Collectors.toSet()))
                .weights(convertWeights(model))
                .seeds(seeds)
                .mixedStrengthGroups(mixedStrengthGroups)
                .build();
    }
    
    private Int2ObjectMap<Int2DoubleMap> convertWeights(InputParameterModel model) {
        final Int2ObjectMap<Int2DoubleMap> weights = new Int2ObjectOpenHashMap<>();
        
        for (Parameter parameter : model.getParameters()) {
            final int parameterId = convertParameter(parameter);
            
            for (Value value : parameter.getValues()) {
                if (value.hasWeight()) {
                    final int valueId = convertValue(parameter, value);
                    weights.computeIfAbsent(parameterId, key -> new Int2DoubleOpenHashMap())
                            .put(valueId, value.getRequiredWeight());
                }
            }
        }
        
        return weights;
    }
    
    private Int2ObjectMap<List<PrimitiveSeed>> convertSeeds(InputParameterModel model) {
        final Int2ObjectMap<List<PrimitiveSeed>> seeds = new Int2ObjectOpenHashMap<>();
        
        seeds.put(CompleteTestModel.POSITIVE_TESTS_ID, model.getPositiveSeeds().stream()
                .map(this::convertSeed)
                .collect(Collectors.toList()));
        
        for (Map.Entry<String, List<Seed>> errorConstraintSeeds : model.getNegativeSeeds().entrySet()) {
            final int convertedConstraintId = findErrorConstraintIdByName(errorConstraintSeeds.getKey(), model);
            seeds.put(convertedConstraintId, errorConstraintSeeds.getValue().stream()
                    .map(this::convertSeed)
                    .collect(Collectors.toList()));
        }
        
        return seeds;
    }
    
    private PrimitiveSeed convertSeed(Seed seed) {
        final int[] combination = convertCombination(seed.getCombination());
        
        return new PrimitiveSeed(combination, seed.getMode(), seed.getPriority());
    }
    
    private int findErrorConstraintIdByName(String constraintName, InputParameterModel model) {
        final Constraint referencedErrorConstraint = model.getErrorConstraints().stream()
                .filter(constraint -> constraint.getName().equals(constraintName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Should not be able to reference non existing " + "constraint"));
        return convertConstraint(referencedErrorConstraint).getId();
    }
    
    private Int2ObjectMap<List<PrimitiveStrengthGroup>> convertMixedStrengthGroups(InputParameterModel model) {
        final Int2ObjectMap<List<PrimitiveStrengthGroup>> mixedStrengthGroups = new Int2ObjectOpenHashMap<>();
        
        mixedStrengthGroups.put(CompleteTestModel.POSITIVE_TESTS_ID, model.getPositiveMixedStrengthGroups().stream()
                .map(this::convertMixedStrengthGroup)
                .collect(Collectors.toList()));
        
        for (Constraint errorConstraint : model.getErrorConstraints()) {
            final int constraintId = convertConstraint(errorConstraint).getId();
            final StrengthGroup constraintGroup = mixedStrengthGroup(errorConstraint.getParameterNames())
                    .ofHighestStrength()
                    .build(model.getParameters());
            mixedStrengthGroups.put(constraintId, List.of(convertMixedStrengthGroup(constraintGroup)));
        }
        
        return mixedStrengthGroups;
    }
    
    private PrimitiveStrengthGroup convertMixedStrengthGroup(StrengthGroup mixedStrengthGroup) {
        final IntSet parameterIds = new IntOpenHashSet(mixedStrengthGroup.getParameters().stream()
                .mapToInt(this::convertParameter)
                .toArray());
        
        return PrimitiveStrengthGroup.ofStrength(parameterIds, mixedStrengthGroup.getStrength());
    }
    
    @Override
    public InputParameterModel getModel() {
        return model;
    }
    
    @Override
    public CompleteTestModel getConvertedModel() {
        return convertedModel;
    }
    
    @Override
    public int[] convertCombination(Combination combination) {
        Preconditions.notNull(combination);
        
        int[] combinationArray = CombinationUtil.emptyCombination(model.size());
        
        for (Map.Entry<Parameter, Value> mapping : combination.getParameterValueMap().entrySet()) {
            final int parameterId = parameterToIdMap.getInt(mapping.getKey());
            final int valueId = parameterValueToIdMap.get(mapping.getKey()).getInt(mapping.getValue());
            combinationArray[parameterId] = valueId;
        }
        
        return combinationArray;
    }
    
    @Override
    public Combination convertCombination(int[] combination) {
        Preconditions.notNull(combination);
        Preconditions.check(combination.length == model.size());
        
        final Map<Parameter, Value> parameterValueMap = new HashMap<>();
        
        for (int parameterId = 0; parameterId < model.size(); parameterId++) {
            if (combination[parameterId] != CombinationUtil.NO_VALUE) {
                final Parameter parameter = model.getParameters().get(parameterId);
                final Value correspondingValue = parameter.getValues().get(combination[parameterId]);
                
                parameterValueMap.put(parameter, correspondingValue);
            }
        }
        
        return Combination.of(parameterValueMap);
    }
    
    @Override
    public int convertParameter(Parameter parameter) {
        Preconditions.notNull(parameter);
        
        return parameterToIdMap.getInt(parameter);
    }
    
    @Override
    public Parameter convertParameter(int parameter) {
        Preconditions.check(parameter >= 0 && parameter < model.size());
        
        return model.getParameters().get(parameter);
    }
    
    @Override
    public int convertValue(Parameter parameter, Value value) {
        Preconditions.notNull(parameter);
        Preconditions.notNull(value);
        
        return parameterValueToIdMap.get(parameter).getInt(value);
    }
    
    @Override
    public Value convertValue(int parameter, int value) {
        Preconditions.check(parameter >= 0);
        Preconditions.check(value >= 0);
        
        return model.getParameters().get(parameter).getValues().get(value);
    }
    
    @Override
    public TupleList convertConstraint(Constraint constraint) {
        Preconditions.notNull(constraint);
        
        return constraintToTuplesListMap.get(constraint);
    }
    
    @Override
    public Constraint convertConstraint(TupleList constraint) {
        Preconditions.notNull(constraint);
        
        return tuplesListToConstraintMap.get(constraint);
    }
}
