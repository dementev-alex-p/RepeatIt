package com.github.dementev_alex_p.repeatit.message_context;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;

import java.util.Map;
import java.util.Optional;

public record MessageContext(long userId, String userName, long chatId, Optional<String> data, Optional<String> message,
                             CommandEnum command, Map<String, String> commandParameters) {
}
