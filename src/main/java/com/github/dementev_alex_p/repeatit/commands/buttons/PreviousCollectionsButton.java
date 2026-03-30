package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.CollectionsCommandHandler;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

import java.util.Arrays;
import java.util.Collections;

public class PreviousCollectionsButton extends CommandButton {
    public PreviousCollectionsButton(final int page, final boolean isPublic) {
        super(
                CommandEnum.COLLECTIONS,
                "<<",
                isPublic ? Arrays.asList(
                        CommandParameterUtils.createPageParameter(page),
                        CommandParameterUtils.createActionParameter(CollectionsCommandHandler.PUBLIC_COLLECTIONS_ACTION)
                ) : Collections.singletonList(CommandParameterUtils.createPageParameter(page))

        );
    }
}
