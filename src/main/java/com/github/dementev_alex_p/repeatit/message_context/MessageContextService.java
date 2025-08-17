package com.github.dementev_alex_p.repeatit.message_context;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.user_states.UserState;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageContextService {
    private final UserStatesService userStatesService;

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
        final CommandEnum command = determinateCommand(data, message, userId);
        return new MessageContext(userId, userName, chatId, data, message, command);
    }

    private CommandEnum determinateCommand(
            final Optional<String> callbackData,
            final Optional<String> message,
            final long userId
    ) {
        if (callbackData.isPresent()) {
            //callback всегда сопровождается однозначной командой
            final String commandCode = StringUtils.substringBefore(callbackData.get(), "?");
            return CommandEnum.findCommandByCode(commandCode);
        }
        if (message.isPresent()) {
            if (message.get().equals(CommandEnum.START.getCode())) {
                return CommandEnum.START;
            }
            final Optional<UserState> userState = userStatesService.getStateByUserId(userId);
            if (userState.isPresent()) {
                return userState.get().getCurrentCommand();
            }
        }
        throw new IllegalArgumentException("Сообщение пользователя не поддерживается!");
    }
}
