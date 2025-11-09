package com.github.dementev_alex_p.repeatit.commands.result.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;

public class PreviousCollectionsButton extends CommandButton {
    public PreviousCollectionsButton(final int page) {
        super(
                CommandEnum.COLLECTIONS,
                "<<",
                CommandButtonUtils.createPageParameter(page)
        );
    }
}
