package com.github.dementev_alex_p.repeatit.commands.handlers.collection;

import com.github.dementev_alex_p.repeatit.cards.collection.CardCollection;
import com.github.dementev_alex_p.repeatit.cards.collection.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EditCollectionTitleHandler implements CommandHandler {


    private static final String TITLE_EDITION_TEXT = """
            <strong>Коллекция</strong>
            —————————————————————
            Название: <code>%s</code>
            
            ✍ Введите новое название
            """;

    private final CardCollectionService cardCollectionService;
    private final ViewCollectionHandler viewCollectionHandler;


    @Override
    public CommandEnum getCommand() {
        return CommandEnum.EDIT_COLLECTION_TITLE;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        final boolean isNewTitleAlreadyEntered = context.message().isPresent();
        if (isNewTitleAlreadyEntered) {
            return updateTitle(context);
        } else {
            return showEditionMessage(context);
        }
    }

    private CommandResponse showEditionMessage(final MessageContext context) {
        final long collectionId = CommandParameterUtils.extractCollectionId(context);
        final CardCollection collection = cardCollectionService.findById(collectionId);
        final List<CommandLine> commandLines = List.of(new CommandLine(new BackButton(
                CommandEnum.VIEW_COLLECTION,
                CommandParameterUtils.createCollectionIdParameter(collectionId))
        ));
        return CommandResponse
                .builder()
                .text(String.format(TITLE_EDITION_TEXT, collection.getName()))
                .availableCommands(commandLines)
                .isAnswerExcepted(true)
                .build();
    }

    private CommandResponse updateTitle(final MessageContext context) {
        final long collectionId = CommandParameterUtils.extractCollectionId(context);
        cardCollectionService.updateTitleByCollectionId(collectionId, context.message().orElseThrow());
        return viewCollectionHandler
                .processCommand(context)
                .withCommand(CommandEnum.VIEW_COLLECTION);
    }

}