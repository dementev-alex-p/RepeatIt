package com.github.dementev_alex_p.repeatit.commands.result;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;

import java.util.List;

public record CommandLine (List<CommandButton> commandButtonList){
    public CommandLine(final CommandButton ... commandButtons) {
        this(List.of(commandButtons));
    }
    public CommandLine(CommandEnum commandEnum) {
        this(new CommandButton(commandEnum));
    }
}
