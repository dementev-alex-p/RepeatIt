package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.commands.result.RIResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ProcessingResult processCommand(MessageContext context) {
        return new ProcessingResult(RIResponse.builder()
                .text(SEARCH_CARD_TEXT)
                .availableCommands(List.of(new CommandLine(CommandEnum.START)))
                .build()
        );
    }
}
