package io.github.coffee4j.junit.engine.it;

import io.github.coffee4j.engine.configuration.model.InputParameterModel;

import static io.github.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static io.github.coffee4j.engine.configuration.model.Parameter.parameter;

class RequirementsModel {

    static final InputParameterModel BROWSER_IPM = inputParameterModel("model name")
            .parameters(
                    parameter("OS").values("Windows", "Linux", "MacOS"),
                    parameter("Language").values(Language.de_DE, Language.en_US, Language.fr_BE),
                    parameter("Browser").values("Chrome", "Edge", "Safari", "Opera")
            )
            .build();

    enum Language {
        de_DE,
        en_US,
        fr_BE
    }

}
