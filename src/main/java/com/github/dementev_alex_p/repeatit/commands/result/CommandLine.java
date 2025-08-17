package com.github.dementev_alex_p.repeatit.commands.result;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;

import java.util.List;

public record CommandLine (List<CommandButton> commandButtonList){
    public CommandLine(CommandButton commandButton) {
        this(List.of(commandButton));
    }
    public CommandLine(CommandEnum commandEnum) {
        this(new CommandButton(commandEnum));
    }
}
