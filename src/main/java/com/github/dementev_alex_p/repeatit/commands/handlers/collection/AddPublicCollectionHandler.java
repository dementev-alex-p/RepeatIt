package com.github.dementev_alex_p.repeatit.commands.handlers.collection;

import com.github.dementev_alex_p.repeatit.collections.CardCollection;
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
public class AddPublicCollectionHandler implements CommandHandler {

    private final CardCollectionService cardCollectionService;
    private final ViewCollectionHandler viewCollectionHandler;

    private static final String ALERT_TEXT = """
            ✅ Коллекция добавлена!
            Теперь эта коллекция и ее карточки доступны вам для редактирования и изучения
            """;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.ADD_PUBLIC_COLLECTION;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {

        final long collectionId = CommandParameterUtils.extractCollectionId(context);

        final CardCollection collection = cardCollectionService.findById(collectionId);
        final CardCollection forkedCollection = cardCollectionService.forkCardCollection(collection, context.userId());
        context.commandParameters().put(CommandParameterUtils.COLLECTION_PARAMETER_CODE, String.valueOf(forkedCollection.getId()));

        return viewCollectionHandler
                .processCommand(context)
                .withCommand(CommandEnum.VIEW_COLLECTION)
                .withAlter(ALERT_TEXT);
    }
}
