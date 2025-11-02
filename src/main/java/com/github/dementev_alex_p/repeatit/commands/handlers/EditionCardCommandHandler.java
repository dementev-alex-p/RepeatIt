package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.cards.CardStatus;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.utils.CardUtils;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EditionCardCommandHandler implements CommandHandler {

    private static final String CARD_EDITION_TEXT = "Редактирование карточки \n%s";
    private static final String SUCCESS_EDITION_TEXT = "Карточка обновлена \n%s";
    private static final String FRONT_SIDE_EDITION_TEXT = "Введите лицевую сторону";
    private static final String BACK_SIDE_EDITION_TEXT = "Введите обратную сторону";

    public static final String START_EDITION_ACTION = "start_edition";
    private static final String FRONT_SIDE_EDITION_ACTION = "edit_front_side";
    private static final String BACK_SIDE_EDITION_ACTION = "edit_back_side";

    private final CardService cardService;

    private static final String CARD_ID = "card_id";

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.EDIT_CARD;
    }

    @Override
    public ProcessingResult processCommand(AbsSender sender, MessageContext context) {

        if (context.message().isPresent() && context.commandParameters().isEmpty()) {
            return updateCardContent(context.message().get(), context.userId());
        }

        final long cardId = Long.parseLong(context.commandParameters().get(CARD_ID));
        final Card card = cardService.findCardById(cardId);
        if (isAction(context, START_EDITION_ACTION)) {
            return processStartEdition(card, context);
        }
        if (isAction(context, FRONT_SIDE_EDITION_ACTION)) {
            cardService.updateStatus(card, CardStatus.EDITING_FRONT_SIDE);
            return new ProcessingResult(new MessageToSend(FRONT_SIDE_EDITION_TEXT, Collections.emptyList(), true));
        }
        if (isAction(context, BACK_SIDE_EDITION_ACTION)) {
            cardService.updateStatus(card, CardStatus.EDITING_BACK_SIDE);
            return new ProcessingResult(new MessageToSend(BACK_SIDE_EDITION_TEXT, Collections.emptyList(), true));
        }

        throw new RuntimeException();
    }

    private ProcessingResult processStartEdition(final Card card, final MessageContext context) {
        final List<Integer> messageIdsForDeletions = context.tgMessageId()
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
        final List<MessageToSend> messagesToSend = List.of(new MessageToSend(
                String.format(CARD_EDITION_TEXT, CardUtils.convertCardToTextForView(card)),
                createCommandLines(card)
        ));
        return new ProcessingResult(messagesToSend, Collections.emptyList(), messageIdsForDeletions);
    }

    private ProcessingResult updateCardContent(final String newValue, final long userId) {
        final Card card = cardService.findEditingCardByUserId(userId).orElseThrow();
        final Card updatedCard = card.getStatus() == CardStatus.EDITING_FRONT_SIDE
                ? cardService.updateContent(card, newValue, card.getBackSide())
                : cardService.updateContent(card, card.getFrontSide(), newValue);
        return new ProcessingResult(new MessageToSend(
                String.format(SUCCESS_EDITION_TEXT, CardUtils.convertCardToTextForView(updatedCard)),
                createCommandLines(updatedCard)
        ));
    }

    private boolean isAction(final MessageContext context, final String startEditionAction) {
        return Optional.ofNullable(context.commandParameters().get("action"))
                .filter(startEditionAction::equals)
                .isPresent();
    }

    private List<CommandLine> createCommandLines(final Card card) {
        final long cardId = card.getId();
        final CommandButton editFrontSideButton = new CommandButton(
                CommandEnum.EDIT_CARD,
                "Заменить лицевую сторону",
                CommandButtonUtils.createCardIdParameter(cardId),
                CommandButtonUtils.createActionParameter(FRONT_SIDE_EDITION_ACTION)
        );
        final CommandButton editBackSideButton = new CommandButton(
                CommandEnum.EDIT_CARD,
                "Заменить обратную сторону",
                CommandButtonUtils.createCardIdParameter(cardId),
                CommandButtonUtils.createActionParameter(BACK_SIDE_EDITION_ACTION)
        );
        return Stream.of(
                editFrontSideButton,
                editBackSideButton,
                //CommandButtonUtils.createForAdditionCardToCollection(card.getId()),
                CommandButtonUtils.createForDeletionCard(card.getId()),
                new CommandButton(CommandEnum.START)
        ).map(CommandLine::new).toList();
    }
}
