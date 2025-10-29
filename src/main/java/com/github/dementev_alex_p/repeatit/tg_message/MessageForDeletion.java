package com.github.dementev_alex_p.repeatit.tg_message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageForDeletion {
    private long userId;
    private long chatId;
    private int messageId;
}
