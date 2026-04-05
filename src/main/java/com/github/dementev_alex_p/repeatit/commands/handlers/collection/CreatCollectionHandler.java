package com.github.dementev_alex_p.repeatit.commands.handlers.collection;

import com.github.dementev_alex_p.repeatit.cards.collection.CardCollection;
import com.github.dementev_alex_p.repeatit.cards.collection.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreatCollectionHandler implements CommandHandler {

    private static final String TITLE_TEXT = """
            <strong>Создание коллекции</strong>
            —————————————————————
            
            ✍ <i>Введите <strong>название</strong>...</i>
            """;
    private static final String FINISH_CREATION_TEXT = """
            ✅ Коллекция успешно создана!
            """;

    private final CardCollectionService cardCollectionService;
    private final ViewCollectionHandler viewCollectionHandler;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.CREATE_COLLECTION;
    }

    @Override
    public CommandResponse processCommand(final MessageContext context) {
        final boolean isTitleAlreadyEntered = context.message().isPresent();
        if (isTitleAlreadyEntered) {
            return createCollection(context);
        } else {
            return showCreationMessage();
        }
    }

    private CommandResponse createCollection(final MessageContext context) {
        final CardCollection cardCollection = cardCollectionService.createCollection(
                context.userId(),
                context.message().orElseThrow()
        );
        context.commandParameters()
                .put(CommandParameterUtils.COLLECTION_PARAMETER_CODE, String.valueOf(cardCollection.getId()));
        return viewCollectionHandler
                .processCommand(context)
                .withCommand(CommandEnum.VIEW_COLLECTION)
                .withAlter(FINISH_CREATION_TEXT);

    }

    private CommandResponse showCreationMessage() {
        return CommandResponse
                .builder()
                .text(TITLE_TEXT)
                .availableCommands(List.of(new CommandLine(new BackButton(CommandEnum.VIEW_COLLECTION_LIST))))
                .isAnswerExcepted(true)
                .build();
    }
}
