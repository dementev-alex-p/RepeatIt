package com.github.dementev_alex_p.repeatit.commands.result;

import lombok.Getter;

import java.util.List;

@Getter
public class MessageToEdit extends MessageToSend {
    private final int messageId;

    public MessageToEdit(int messageId, String text, List<CommandLine> availableCommands, boolean isAnswerExcepted) {
        super(text, availableCommands, isAnswerExcepted);
        this.messageId = messageId;
    }
}
