package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.collection.ViewCollectionListHandler;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

import java.util.Arrays;
import java.util.Collections;

public class NextCollectionsButton extends CommandButton {
    public NextCollectionsButton(final int page, final boolean isPublic) {
        super(
                CommandEnum.VIEW_COLLECTION_LIST,
                ">>",
                isPublic ? Arrays.asList(
                        CommandParameterUtils.createPageParameter(page),
                        CommandParameterUtils.createActionParameter(ViewCollectionListHandler.PUBLIC_COLLECTIONS_ACTION)
                ) : Collections.singletonList(CommandParameterUtils.createPageParameter(page))

        );
    }
}
