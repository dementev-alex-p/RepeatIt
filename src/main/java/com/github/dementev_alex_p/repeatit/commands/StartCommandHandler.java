package com.github.dementev_alex_p.repeatit.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class StartCommandHandler implements CommandHandler {

    private String greeting = """
            %s, приветствую!
            """;
    @Override
    public CommandEnum getCommand() {
        return CommandEnum.START;
    }

    @Override
    public void handleCommand(AbsSender sender, Update update) throws TelegramApiException {
        final Long chatId = update.hasCallbackQuery()
                ? update.getCallbackQuery().getMessage().getChatId()
                : update.getMessage().getChatId();

        final String userName = update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom().getFirstName()
                : update.getMessage().getChat().getFirstName();
        final SendMessage sendMessage = SendMessage
                .builder()
                .chatId(chatId)
                .text(String.format(greeting, userName))
                .replyMarkup(createInlineKeyboard())
                .build();

        sender.execute(sendMessage);
    }

    private ReplyKeyboard createInlineKeyboard() {
        final List<List<InlineKeyboardButton>> buttons = Stream.of(CommandEnum.CREATE_CARD, CommandEnum.VIEW_CARDS, CommandEnum.TRAINING)
                .map(this::createButtonRow)
                .toList();

        return InlineKeyboardMarkup
                .builder()
                .keyboard(buttons)
                .build();

    }
    private List<InlineKeyboardButton> createButtonRow(CommandEnum command) {
        return Collections.singletonList(InlineKeyboardButton
                .builder()
                .text(command.getDescription())
                .callbackData(command.getCode())
                .build()
        );
    }
}
