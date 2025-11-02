package com.github.dementev_alex_p.repeatit.message_context;

import com.github.dementev_alex_p.repeatit.commands.handlers.EditionCardCommandHandler;
import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessage;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MessageContextService {
    private final TgMessageService tgMessageService;
    private static final String START_MESSAGE = "/start";
    private static final String EDIT_MESSAGE = "/edit_card";

    public MessageContext create(Update update) {

        final boolean isCallback = update.hasCallbackQuery();

        final long userId = isCallback
                ? update.getCallbackQuery().getFrom().getId()
                : update.getMessage().getFrom().getId();

        final Long chatId = isCallback
                ? update.getCallbackQuery().getMessage().getChatId()
                : update.getMessage().getChatId();

        final String userName = isCallback
                ? update.getCallbackQuery().getFrom().getFirstName()
                : update.getMessage().getChat().getFirstName();

        final Optional<String> data = Optional.ofNullable(update.getCallbackQuery()).map(CallbackQuery::getData);

        final Optional<String> message = Optional.ofNullable(update.getMessage()).map(Message::getText);
        final Optional<Integer> messageId = Optional.ofNullable(update.getMessage()).map(Message::getMessageId);
        final Command command = determinateCommand(data, message, userId);
        return new MessageContext(userId, userName, chatId, messageId, data, message, command.commandEnum, command.parameters);
    }

    private Command determinateCommand(
            final Optional<String> callbackData,
            final Optional<String> message,
            final long userId
    ) {
        if (callbackData.isPresent()) {
            String[] request = callbackData.get().split("\\?");
            final CommandEnum command = CommandEnum.findCommandByCode(request[0]);
            final Map<String, String> parameters = request.length > 1
                    ? extractCommandParameters(request[1]) : new HashMap<>();
            return new Command(command, parameters);
        }
        if (message.isPresent()) {
            if (message.get().startsWith("/")) {
                if (message.get().equals(START_MESSAGE)) {
                    return new Command(CommandEnum.START, new HashMap<>());
                } else if (message.get().startsWith(EDIT_MESSAGE)) {
                    final long cardId = Long.parseLong(message.get().substring(EDIT_MESSAGE.length()).trim());
                    final CommandParameter cardIdParameter = CommandButtonUtils.createCardIdParameter(cardId);
                    final CommandParameter actionParameter = CommandButtonUtils.createActionParameter(
                            EditionCardCommandHandler.START_EDITION_ACTION
                    );

                    return new Command(
                            CommandEnum.EDIT_CARD,
                            Map.of(
                                    cardIdParameter.getName(), cardIdParameter.getValue(),
                                    actionParameter.getName(), actionParameter.getValue()
                            )
                    );
                } else {
                        throw new IllegalArgumentException("Команда не поддерживается!");
                }
            }
            final TgMessage lastMessage = tgMessageService.findLastByUserId(userId);
            if (lastMessage.isAnswerExcepted()) {
                return new Command(lastMessage.getCommand(), new HashMap<>());
            }
            return new Command(CommandEnum.CREATE_CARD, new HashMap<>());
        }
        throw new IllegalArgumentException("Сообщение пользователя не поддерживается!");
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

    private record Command(CommandEnum commandEnum, Map<String, String> parameters) {}
}
