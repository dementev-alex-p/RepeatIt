package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.collection.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.RIResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeletionCollectionCommandHandler implements CommandHandler {


    private static final String DELETED_TEXT = "Коллекция успешно удалена!";
    private final CardCollectionService cardCollectionService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.DELETE_COLLECTION;
    }

    @Override
    public ProcessingResult processCommand(MessageContext context) {
        final long collectionId = CommandParameterUtils.extractCollectionId(context);
        cardCollectionService.softDeleteById(collectionId);
        return new ProcessingResult(RIResponse.builder()
                .text(DELETED_TEXT)
                .availableCommands(List.of(new CommandLine(new CommandButton(CommandEnum.VIEW_COLLECTION_LIST))))
                .build()
        );
    }
}
