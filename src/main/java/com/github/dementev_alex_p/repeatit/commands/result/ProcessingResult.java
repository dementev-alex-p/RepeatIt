package com.github.dementev_alex_p.repeatit.commands.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class ProcessingResult {
    private final List<MessageToSend> messagesToSend;
    private final List<MessageToEdit> messagesToEdit;
    private final List<Integer> messageIdsToDelete;
    private final RIResponse response;
    public ProcessingResult(final MessageToSend messageToSend) {
        this.messageIdsToDelete = Collections.emptyList();
        this.messagesToSend = Collections.singletonList(messageToSend);
        this.messagesToEdit = Collections.emptyList();
        this.response = null;
    }

    public ProcessingResult(final MessageToEdit messageToEdit) {
        this.messageIdsToDelete = Collections.emptyList();
        this.messagesToSend = Collections.emptyList();
        this.messagesToEdit = Collections.singletonList(messageToEdit);
        this.response = null;
    }

    public ProcessingResult(final RIResponse response) {
        this.messageIdsToDelete = Collections.emptyList();
        this.messagesToSend = Collections.emptyList();
        this.messagesToEdit = Collections.emptyList();
        this.response = response;
    }

    public ProcessingResult(final List<MessageToSend> messagesToSend, final List<MessageToEdit> messagesToEdit, final List<Integer> messageIdsToDelete ) {
        this.messageIdsToDelete = messageIdsToDelete;
        this.messagesToSend = messagesToSend;
        this.messagesToEdit = messagesToEdit;
        this.response = null;
    }

}
