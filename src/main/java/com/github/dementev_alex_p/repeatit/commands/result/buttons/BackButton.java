package com.github.dementev_alex_p.repeatit.commands.result.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;

public class BackButton extends CommandButton {
    public static final String TEXT = "↩ Вернуться назад";
    public BackButton(final CommandEnum command) {
        super(
                command,
                TEXT
        );
    }

}
