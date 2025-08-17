package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.user_states.CreationCardAdditionData;
import com.github.dementev_alex_p.repeatit.user_states.UserState;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreationCardCommandHandler implements CommandHandler {
    private final UserStatesService userStatesService;
    private final CardService cardService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.CREATE_CARD;
    }

    @Override
    public CommandProcessingResult processCommand(final AbsSender sender, final MessageContext context) throws TelegramApiException {

        final Optional<UserState> userState = userStatesService.getStateByUserId(context.userId());
        if (userState.isEmpty()) {
            beginCreationCard(context);
            return new CommandProcessingResult("Напишите лицевую сторону карточки");
        }

        final String message = context.message().orElseThrow();
        long cardId = ((CreationCardAdditionData) userState.get().getAdditionalData()).getCardId();
        final Card card = cardService.finaCardById(cardId);
        if (card.getName() == null) {
            card.setName(message);
            return new CommandProcessingResult("Напишите обратную сторону карточки");
        } else {
            card.setDescription(message);
            userStatesService.removeStateByUserId(context.userId());
            return new CommandProcessingResult("Карточка успешно создана!", new CommandLine(CommandEnum.VIEW_CARDS));
        }

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
