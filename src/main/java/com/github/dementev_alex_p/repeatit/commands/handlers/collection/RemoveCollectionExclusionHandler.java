package com.github.dementev_alex_p.repeatit.commands.handlers.collection;

import com.github.dementev_alex_p.repeatit.collections.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoveCollectionExclusionHandler implements CommandHandler {


    private static final String REMOVE_EXCLUSION_TEXT = "Исключение снято! Карточки коллекции снова начнут появляться в тренировках";
    private final CardCollectionService cardCollectionService;
    private final ViewCollectionHandler viewCollectionHandler;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.REMOVE_COLLECTION_EXCLUSION;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        long collectionId = CommandParameterUtils.extractCollectionId(context);
        cardCollectionService.removeExclusion(collectionId);
        final CommandResponse commandResponse = viewCollectionHandler.processCommand(context);
        return commandResponse
                .withAlter(REMOVE_EXCLUSION_TEXT)
                .withCommand(CommandEnum.VIEW_COLLECTION);
    }

}