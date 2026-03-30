package com.github.dementev_alex_p.repeatit.commands.result;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class MessageToEdit extends MessageToSend {
    public static final int LAST_MESSAGE = -1;
    private final int messageId;

    public MessageToEdit(int messageId, String text, List<CommandLine> availableCommands, boolean isAnswerExpected, String messageMetaInfo) {
        super(text, availableCommands, isAnswerExpected, messageMetaInfo);
        this.messageId = messageId;
    }

    public MessageToEdit(int messageId, String text, List<CommandLine> availableCommands) {
        super(text, availableCommands);
        this.messageId = messageId;
    }

    public MessageToEdit(int messageId, String text, CommandLine ... availableCommands) {
        super(text, Arrays.asList(availableCommands));
        this.messageId = messageId;
    }

    public MessageToEdit(final int tgMessageId, final MessageToSend message) {
        this(tgMessageId, message.getText(), message.getAvailableCommands(), message.isAnswerExcepted(), message.getMessageMetaInfo());
    }
}
