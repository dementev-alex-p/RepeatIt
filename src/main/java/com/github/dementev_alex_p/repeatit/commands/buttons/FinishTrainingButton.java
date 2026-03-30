package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class FinishTrainingButton extends CommandButton {
    public FinishTrainingButton() {
        super(CommandEnum.TRAINING,
                "Завершить тренировку",
                CommandParameterUtils.createActionParameter("end"));
    }
}
