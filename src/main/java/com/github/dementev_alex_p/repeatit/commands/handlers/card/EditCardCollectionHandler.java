package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.collections.CardCollection;
import com.github.dementev_alex_p.repeatit.collections.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.SkipCollectionButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessage;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EditCardCollectionHandler implements CommandHandler {


    private static final String TITLE_TEXT = """
            <strong>Редактирование карточки</strong>
            —————————————————————
            %s
            
            💡 Нажмите на поиск и выберите коллекцию для карточки
            """;
    private final CardService cardService;
    private final CardCollectionService cardCollectionService;
    private final ViewCardHandler viewCardHandler;
    private final TgMessageService tgMessageService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.EDIT_CARD_COLLECTION;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        final Optional<Long> chosenCollectionId = extractCollectionIdFromMessage(context);
        final boolean isCollectionAlreadyChosen = chosenCollectionId.isPresent();
        if (isCollectionAlreadyChosen) {
            return updateCardCollection(chosenCollectionId.get(), context);
        }
        final Card card = cardService.findCardById(CommandParameterUtils.extractCardId(context));
        if (isSkipCollectionAction(context)) {
            return skipCollection(context);
        }
        final List<CommandLine> commandLines = List.of(
                new CommandLine(new CommandButton(CommandEnum.SEARCH)),
                new CommandLine(new SkipCollectionButton(card.getId())),
                new CommandLine(new BackButton())
        );
        return CommandResponse
                .builder()
                .text(String.format(TITLE_TEXT, CardTextConverter.convertCardToTextForView(card)))
                .availableCommands(commandLines)
                .build();
    }

    private CommandResponse skipCollection(final MessageContext context) {
        final long cardId = CommandParameterUtils.extractCardId(context);
        cardService.updateCardCollection(cardId, null);
        return viewCardHandler
                .processCommand(context)
                .withCommand(CommandEnum.VIEW_CARD);
    }

    private boolean isSkipCollectionAction(final MessageContext context) {
        return CommandParameterUtils.extractNullableAction(context).filter(SkipCollectionButton.ACTION_VALUE::equals).isPresent();
    }

    private CommandResponse updateCardCollection(final long collectionId, final MessageContext context) {
        final Long cardId = tgMessageService
                .findLastEditableByUserId(context.userId())
                .filter(message -> CommandEnum.EDIT_CARD_COLLECTION == message.getCommand())
                .map(TgMessage::getCommandParameters)
                .flatMap(CommandParameterUtils::extractCardId)
                .orElseThrow();

        final CardCollection collection = cardCollectionService.findById(collectionId);
        cardService.updateCardCollection(cardId, collection);
        context.commandParameters().put(CommandParameterUtils.CARD_PARAMETER_CODE, String.valueOf(cardId));
        return viewCardHandler
                .processCommand(context)
                .withCommand(CommandEnum.VIEW_CARD);
    }

    private Optional<Long> extractCollectionIdFromMessage(final MessageContext messageContext) {
        return messageContext
                .message()
                .map(message -> message.substring(CommandEnum.EDIT_CARD_COLLECTION.getCode().length() + 1).trim())
                .map(Long::parseLong);
    }


}
