package de.rwth.swc.coffee4j.engine.process.report.util;

import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.Parameter;
import de.rwth.swc.coffee4j.engine.configuration.model.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Used to format a found exception-inducing combination as java-code.
 */
public class JavaFormatter implements CombinationFormatter {
    @Override
    public String format(Combination combination) {
        StringBuilder builder = new StringBuilder();
        List<Parameter> parameters = new ArrayList<>();
        List<Value> values = new ArrayList<>();

        // ensure that used parameter-value pairs always have the same order
        for (Map.Entry<Parameter, Value> entry : combination.getParameterValueMap().entrySet()) {
            parameters.add(entry.getKey());
            values.add(entry.getValue());
        }

        Parameter firstParameter = parameters.remove(0);
        Value firstValue = values.remove(0);

        builder.append("constrain(\"").append(firstParameter.getName()).append("\"");
        parameters.forEach(parameter -> builder.append(", \"").append(parameter.getName()).append("\""));
        builder.append(").by((Object ").append(firstParameter.getName().toLowerCase().replace(" ", ""));
        parameters.forEach(parameter -> builder.append(", Object ").append(parameter.getName().toLowerCase().replace(" ", "")));
        builder.append(") -> !(").append(firstParameter.getName().toLowerCase().replace(" ", "")).append(".equals(").append(firstValue.get() instanceof String ? "\"" + firstValue.get() + "\"" : firstValue.get()).append(")");

        for (int i = 0; i < parameters.size(); i++) {
            builder.append(" && ").append(parameters.get(i).getName().toLowerCase().replace(" ", "")).append(".equals(").append(values.get(i).get() instanceof String ? "\"" + values.get(i).get() + "\"" : values.get(i).get()).append(")");
        }

        builder.append("))");

        return builder.toString();
    }
}
