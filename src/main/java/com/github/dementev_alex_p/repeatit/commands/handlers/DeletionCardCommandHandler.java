package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToSend;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class DeletionCardCommandHandler implements CommandHandler {


    private static final String DELETED_TEXT = "Карточка успешно удалена!";
    private final CardService cardService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.DELETE_CARD;
    }

    @Override
    public ProcessingResult processCommand(AbsSender sender, MessageContext context) {
        long cardId = Long.parseLong(context.commandParameters().get("card_id"));
        cardService.softDeleteCardById(cardId);
        return new ProcessingResult(new MessageToSend(
                DELETED_TEXT, new CommandLine(CommandEnum.START)
        ));
    }
}
