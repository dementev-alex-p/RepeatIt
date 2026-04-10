package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

import java.util.List;
import java.util.stream.Stream;

public class HintButton extends CommandButton {

    public HintButton(CommandEnum command, List<CommandParameter> parameters) {
        super(
                command,
                "💡 Подсказка",
                Stream.concat(
                        parameters.stream(),
                        Stream.of(CommandParameterUtils.createHintActionParameter())
                ).toList()
        );
    }
}
