package com.github.dementev_alex_p.repeatit.commands.result.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class CommandButton {
    private final CommandEnum command;
    private final String text;
    private final List<CommandParameter> parameters;

    public CommandButton(CommandEnum command){
        this(
                command,
                command.getDescription(),
                command.getDefaultParameter() == null
                        ? Collections.emptyList()
                        : Collections.singletonList(command.getDefaultParameter())
        );
    }

    public CommandButton(CommandEnum command, String buttonText, CommandParameter parameter) {
        this(
                command,
                buttonText,
                parameter == null
                        ? Collections.emptyList()
                        : Collections.singletonList(parameter)
        );
    }

    public CommandButton(CommandEnum command, String buttonText, CommandParameter ... parameters) {
        this(command, buttonText, Arrays.asList(parameters));
    }
}
