package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
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
    public void handleCommand(final AbsSender sender, final MessageContext context) throws TelegramApiException {

        final SendMessage sendMessage = SendMessage
                .builder()
                .chatId(context.chatId())
                .text(String.format(GREETING, context.userName()))
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
