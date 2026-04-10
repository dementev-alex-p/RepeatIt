package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.HintButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public interface CommandHandler {
    CommandEnum getCommand();
    @Transactional
    CommandResponse processCommand(MessageContext context);

    default List<CommandLine> addHintButtonIfRequired(final MessageContext context, final List<CommandLine> commandLines) {
        if (CommandParameterUtils.isViewHintRequired(context)) {
            return commandLines;
        }
        final List<CommandLine> commands = new ArrayList<>();
        commands.add(new CommandLine(new HintButton(getCommand(), CommandParameterUtils.convert(context.commandParameters()))));
        commands.addAll(commandLines);
        return commands;
    }

}
