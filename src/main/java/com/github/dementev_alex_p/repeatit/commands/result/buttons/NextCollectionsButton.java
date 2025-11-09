package com.github.dementev_alex_p.repeatit.commands.result.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;

public class NextCollectionsButton extends CommandButton {
    public NextCollectionsButton(int nextPageNumber) {
        super(
                CommandEnum.COLLECTIONS,
                ">>",
                CommandButtonUtils.createPageParameter(nextPageNumber)
        );
    }
}
