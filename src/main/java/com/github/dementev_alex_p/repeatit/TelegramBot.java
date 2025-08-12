package com.github.dementev_alex_p.repeatit;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandHandler;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.message_context.MessageContextUtils;
import com.github.dementev_alex_p.repeatit.user_states.UserState;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final String bootName;
    private final UserStatesService userStatesService;

    private final Map<CommandEnum, CommandHandler> handlersByCommand;

    public TelegramBot(final TgBotConfig tgBotConfig, final UserStatesService userStatesService, final List<CommandHandler> commandHandlers) {
        super(new DefaultBotOptions(), tgBotConfig.getToken());
        bootName = tgBotConfig.getName();
        this.userStatesService = userStatesService;
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
        try {
            final MessageContext context = MessageContextUtils.create(update);

            if (isStart(update)) {
                handlersByCommand.get(CommandEnum.START).handleCommand(this, context);
            }

            if(update.hasCallbackQuery()) {
                final String commandCode = StringUtils.substringBefore(update.getCallbackQuery().getData(), "?");
                CommandEnum command = CommandEnum.findCommandByCode(commandCode);
                handlersByCommand.get(command).handleCommand(this, context);
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                final Long userId = update.getMessage().getFrom().getId();
                final UserState userState = userStatesService.getStateByUserId(userId);
                if (userState != null) {
                    handlersByCommand.get(userState.getCurrentState()).handleCommand(this, context);
                }
            }
        //TODO Вернуть ошибку
        } catch (TelegramApiException e) {
            System.out.println("ERROR. Command not handled. Cause: " + e.getMessage());
        }

    }

    private boolean isStart(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().equals(CommandEnum.START.getCode());
    }
}