package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

import java.util.Arrays;

public class SkipCollectionButton extends CommandButton {
    public static final String TEXT = "Без коллекции";
    public static final String ACTION_VALUE = "skip_collection";
    public SkipCollectionButton(final long cardId) {
        super(
                CommandEnum.EDIT_CARD_COLLECTION,
                TEXT,
                Arrays.asList(
                    CommandParameterUtils.createActionParameter(ACTION_VALUE),
                    CommandParameterUtils.createCardIdParameter(cardId)
                )
        );
    }

}
