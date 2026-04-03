package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import org.springframework.transaction.annotation.Transactional;

public interface CommandHandler {
    CommandEnum getCommand();
    @Transactional
    CommandResponse processCommand(MessageContext context);

}
