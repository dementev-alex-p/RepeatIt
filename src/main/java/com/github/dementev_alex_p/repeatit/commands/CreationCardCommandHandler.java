package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.result.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.user_states.CreationCardAdditionData;
import com.github.dementev_alex_p.repeatit.user_states.UserState;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreationCardCommandHandler implements CommandHandler {
    private final UserStatesService userStatesService;
    private final CardService cardService;
    private final static String ACTION_PARAM = "action";
    private final static String SKIP_BACK_SIDE = "skip_back_side";
    private final static String SKIP_CARD_TEXT = "Пропустить шаг";
    private final static String VIEW_CARD = "%s --> <tg-spoiler>%s</tg-spoiler>";
    private final static String SUCCESS_CREATION_TEXT = "Карточка успешно создана!\n";


    @Override
    public CommandEnum getCommand() {
        return CommandEnum.CREATE_CARD;
    }

    @Override
    public CommandProcessingResult processCommand(final AbsSender sender, final MessageContext context)  {

        final Optional<UserState> userState = userStatesService.getStateByUserId(context.userId());
        if (userState.isEmpty()) {
            beginCreationCard(context);
            return new CommandProcessingResult("Напишите лицевую сторону карточки");
        }

        long cardId = ((CreationCardAdditionData) userState.get().getAdditionalData()).getCardId();
        final Card card = cardService.findCardById(cardId);
        if (isSkipBackSideCommand(context)) {
            return finishCreation(context, card);
        }
        final String message = context.message().orElseThrow();
        if (card.getFrontSide() == null) {
            cardService.updateFrontSideCard(card, message);
            return new CommandProcessingResult(
                    "Напишите обратную сторону карточки",
                    new CommandLine(new CommandButton(CommandEnum.CREATE_CARD, SKIP_CARD_TEXT, String.format("%s=%s", ACTION_PARAM, SKIP_BACK_SIDE)))
            );
        } else {
            final Card updatedCard = cardService.updateBackSideCard(card, message);
            return finishCreation(context, updatedCard);
        }
    }

    private CommandProcessingResult finishCreation(final MessageContext context, final Card card) {
        userStatesService.removeStateByUserId(context.userId());
        final String viewCard = card.getBackSide() != null
                ? String.format(VIEW_CARD, card.getFrontSide(), card.getBackSide())
                : card.getFrontSide();
        return new CommandProcessingResult(SUCCESS_CREATION_TEXT + viewCard, new CommandLine(CommandEnum.VIEW_CARDS));
    }

    private boolean isSkipBackSideCommand(MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get(ACTION_PARAM))
                .filter(SKIP_BACK_SIDE::equals)
                .isPresent();
    }

    private void beginCreationCard(MessageContext context) {

        final Card card = cardService.createCard(context.userId());
        final UserState state = new UserState(
                context.userId(),
                CommandEnum.CREATE_CARD,
                new CreationCardAdditionData(card.getId())
        );
        userStatesService.addState(state);

    }
}
