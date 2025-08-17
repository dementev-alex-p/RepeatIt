package com.github.dementev_alex_p.repeatit.commands.result;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;

public record CommandButton (CommandEnum command, String text, String parameter){
    public CommandButton (CommandEnum command){
        this(command, command.getDescription(), "");
    }
}
