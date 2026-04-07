package com.github.dementev_alex_p.repeatit.commands.handlers.collection;

import com.github.dementev_alex_p.repeatit.collections.CardCollection;
import com.github.dementev_alex_p.repeatit.collections.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcludeCollectionFromTrainingHandler implements CommandHandler {

    private static final String CONFIRM_TEXT = """
            <strong>Исключение коллекции из тренировки</strong>
            —————————————————————
            Название: <code>%s</code>
            
            Вы уверены, что хотите исключить коллекцию?
            Карточки исключенных коллекций не попадают в состав тренировок.
            Вы всегда сможете снять исключение, тогда алгоритмы снова начнут добавлять карточки коллекции в тренировки.
            """;

    private static final String CONFIRM_ACTION = "confirm";
    private static final String EXCLUSION_TEXT = "Коллекция исключена из тренировок!";
    private final CardCollectionService cardCollectionService;
    private final ViewCollectionHandler viewCollectionHandler;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.EXCLUDE_COLLECTION_FROM_TRAINING;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        if (isExclusionConfirmed(context)) {
            return excludeCollection(context);
        }
        return sendConfirmation(context);
    }

    private CommandResponse sendConfirmation(final MessageContext context) {
        final long collectionId = CommandParameterUtils.extractCollectionId(context);
        final CardCollection collection = cardCollectionService.findById(collectionId);
        final String message = String.format(CONFIRM_TEXT, collection.getName());
        final CommandLine commandLine = new CommandLine(
                new CommandButton(
                        CommandEnum.EXCLUDE_COLLECTION_FROM_TRAINING,
                        CommandEnum.EXCLUDE_COLLECTION_FROM_TRAINING.getDescription(),
                        CommandParameterUtils.createAction(CONFIRM_ACTION),
                        CommandParameterUtils.createCollectionIdParameter(collectionId)
                ),
                new BackButton()
        );
        return CommandResponse
                .builder()
                .text(message)
                .availableCommands(List.of(commandLine))
                .build();
    }

    private CommandResponse excludeCollection(final MessageContext context) {
        long collectionId = CommandParameterUtils.extractCollectionId(context);
        cardCollectionService.excludeFromTraining(collectionId);
        final CommandResponse commandResponse = viewCollectionHandler.processCommand(context);
        return commandResponse
                .withAlter(EXCLUSION_TEXT)
                .withCommand(CommandEnum.VIEW_COLLECTION);
    }


    private boolean isExclusionConfirmed(final MessageContext context) {
        return CommandParameterUtils.extractNullableAction(context)
                .filter(CONFIRM_ACTION::equals)
                .isPresent();
    }
}