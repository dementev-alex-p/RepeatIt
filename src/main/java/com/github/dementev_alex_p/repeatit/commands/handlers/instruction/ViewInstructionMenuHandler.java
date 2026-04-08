package com.github.dementev_alex_p.repeatit.commands.handlers.instruction;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ViewInstructionMenuHandler implements CommandHandler {


    private static final String MAIN_TEXT = """
            <strong>RepeatIt - Как это работает?</strong>
            —————————————————————
            
            Выберите интересующую тему:
            """;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.INSTRUCTION_MENU;
    }

    @Override
    public CommandResponse processCommand(final MessageContext context) {
        final List<CommandLine> commandLines = Stream
                .of(InstructionEnum.values())
                .map(e -> new CommandButton(CommandEnum.INSTRUCTION, e.getButtonText(), CommandParameterUtils.createAction(e.getCode())))
                .map(CommandLine::new)
                .collect(Collectors.toList());
        commandLines.add(new CommandLine(new BackButton()));
        return CommandResponse
                .builder()
                .text(MAIN_TEXT)
                .availableCommands(commandLines)
                .build();

    }

}
