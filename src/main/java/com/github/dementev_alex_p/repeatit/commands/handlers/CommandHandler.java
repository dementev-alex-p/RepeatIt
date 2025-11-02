package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface CommandHandler {
    CommandEnum getCommand();
    ProcessingResult processCommand(AbsSender sender, MessageContext context);

}
