package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.RecallScoreEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

import java.util.Arrays;

public class ScoreButton extends CommandButton {
    public ScoreButton(final RecallScoreEnum score, final long cardId) {
        super(CommandEnum.TRAINING,
                score.getText(),
                Arrays.asList(
                        new CommandParameter("score", score.name()),
                        CommandParameterUtils.createCardIdParameter(cardId)
                ));
    }
}
