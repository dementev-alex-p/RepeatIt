package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class StartTrainingButton extends CommandButton {
    public static final String ACTION_CODE = "start";
    public StartTrainingButton() {
        super(CommandEnum.TRAINING,
                CommandEnum.TRAINING.getDescription(),
                CommandParameterUtils.createActionParameter(ACTION_CODE));
    }
}
