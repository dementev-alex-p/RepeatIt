package com.github.dementev_alex_p.repeatit.message_context;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.commands.buttons.CreateCardButton;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessage;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MessageContextService {
    private final TgMessageService tgMessageService;

    public MessageContext create(Update update) {

        final boolean isCallback = update.hasCallbackQuery();

        final long userId = isCallback
                ? update.getCallbackQuery().getFrom().getId()
                : update.getMessage().getFrom().getId();

        final String callbackId = isCallback
                ? update.getCallbackQuery().getId()
                : null;

        final Long chatId = isCallback
                ? update.getCallbackQuery().getMessage().getChatId()
                : update.getMessage().getChatId();

        final String userName = isCallback
                ? update.getCallbackQuery().getFrom().getFirstName()
                : update.getMessage().getChat().getFirstName();

        final Optional<String> data = Optional.ofNullable(update.getCallbackQuery()).map(CallbackQuery::getData);

        final Optional<String> message = Optional.ofNullable(update.getMessage()).map(org.telegram.telegrambots.meta.api.objects.Message::getText);
        final Optional<Integer> messageId = Optional.ofNullable(update.getMessage()).map(org.telegram.telegrambots.meta.api.objects.Message::getMessageId);
        final Command command = determinateCommand(data, message, userId);
        return new MessageContext(
                userId,
                userName,
                chatId,
                messageId,
                data,
                message,
                command.commandEnum,
                command.parameters,
                callbackId
        );
    }

    private Command determinateCommand(
            final Optional<String> callbackData,
            final Optional<String> message,
            final long userId
    ) {
        if (callbackData.isPresent()) {
            String[] request = callbackData.get().split("\\?");
            final CommandEnum command = CommandEnum.findCommandByCode(request[0]);
            if (command == CommandEnum.RETURN_BACK) {
                return determinateCommandFromLastMessage(userId);
            }
            final Map<String, String> parameters = request.length > 1
                    ? extractCommandParameters(request[1]) : new HashMap<>();
            return new Command(command, parameters);
        }
        if (message.isPresent()) {
            if (message.get().startsWith("/")) {
                final String command = message.get().substring(1);
                if (command.equals(CommandEnum.MAIN_MENU.getCode())) {
                    return new Command(CommandEnum.MAIN_MENU, new HashMap<>());
                }
                if (command.startsWith(CommandEnum.VIEW_CARD.getCode())) {
                    return new Command(CommandEnum.VIEW_CARD, new HashMap<>());
                }
                if (command.startsWith(CommandEnum.EDIT_CARD_COLLECTION.getCode())) {
                    return new Command(CommandEnum.EDIT_CARD_COLLECTION, new HashMap<>());
                }
            }

            final Optional<TgMessage> lastMessage = tgMessageService.findLastEditableByUserId(userId);
            final boolean isExceptedAnswer = lastMessage.filter(TgMessage::isAnswerExcepted).isPresent();
            if (isExceptedAnswer) {
                return new Command(
                        lastMessage.get().getCommand(),
                        extractCommandParametersFromMessage(lastMessage.get())
                );
            }
            //Если мы не ждали ответа значит это создание новой карточки
            final HashMap<String, String> parameters = new HashMap<>();
            parameters.put(CommandParameterUtils.ACTION_PARAMETER_CODE, CreateCardButton.START_ACTION_CODE);
            return new Command(CommandEnum.CREATE_CARD, parameters);
        }
        throw new IllegalArgumentException("Сообщение пользователя не поддерживается!");
    }

    private Command determinateCommandFromLastMessage(final long userId) {
        final List<TgMessage> lastedOrderedMessages = tgMessageService.findLastedMessagesByUserIdOrderedByCreatedAtDesc(userId, 20);
        if (lastedOrderedMessages.isEmpty()) {
            return new Command(CommandEnum.MAIN_MENU, new HashMap<>());
        }
        final int lastMessageLevel = lastedOrderedMessages.get(0).getCommand().getHierarchyLevel();
        return lastedOrderedMessages
                .stream()
                .filter(message -> message.getCommand().getHierarchyLevel() < lastMessageLevel)
                .findFirst()
                .map(message -> new Command(message.getCommand(), extractCommandParametersFromMessage(message)))
                .orElse(new Command(CommandEnum.MAIN_MENU, new HashMap<>()));
    }

    private Map<String, String> extractCommandParameters(final String request) {
        if (StringUtils.isBlank(request)) {
            return new HashMap<>();
        }
        try {
            return Stream.of(request.split("&"))
                    .map(parameter -> parameter.split("="))
                    .collect(Collectors.toMap(parameter -> parameter[0], parameter -> parameter[1]));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в извлечении параметров запроса");
        }
    }

    private Map<String, String> extractCommandParametersFromMessage(final TgMessage tgMessage) {
        if (tgMessage.getCommandParameters() == null) {
            return new HashMap<>();
        }
        return tgMessage
                .getCommandParameters()
                .stream()
                .collect(Collectors.toMap(CommandParameter::getName, CommandParameter::getValue));
    }
    private record Command(CommandEnum commandEnum, Map<String, String> parameters) { }
}
