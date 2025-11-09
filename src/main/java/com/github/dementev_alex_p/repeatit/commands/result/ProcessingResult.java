package com.github.dementev_alex_p.repeatit.commands.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProcessingResult {
    private final List<MessageToSend> messagesToSend;
    private final List<MessageToEdit> messagesToEdit;
    private final List<Integer> messageIdsToDelete;

    public ProcessingResult(final MessageToSend messageToSend) {
        this.messageIdsToDelete = Collections.emptyList();
        this.messagesToSend = Collections.singletonList(messageToSend);
        this.messagesToEdit = Collections.emptyList();
    }

    public ProcessingResult(final MessageToEdit messageToEdit) {
        this.messageIdsToDelete = Collections.emptyList();
        this.messagesToSend = Collections.emptyList();
        this.messagesToEdit = Collections.singletonList(messageToEdit);
    }

}
