package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.CollectionsCommandHandler;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class PublicCollectionsButton extends CommandButton {
    public PublicCollectionsButton() {
        super(CommandEnum.COLLECTIONS,
                "Публичные коллекции",
                CommandParameterUtils.createActionParameter(CollectionsCommandHandler.PUBLIC_COLLECTIONS_ACTION),
                CommandParameterUtils.createPageParameter(1)
        );
    }

    public PublicCollectionsButton(final String name) {
        super(CommandEnum.COLLECTIONS,
                name,
                CommandParameterUtils.createActionParameter(CollectionsCommandHandler.PUBLIC_COLLECTIONS_ACTION),
                CommandParameterUtils.createPageParameter(1)
        );
    }
}
