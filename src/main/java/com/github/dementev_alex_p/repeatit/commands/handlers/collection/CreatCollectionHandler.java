package com.github.dementev_alex_p.repeatit.commands.handlers.collection;

import com.github.dementev_alex_p.repeatit.collections.CardCollection;
import com.github.dementev_alex_p.repeatit.collections.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.handlers.card.EditCardCollectionHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessage;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
    private final EditCardCollectionHandler editCardCollectionHandler;
    private final TgMessageService tgMessageService;

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

        final Optional<TgMessage> previousMessage = findPreviousMessage(context);
        if (previousMessage.filter(m -> m.getCommand() == CommandEnum.EDIT_CARD_COLLECTION).isPresent()) {
            return viewEditCardCollectionMessage(context, previousMessage.get(), cardCollection);
        }

        context.commandParameters()
                .put(CommandParameterUtils.COLLECTION_PARAMETER_CODE, String.valueOf(cardCollection.getId()));
        return viewCollectionHandler
                .processCommand(context)
                .withCommand(CommandEnum.VIEW_COLLECTION)
                .withAlter(FINISH_CREATION_TEXT);

    }

    private CommandResponse viewEditCardCollectionMessage(final MessageContext context, final TgMessage tgMessage, final CardCollection cardCollection) {
        final Long cardId = CommandParameterUtils.extractCardId(tgMessage.getCommandParameters()).orElseThrow();
        final MessageContext newContext = context
                .withMessage(Optional.empty())
                .withCommandParameters(CommandParameterUtils.convert(
                        CommandParameterUtils.createCollectionIdParameter(cardCollection.getId()),
                        CommandParameterUtils.createCardIdParameter(cardId)
                ));
        return editCardCollectionHandler.processCommand(newContext);
    }

    private Optional<TgMessage> findPreviousMessage(final MessageContext context) {
        //Ищем предпоследнее сообщение, тк последнее это создание коллекции
        final List<TgMessage> lastMessages = tgMessageService
                .findLastedMessagesByUserIdOrderedByCreatedAtDesc(context.userId(), 2);
        if (lastMessages.size() < 2) {
            return Optional.empty();
        }
        return Optional.of(lastMessages.get(1));
    }

    private CommandResponse showCreationMessage() {
        return CommandResponse
                .builder()
                .text(TITLE_TEXT)
                .availableCommands(List.of(new CommandLine(new BackButton())))
                .isAnswerExcepted(true)
                .build();
    }
}
