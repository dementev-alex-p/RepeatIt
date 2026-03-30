package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;

public class BackButton extends CommandButton {
    public static final String TEXT = "↩ Вернуться назад";
    public BackButton(final CommandEnum command) {
        super(
                command,
                TEXT,
                command.getDefaultParameter()
        );
    }

    public BackButton(final CommandEnum command, final CommandParameter ... parameters) {
        super(
                command,
                TEXT,
                parameters
        );
    }

}
