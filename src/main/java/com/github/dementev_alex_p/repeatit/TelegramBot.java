package com.github.dementev_alex_p.repeatit;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.message_context.MessageContextService;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessage;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
    private final CardService cardService;

    private final Map<CommandEnum, CommandHandler> handlersByCommand;

    public TelegramBot(final TgBotConfig tgBotConfig, final MessageContextService messageContextService, TgMessageService tgMessageService, CardService cardService, final List<CommandHandler> commandHandlers) {
        super(new DefaultBotOptions(), tgBotConfig.getToken());
        bootName = tgBotConfig.getName();
        this.messageContextService = messageContextService;
        this.tgMessageService = tgMessageService;
        this.cardService = cardService;
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
        if (update.hasInlineQuery()) {
            processSearchRequest(update.getInlineQuery());
            return;
        }
        final MessageContext context = messageContextService.create(update);
        final ProcessingResult processingResult = processRequest(context);
        replyToUser(context, processingResult);
    }

    private void replyToUser(final MessageContext context, final ProcessingResult processingResult) {
        Optional
                .ofNullable(context.callBackId())
                .ifPresent(callbackId -> answerToCallback(callbackId, processingResult.getResponse()));
        processingResult
                .getMessagesToEdit()
                .forEach(messageToEdit -> editMessage(context, messageToEdit));
        processingResult
                .getMessagesToSend()
                .forEach(messageToSend -> sendMessageToUser(context, messageToSend));
        processingResult
                .getMessageIdsToDelete()
                .forEach(messageIdToDelete -> deleteMessage(context, messageIdToDelete));
        Optional.ofNullable(processingResult.getResponse())
                .ifPresent(response -> sendResponse(context, response));
        removeUserMessageIfRequired(context);
    }

    /**
     * Удаление введенного пользователем сообщения из истории переписки
     * Удаление происходит, если пользователь отправил текстовое сообщение в рамках этой транзакции
     */
    private void removeUserMessageIfRequired(final MessageContext context) {
        context.tgMessageId().ifPresent(messageId ->
                deleteMessage(context, messageId)
        );
    }

    private void answerToCallback(final String callbackId, @Nullable final RIResponse riResponse) {
        final String alert = Optional.ofNullable(riResponse).map(RIResponse::getAlter).orElse(null);
        AnswerCallbackQuery answer = AnswerCallbackQuery
                .builder()
                .callbackQueryId(callbackId)
                .text(alert)
                .showAlert(alert != null)
                .build();
        try {
            execute(answer);
        } catch (final Exception e) {
            log.error(e.getMessage());
        }
    }

    private void deleteMessage(final MessageContext context, final int messageId) {
        try {
            execute(new DeleteMessage(String.valueOf(context.chatId()), messageId));
            tgMessageService.softDeleteMessageByIdIfRequired(messageId);
            Thread.sleep(50);
        } catch (final Exception e) {
            log.error(e.getMessage());
        }
    }

    private void processSearchRequest(final InlineQuery inlineQuery) {
        final long userId = inlineQuery.getFrom().getId();
        final String query = inlineQuery.getQuery();
        final List<Card> cards = cardService.searchCard(userId, query);

        // Конвертируем в формат Telegram
        List<InlineQueryResult> inlineResults = cards
                .stream()
                .map(this::toInlineQueryResult)
                .collect(Collectors.toList());

        final AnswerInlineQuery answer = AnswerInlineQuery
                .builder()
                .inlineQueryId(inlineQuery.getId())
                .results(inlineResults)
                .cacheTime(10)
                .isPersonal(true)
                .build();
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public InlineQueryResult toInlineQueryResult(Card card) {

        final InputTextMessageContent messageContent = InputTextMessageContent.builder()
                .messageText(String.format("/%s %d", CommandEnum.VIEW_CARD.getCode(), card.getId()))
                .build();

        return InlineQueryResultArticle.builder()
                .id(card.getId().toString())
                .title(card.getFrontSide())
                .description(card.getBackSide())
                .inputMessageContent(messageContent)
                .build();
    }

    private ProcessingResult processRequest(MessageContext context) {
        try {
            return handlersByCommand.get(context.command()).processCommand(context);
        } catch (Exception e) {
            log.error("ERROR. Cause: {}", e.getMessage());
            e.printStackTrace();
            return new ProcessingResult(new MessageToSend("Произошла ошибка! " + e.getMessage()));
        }
    }

    private void sendMessageToUser(MessageContext context, MessageToSend messageToSend) {
        final List<CommandLine> availableCommands = messageToSend.getAvailableCommands();
        final ReplyKeyboard inlineKeyboard = createInlineKeyboard(availableCommands);

        final SendMessage sendMessage = SendMessage
                .builder()
                .chatId(context.chatId())
                .text(messageToSend.getText())
                .parseMode("HTML")
                .replyMarkup(inlineKeyboard)
                .build();

        try {
            Message sentMessage = execute(sendMessage);
            tgMessageService.save(convertMessage(messageToSend, context, sentMessage.getMessageId()));
            Thread.sleep(50);

        } catch (Exception e) {
            log.error("ERROR. Cause: {}", e.getMessage());
        }
    }

    private void editMessage(MessageContext context, MessageToEdit messageToEdit) {
        int messageId = messageToEdit.getMessageId();
        if (messageId == MessageToEdit.LAST_MESSAGE) {
            final Optional<TgMessage> lastMessage = tgMessageService.findLastEditableByUserId(context.userId());
            if (lastMessage.isEmpty()) {
                // последнее сообщение недоступно для редактирования, значит отправляем новое
                sendMessageToUser(context, messageToEdit);
                return;
            }
            messageId = lastMessage.get().getTgMessageId();
        }
        final List<CommandLine> availableCommands = messageToEdit.getAvailableCommands();
        final InlineKeyboardMarkup inlineKeyboard = createInlineKeyboard(availableCommands);

        final EditMessageText editMessage = EditMessageText.builder()
                .chatId(context.chatId())
                .messageId(messageId)
                .text(messageToEdit.getText())
                .parseMode("HTML")
                .replyMarkup(inlineKeyboard)
                .build();

        try {
            execute(editMessage);
            tgMessageService.update(editMessage.getMessageId(), editMessage.getText(), context.command(), messageToEdit.isAnswerExcepted(), messageToEdit.getMessageMetaInfo());
            Thread.sleep(50);
        } catch (Exception e) {
            log.error("ERROR. Cause: {}", e.getMessage());
        }
    }

    private void sendResponse(final MessageContext context, final RIResponse response) {
        final Optional<TgMessage> lastEditableMessage = tgMessageService.findLastEditableByUserId(context.userId());

        if (lastEditableMessage.isEmpty()) {
            sendNewMessage(context, response);
        } else {
            editLastMessage(context, response, lastEditableMessage.get());
        }
    }

    private void sendNewMessage(final MessageContext context, final RIResponse response) {
        final ReplyKeyboard inlineKeyboard = createInlineKeyboard(response.getAvailableCommands());

        final SendMessage sendMessage = SendMessage
                .builder()
                .chatId(context.chatId())
                .text(response.getText())
                .parseMode("HTML")
                .replyMarkup(inlineKeyboard)
                .build();

        try {
            Message sentMessage = execute(sendMessage);
            tgMessageService.save(convertMessage(response, context, sentMessage.getMessageId()));
            Thread.sleep(50);

        } catch (Exception e) {
            log.error("ERROR. Cause: {}", e.getMessage());
        }
    }

    private void editLastMessage(final MessageContext context, final RIResponse response, final TgMessage lastMessage) {
        final int tgMessageId = lastMessage.getTgMessageId();
        final InlineKeyboardMarkup inlineKeyboard = createInlineKeyboard(response.getAvailableCommands());

        final EditMessageText editMessage = EditMessageText.builder()
                .chatId(context.chatId())
                .messageId(tgMessageId)
                .text(response.getText())
                .parseMode("HTML")
                .replyMarkup(inlineKeyboard)
                .build();

        try {
            execute(editMessage);
            tgMessageService.update(editMessage.getMessageId(),
                    editMessage.getText(),
                    context.command(),
                    response.isAnswerExcepted(),
                    response.getMessageMetaInfo()
            );
            Thread.sleep(50);
        } catch (Exception e) {
            log.error("ERROR. Cause: {}", e.getMessage());
        }
    }

    private TgMessage convertMessage(final MessageToSend message, final MessageContext context, final int tgMessageId) {
        return new TgMessage(
                tgMessageId,
                context.userId(),
                context.chatId(),
                context.command(),
                message.getText(),
                message.isAnswerExcepted(),
                message.getMessageMetaInfo()

        );
    }

    private TgMessage convertMessage(final RIResponse message, final MessageContext context, final int tgMessageId) {
        return new TgMessage(
                tgMessageId,
                context.userId(),
                context.chatId(),
                context.command(),
                message.getText(),
                message.isAnswerExcepted(),
                message.getMessageMetaInfo()

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