package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.commands.result.CommandProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface CommandHandler {
    public CommandEnum getCommand();
    public CommandProcessingResult processCommand(AbsSender sender, MessageContext context) throws TelegramApiException;

}
