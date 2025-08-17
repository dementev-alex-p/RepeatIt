package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.commands.result.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.stream.Stream;

@Component
public class StartCommandHandler implements CommandHandler {

    private static final String GREETING = """
            %s, приветствую!
            """;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.START;
    }

    @Override
    public CommandProcessingResult processCommand(final AbsSender sender, final MessageContext context) throws TelegramApiException {
        return new CommandProcessingResult(
                String.format(GREETING, context.userName()),
                Stream.of(CommandEnum.CREATE_CARD, CommandEnum.VIEW_CARDS, CommandEnum.START_TRAINING)
                        .map(CommandLine::new)
                        .toList()
        );
    }


}
