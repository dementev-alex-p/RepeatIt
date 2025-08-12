package com.github.dementev_alex_p.repeatit.message_context;

public record MessageContext (long userId, String userName, long chatId, String data, String message) {
}
