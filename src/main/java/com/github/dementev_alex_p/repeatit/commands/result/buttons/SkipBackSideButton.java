package com.github.dementev_alex_p.repeatit.commands.result.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;

import java.util.Arrays;

public class SkipBackSideButton extends CommandButton {
    public static final String TEXT = "Содержание не требуется";
    public static final String ACTION_VALUE = "skip_back_side";
    public SkipBackSideButton(final CommandEnum command, final long cardId) {
        super(
                command,
                TEXT,
                Arrays.asList(
                    CommandButtonUtils.createActionParameter(ACTION_VALUE),
                    CommandButtonUtils.createCardIdParameter(cardId)
                )
        );
    }

}
