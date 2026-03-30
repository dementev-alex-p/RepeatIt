package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.ViewCollectionListCommandHandler;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class PublicCollectionsButton extends CommandButton {
    public PublicCollectionsButton() {
        super(CommandEnum.VIEW_COLLECTION_LIST,
                "Публичные коллекции",
                CommandParameterUtils.createActionParameter(ViewCollectionListCommandHandler.PUBLIC_COLLECTIONS_ACTION),
                CommandParameterUtils.createPageParameter(1)
        );
    }

    public PublicCollectionsButton(final String name) {
        super(CommandEnum.VIEW_COLLECTION_LIST,
                name,
                CommandParameterUtils.createActionParameter(ViewCollectionListCommandHandler.PUBLIC_COLLECTIONS_ACTION),
                CommandParameterUtils.createPageParameter(1)
        );
    }
}
