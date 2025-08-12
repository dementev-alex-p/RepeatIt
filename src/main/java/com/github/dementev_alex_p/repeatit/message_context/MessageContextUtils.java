package com.github.dementev_alex_p.repeatit.message_context;

import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageContextUtils {
    public static MessageContext create(Update update) {
        final Long userId = update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom().getId()
                : update.getMessage().getFrom().getId();

        final Long chatId = update.hasCallbackQuery()
                ? update.getCallbackQuery().getMessage().getChatId()
                : update.getMessage().getChatId();

        final String userName = update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom().getFirstName()
                : update.getMessage().getChat().getFirstName();

        final String data = update.hasCallbackQuery() ? update.getCallbackQuery().getData() : null;

        final String message = update.hasMessage() ? update.getMessage().getText() : null;

        return new MessageContext(userId,userName, chatId, data, message);
    }
}
