package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.collection.ViewCollectionListHandler;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

import java.util.Arrays;
import java.util.Collections;

public class PreviousCollectionsButton extends CommandButton {
    public PreviousCollectionsButton(final int page, final CommandEnum command) {
        super(
                command,
                "<<",
                Collections.singletonList(CommandParameterUtils.createPageParameter(page))

        );
    }
}
