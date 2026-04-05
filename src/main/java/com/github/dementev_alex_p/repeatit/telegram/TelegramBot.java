package com.github.dementev_alex_p.repeatit.telegram;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.message_context.MessageContextService;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessage;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String bootName;
    private final MessageContextService messageContextService;
    private final TgMessageService tgMessageService;
    private final TelegramSearchHandler telegramSearchHandler;

    private final Map<CommandEnum, CommandHandler> handlersByCommand;

    public TelegramBot(final TgBotConfig tgBotConfig, final MessageContextService messageContextService, TgMessageService tgMessageService, TelegramSearchHandler telegramSearchHandler, final List<CommandHandler> commandHandlers) {
        super(new DefaultBotOptions(), tgBotConfig.getToken());
        bootName = tgBotConfig.getName();
        this.messageContextService = messageContextService;
        this.tgMessageService = tgMessageService;
        this.telegramSearchHandler = telegramSearchHandler;
        handlersByCommand = commandHandlers
                .stream()
                .collect(Collectors.toMap(
                        CommandHandler::getCommand,
                        Function.identity()
                ));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.ipify.org"))
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("External IP: " + response.body());
    }

    @Override
    public String getBotUsername() {
        return bootName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasInlineQuery()) {
            telegramSearchHandler.search(this, update.getInlineQuery());
            return;
        }
        final MessageContext context = messageContextService.create(update);
        final CommandResponse commandResponse = processRequest(context);
        sendResponse(context, commandResponse);
    }

    private void sendResponse(final MessageContext context, final CommandResponse commandResponse) {
        try {
            if (context.callBackId() != null) {
                answerToCallback(context.callBackId(), commandResponse.getAlter());
            }
            if (context.message().isPresent()) {
                removeUserMessage(context.tgMessageId().orElseThrow(), context.chatId());
            }
            sendMessage(context, commandResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Удаление введенного пользователем сообщения из истории переписки
     * Удаление происходит, если пользователь отправил текстовое сообщение в рамках этой транзакции
     */
    private void removeUserMessage(final int messageId, final long chatId) throws TelegramApiException {
        execute(new DeleteMessage(String.valueOf(chatId), messageId));
    }

    private void answerToCallback(final String callbackId, @Nullable final String alert) throws TelegramApiException {
        execute(AnswerCallbackQuery
                .builder()
                .callbackQueryId(callbackId)
                .text(alert)
                .showAlert(alert != null)
                .build()
        );
    }

    private void deleteMessages(final long chatId, final List<TgMessage> messages) throws TelegramApiException {
        if (messages.isEmpty()) {
            return;
        }
        final List<Integer> messageIdsForDeletion = messages
                .stream()
                .map(TgMessage::getTgMessageId)
                .toList();
        execute(new DeleteMessages(String.valueOf(chatId), messageIdsForDeletion));
        tgMessageService.softDeleteByTgMessageIds(messageIdsForDeletion);
    }


    private CommandResponse processRequest(MessageContext context) {
        try {
            return handlersByCommand.get(context.command()).processCommand(context);
        } catch (Exception e) {
            log.error("ERROR. В момент обработки сообщения произошла ошибка", e);
            return CommandResponse
                    .builder()
                    .text("Произошла ошибка!" + e.getMessage())
                    .availableCommands(List.of(new CommandLine(new CommandButton(CommandEnum.MAIN_MENU))))
                    .build();
        }
    }

    private void sendMessage(final MessageContext context, final CommandResponse response) throws Exception {
        final List<TgMessage> previousMessages = tgMessageService.findNotDeletedMessagesByUserId(context.userId());

        if (response.isChatClearRequired()) {
            sendNewMessage(context, response);
            Thread.sleep(50);
            deleteMessages(context.chatId(), previousMessages);
            return;
        }

        if (previousMessages.isEmpty()) {
            sendNewMessage(context, response);
        } else {
            editMessage(context,response, previousMessages);
        }

    }

    private void sendNewMessage(final MessageContext context, final CommandResponse response) throws Exception {
        if (response.getTrainingStatisticMessage() != null) {
            sendNewMessage(context, response.getTrainingStatisticMessage());
        }
        final ReplyKeyboard inlineKeyboard = createInlineKeyboard(response.getAvailableCommands());

        final SendMessage sendMessage = SendMessage
                .builder()
                .chatId(context.chatId())
                .disableNotification(true)
                .text(response.getText())
                .parseMode("HTML")
                .replyMarkup(inlineKeyboard)
                .build();

        final org.telegram.telegrambots.meta.api.objects.Message sentMessage = execute(sendMessage);
        tgMessageService.create(convertMessage(response, context, sentMessage.getMessageId()));
    }

    private void editMessage(final MessageContext context, final CommandResponse response, final List<TgMessage> previousMessage) throws Exception {
        final List<TgMessage> messagesSortedByCreatedAtDesc = previousMessage
                .stream()
                .sorted(Comparator.comparing(TgMessage::getCreatedAt).reversed())
                .toList();
        final TgMessage lastMessages = messagesSortedByCreatedAtDesc.get(0);

        if (response.getTrainingStatisticMessage() != null) {
            final List<TgMessage> messagesWithoutLast = messagesSortedByCreatedAtDesc
                    .stream()
                    .filter(t -> t.getMessageId() != lastMessages.getMessageId())
                    .toList();

            editMessage(context, response.getTrainingStatisticMessage(), messagesWithoutLast);
        }

        if (response.getText().equals(lastMessages.getMessageText())) {
            //Новое сообщение идентично предыдущему, изменение не требуется
            return;
        }

        final int tgMessageId = lastMessages.getTgMessageId();
        final InlineKeyboardMarkup inlineKeyboard = createInlineKeyboard(response.getAvailableCommands());

        final EditMessageText editMessage = EditMessageText.builder()
                .chatId(context.chatId())
                .messageId(tgMessageId)
                .text(response.getText())
                .parseMode("HTML")
                .replyMarkup(inlineKeyboard)
                .build();

        execute(editMessage);
        tgMessageService.processChangeMessage(
                lastMessages,
                convertMessage(response, context, tgMessageId)
        );
    }

    private TgMessage convertMessage(final CommandResponse response, final MessageContext context, final int tgMessageId) {
        return new TgMessage(
                tgMessageId,
                context.userId(),
                context.chatId(),
                Optional.ofNullable(response.getCommand()).orElse(context.command()),
                response.getText(),
                response.isAnswerExcepted(),
                Optional.ofNullable(response.getParameters()).orElse(CommandParameterUtils.convert(context.commandParameters())),
                response.isChatClearRequired()
        );
    }

    private InlineKeyboardMarkup createInlineKeyboard(final List<CommandLine> availableCommands) {
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
                .map(this::convertButton)
                .toList();
    }

    private InlineKeyboardButton convertButton(CommandButton commandButton) {
        final InlineKeyboardButton.InlineKeyboardButtonBuilder builder = InlineKeyboardButton.builder();
        builder.text(commandButton.getText());

        if (commandButton.getCommand() == CommandEnum.SEARCH) {
            builder.switchInlineQueryCurrentChat("");
        } else {
            builder.callbackData(String.format(
                    "%s?%s",
                    commandButton.getCommand().getCode(),
                    convertParametersToString(commandButton.getParameters())
            ));
        }
        return builder.build();
    }

    private String convertParametersToString(List<CommandParameter> parameters) {
        return parameters
                .stream()
                .map(parameter -> String.format("%s=%s", parameter.getName(), parameter.getValue()))
                .collect(Collectors.joining("&"));
    }

}