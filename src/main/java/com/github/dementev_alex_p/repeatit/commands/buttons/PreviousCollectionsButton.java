package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.ViewCollectionListCommandHandler;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

import java.util.Arrays;
import java.util.Collections;

public class PreviousCollectionsButton extends CommandButton {
    public PreviousCollectionsButton(final int page, final boolean isPublic) {
        super(
                CommandEnum.VIEW_COLLECTION_LIST,
                "<<",
                isPublic ? Arrays.asList(
                        CommandParameterUtils.createPageParameter(page),
                        CommandParameterUtils.createActionParameter(ViewCollectionListCommandHandler.PUBLIC_COLLECTIONS_ACTION)
                ) : Collections.singletonList(CommandParameterUtils.createPageParameter(page))

        );
    }
}
