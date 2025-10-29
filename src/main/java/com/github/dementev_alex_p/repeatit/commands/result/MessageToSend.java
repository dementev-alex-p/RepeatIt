package com.github.dementev_alex_p.repeatit.commands.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class MessageToSend {
    private final String text;
    private final List<CommandLine> availableCommands;
    private final boolean isAnswerExcepted;

    public MessageToSend(final String text) {
        this.text = text;
        this.availableCommands = Collections.emptyList();
        this.isAnswerExcepted = false;
    }

    public MessageToSend(final String text, final List<CommandLine> availableCommands) {
        this.text = text;
        this.availableCommands = availableCommands;
        this.isAnswerExcepted = false;
    }

    public MessageToSend(final String text, final CommandLine... commandLines) {
        this.text = text;
        this.availableCommands = Arrays.asList(commandLines);
        this.isAnswerExcepted = false;
    }
}
