package com.github.dementev_alex_p.repeatit.commands.handlers.collection;

import com.github.dementev_alex_p.repeatit.collections.CardCollection;
import com.github.dementev_alex_p.repeatit.collections.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.ExcludeCollectionFromTrainingButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class DeleteCollectionHandler implements CommandHandler {

    private static final String CONFIRM_TEXT = """
            <strong>Удаление коллекции</strong>
            —————————————————————
            Название: %s
            
            ⚠ Вы уверены, что хотите удалить коллекцию?
            Вместе с коллекцией так же удалятся все ее карточки и прогресс по ним.
            Если вы хотите временно приостановить изучение коллекции, то воспользуйтесь "Исключить из тренировок"
            """;
    private static final String CONFIRMED_DELETION_ACTION = "confirmed_deletion";
    private static final String DELETION_TEXT = "Коллекция успешно удалена!";
    private final CardCollectionService cardCollectionService;
    private final ViewCollectionListHandler viewCollectionListHandler;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.DELETE_COLLECTION;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        if (isDeletionConfirmed(context)) {
            return deleteCollection(context);
        }
        return sendConfirmation(context);
    }

    private CommandResponse sendConfirmation(final MessageContext context) {
        final long collectionId = CommandParameterUtils.extractCollectionId(context);
        final CardCollection collection = cardCollectionService.findById(collectionId);
        final String message = String.format(CONFIRM_TEXT, collection.getName());
        final List<CommandLine> commandLines = Stream.of(
                new CommandButton(
                        CommandEnum.DELETE_COLLECTION,
                        CommandEnum.DELETE_COLLECTION.getDescription(),
                        CommandParameterUtils.createAction(CONFIRMED_DELETION_ACTION),
                        CommandParameterUtils.createCollectionIdParameter(collectionId)
                ),
                new ExcludeCollectionFromTrainingButton(collectionId),
                new BackButton()
        ).map(CommandLine::new).toList();

        return CommandResponse
                .builder()
                .text(message)
                .availableCommands(commandLines)
                .build();
    }

    private CommandResponse deleteCollection(final MessageContext context) {
        long collectionId = CommandParameterUtils.extractCollectionId(context);
        cardCollectionService.softDeleteById(collectionId);
        final CommandResponse commandResponse = viewCollectionListHandler.processCommand(context);
        return commandResponse
                .withAlter(DELETION_TEXT)
                .withCommand(CommandEnum.VIEW_COLLECTION_LIST);
    }


    private boolean isDeletionConfirmed(final MessageContext context) {
        return CommandParameterUtils.extractNullableAction(context)
                .filter(CONFIRMED_DELETION_ACTION::equals)
                .isPresent();
    }

}
