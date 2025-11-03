package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToEdit;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToSend;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.*;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessage;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CreationCardCommandHandler implements CommandHandler {

    private final CardService cardService;
    private final TgMessageService tgMessageService;

    private static final String TITLE_TEXT = """
            <strong>Создание</strong>
            —————————————————————
            """;

    private static final String CARD_ID_PARAM_NAME = "card_id";
    private static final String QUICK_CREATION_HINT = """
            <strong>Подсказка</strong>
            —————————————————————
            <code>💡 Для быстрого создания карточки просто введите и отправьте лицевую сторону карточки из любого пункта меню</code>
            """;
    private static final String CREATION_CARD_TEXT = """
            %s%s
            
            Новая карточка:
            %s
            """;
    private static final String FINISH_CREATION_TEXT = """
            ✅ Карточка успешно создана!
            
            %s
            """;
    private static final String WRITE_FRONT_SIDE = "✍ <i>Введите <strong>обложку</strong>...</i>";
    private static final String WRITE_BACK_SIDE = "✅ Обложка сохранена!\n\n✍ <i>Введите <strong>содержание</strong>...</i>";


    @Override
    public CommandEnum getCommand() {
        return CommandEnum.CREATE_CARD;
    }

    @Override
    public ProcessingResult processCommand(final AbsSender sender, final MessageContext context) {
        if (isStartCreationCard(context)) {
            return startCreationCard(context);
        }
        if (isSkipBackSideCommand(context)) {
            return skipBackSide(context);
        }
        final String message = context.message().orElseThrow();
        final Optional<Card> creationCard = cardService.findDraftCardByUserId(context.userId());
        if (creationCard.isEmpty()) {
            return createCard(context, message);
        } else {
            return finishCreation(context, creationCard.get(), message);
        }
    }

    private ProcessingResult skipBackSide(final MessageContext context) {
        final long cardId = Long.parseLong(context.commandParameters().get(CARD_ID_PARAM_NAME));
        final Card card = cardService.findCardById(cardId);
        return finishCreation(context, card, null);
    }

    private ProcessingResult createCard(final MessageContext context, final String frontSideText) {
        final Card card = cardService.createCard(context.userId(), frontSideText);
        final TgMessage lastMessage = tgMessageService.findLastByUserId(context.userId());
        final String startCreationText = String.format(
                CREATION_CARD_TEXT,
                TITLE_TEXT,
                WRITE_BACK_SIDE,
                CardTextConverter.convertForCreatingCard(frontSideText)
        );
        final List<CommandLine> commandLines = Collections.singletonList(
                new CommandLine(new SkipBackSideButton(CommandEnum.CREATE_CARD, card.getId()))
        );
        final List<MessageToSend> messageToSends = new ArrayList<>();
        final List<MessageToEdit> messageToEdit = new ArrayList<>();
        if (lastMessage.getCommand() == getCommand()) {
            messageToEdit.add(new MessageToEdit(
                    lastMessage.getTgMessageId(),
                    startCreationText,
                    commandLines,
                    true
            ));
        } else {
            messageToSends.add(new MessageToSend(
                    startCreationText,
                    commandLines,
                    true
            ));
        }
        final List<Integer> messageIdToDelete = context
                .tgMessageId()
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());

        return new ProcessingResult(messageToSends, messageToEdit, messageIdToDelete);
    }

    private ProcessingResult startCreationCard(final MessageContext context) {
        final List<Integer> messageIdsForDeletion = tgMessageService.findMessageIdsForDeletion(context.userId());
        final MessageToSend quickCreationMessage = new MessageToSend(
                QUICK_CREATION_HINT
        );
        final String startCreationText = String.format(
                CREATION_CARD_TEXT,
                TITLE_TEXT,
                WRITE_FRONT_SIDE,
                CardTextConverter.convertForCreatingCard(null)
        );
        final MessageToSend startCreationMessage = new MessageToSend(
                startCreationText,
                new CommandLine(new BackButton(CommandEnum.ADD_CARD))
        );
        return new ProcessingResult(
                Arrays.asList(quickCreationMessage, startCreationMessage),
                Collections.emptyList(),
                messageIdsForDeletion
        );
    }

    private boolean isStartCreationCard(final MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("action")).filter("start"::equals).isPresent();
    }

    private ProcessingResult finishCreation(final MessageContext context, final Card card, final String backSide) {
        final Card updatedCard = cardService.complitCreationCard(card, backSide);


        final List<CommandLine> commands = createCommandsForFinishMessage(card);

        final List<Integer> messageIdToDelete = context
                .tgMessageId()
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
        final TgMessage lastMessage = tgMessageService.findLastByUserId(context.userId());
        final MessageToEdit messageToEdit = new MessageToEdit(
                lastMessage.getTgMessageId(),
                TITLE_TEXT + String.format(FINISH_CREATION_TEXT, CardTextConverter.convertCardToTextForView(updatedCard)),
                commands,
                false
        );

        return new ProcessingResult(
                Collections.emptyList(),
                Collections.singletonList(messageToEdit),
                messageIdToDelete
        );
    }

    private List<CommandLine> createCommandsForFinishMessage(final Card card) {
        final CommandLine firstLine = new CommandLine(
                new EditCardButton(card.getId()),
                new CreateAnotherCardButton()
        );
        return Arrays.asList(
                firstLine,
                new CommandLine(CommandEnum.CARDS),
                new CommandLine(CommandEnum.START)
        );
    }

    private boolean isSkipBackSideCommand(final MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("action"))
                .filter(SkipBackSideButton.ACTION_VALUE::equals)
                .isPresent();
    }

}
