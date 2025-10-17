package com.github.dementev_alex_p.repeatit.message_context;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.user_states.UserState;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MessageContextService {
    private final UserStatesService userStatesService;
    private final static String START_MESSAGE = "/start";

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
        final Command command = determinateCommand(data, message, userId);
        return new MessageContext(userId, userName, chatId, data, message, command.commandEnum, command.parameters);
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
                } else {
                    throw new IllegalArgumentException("Команда не поддерживается!");
                }
            }
            //TODO тут нужно будет узнать последнее сообщение к пользователю и точечно определить команду
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
