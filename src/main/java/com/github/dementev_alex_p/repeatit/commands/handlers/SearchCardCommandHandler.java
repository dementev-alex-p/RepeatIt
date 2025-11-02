package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToSend;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Service
public class SearchCardCommandHandler implements CommandHandler {
    private static final String SEARCH_CARD_TEXT = """
            <strong>Поиск карточки</strong>
            Начните вводить содержимое карточки
            """;
    @Override
    public CommandEnum getCommand() {
        return CommandEnum.SEARCH;
    }

    @Override
    public ProcessingResult processCommand(AbsSender sender, MessageContext context) {
        return new ProcessingResult(new MessageToSend(
                SEARCH_CARD_TEXT,
                new CommandLine(CommandEnum.START)
        ));
    }
}
