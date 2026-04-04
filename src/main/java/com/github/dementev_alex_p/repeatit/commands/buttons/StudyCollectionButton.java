package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.TrainingCommandHandler;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;


public class StudyCollectionButton extends CommandButton {
    public StudyCollectionButton(final long collectionId) {
        super(
                CommandEnum.TRAINING,
                "\uD83C\uDFC6 Изучать",
                CommandParameterUtils.createCollectionIdParameter(collectionId),
                CommandParameterUtils.createActionParameter(TrainingCommandHandler.START_ACTION_CODE)
        );
    }

}
