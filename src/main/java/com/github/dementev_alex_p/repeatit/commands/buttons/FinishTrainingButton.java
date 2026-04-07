package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class FinishTrainingButton extends CommandButton {
    public static final String ACTION_CODE = "finish";
    public FinishTrainingButton() {
        super(CommandEnum.TRAINING,
                "Завершить тренировку",
                CommandParameterUtils.createAction(ACTION_CODE));
    }
}
