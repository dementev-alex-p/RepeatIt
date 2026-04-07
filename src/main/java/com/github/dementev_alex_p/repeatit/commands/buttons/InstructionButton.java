package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.instruction.InstructionEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class InstructionButton extends CommandButton {

    public InstructionButton(InstructionEnum instruction) {
        super(
                CommandEnum.INSTRUCTION,
                instruction.getButtonText(),
                CommandParameterUtils.createAction(instruction.getCode())
        );
    }
}
