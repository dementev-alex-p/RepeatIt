package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

import java.util.Arrays;

public class SkipBackSideButton extends CommandButton {
    public static final String TEXT = "Содержание не требуется";
    public static final String ACTION_VALUE = "skip_back_side";
    public SkipBackSideButton(final long cardId) {
        super(
                CommandEnum.CREATE_CARD,
                TEXT,
                Arrays.asList(
                    CommandParameterUtils.createAction(ACTION_VALUE),
                    CommandParameterUtils.createCardIdParameter(cardId)
                )
        );
    }

}
