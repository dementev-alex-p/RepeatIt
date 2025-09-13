package com.github.dementev_alex_p.repeatit;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.message_context.MessageContextService;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final String bootName;
    private final MessageContextService messageContextService;

    private final Map<CommandEnum, CommandHandler> handlersByCommand;

    public TelegramBot(final TgBotConfig tgBotConfig, final MessageContextService messageContextService, final List<CommandHandler> commandHandlers) {
        super(new DefaultBotOptions(), tgBotConfig.getToken());
        bootName = tgBotConfig.getName();
        this.messageContextService = messageContextService;
        handlersByCommand = commandHandlers
                .stream()
                .collect(Collectors.toMap(
                        CommandHandler::getCommand,
                        Function.identity()
                ));
    }

    @Override
    public String getBotUsername() {
        return bootName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        final MessageContext context = messageContextService.create(update);
        final CommandProcessingResult processingResult = processRequest(context);
        sendUserResponse(context, processingResult);
    }

    private CommandProcessingResult processRequest(MessageContext context) {
        try {
            return handlersByCommand.get(context.command()).processCommand(this, context);
        } catch (Exception e) {
            System.out.println("ERROR. Cause: " + e.getMessage());
            return new CommandProcessingResult("Произошла ошибка! " + e.getMessage());
        }
    }

    private void sendUserResponse(MessageContext context, CommandProcessingResult processingResult) {
        final List<CommandLine> availableCommands = processingResult.availableCommands();
        final ReplyKeyboard inlineKeyboard = createInlineKeyboard(
                enrichAvailableCommandsWithDefault(availableCommands)
        );

        final SendMessage sendMessage = SendMessage
                .builder()
                .chatId(context.chatId())
                .text(processingResult.message())
                .parseMode("HTML")
                .replyMarkup(inlineKeyboard)
                .build();

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("ERROR. Cause: " + e.getMessage());
        }
    }

    private LinkedHashSet<CommandLine> enrichAvailableCommandsWithDefault(final List<CommandLine> availableCommands) {
        final LinkedHashSet<CommandLine> commands = new LinkedHashSet<>(availableCommands);
        commands.add(new CommandLine(new CommandButton(CommandEnum.START)));
        return commands;
    }

    private ReplyKeyboard createInlineKeyboard(final LinkedHashSet<CommandLine> availableCommands) {
        final List<List<InlineKeyboardButton>> buttons = availableCommands
                .stream()
                .map(this::createButtonRow)
                .toList();

        return InlineKeyboardMarkup
                .builder()
                .keyboard(buttons)
                .build();

    }

    private List<InlineKeyboardButton> createButtonRow(CommandLine commandLine) {
        return commandLine
                .commandButtonList()
                .stream()
                .map(commandButton -> InlineKeyboardButton
                        .builder()
                        .text(commandButton.text())
                        .callbackData(String.format("%s?%s", commandButton.command().getCode(), commandButton.parameter()))
                        .build()
                ).toList();
    }

}