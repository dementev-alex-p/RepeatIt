package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.commands.result.CommandProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class AdditionCardCommandHandler implements CommandHandler {
    @Override
    public CommandEnum getCommand() {
        return CommandEnum.ADD_CARD;
    }

    @Override
    public CommandProcessingResult processCommand(AbsSender sender, MessageContext context) throws TelegramApiException {
        return CommandProcessingResult.createWithVerticalButtons(
                "Отлично! Вы можете создать новую карточку, добавить набор карточек из публичных коллекций или импортировать карточки",
                CommandEnum.CREATE_CARD, CommandEnum.ADD_CARDS_FROM_COLLECTION, CommandEnum.IMPORT_CARDS
        );
    }
}
