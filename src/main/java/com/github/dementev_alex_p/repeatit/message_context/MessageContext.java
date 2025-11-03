package com.github.dementev_alex_p.repeatit.message_context;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Optional;

public record MessageContext(
        long userId, String userName, long chatId, Optional<Integer> tgMessageId, Optional<String> data, Optional<String> message,
        CommandEnum command, @NotNull Map<String, String> commandParameters, String callBackId
) {
}
