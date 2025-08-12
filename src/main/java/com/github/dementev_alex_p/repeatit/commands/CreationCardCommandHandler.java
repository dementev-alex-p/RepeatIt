package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.user_states.CreationCardAdditionData;
import com.github.dementev_alex_p.repeatit.user_states.UserState;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
    public void handleCommand(final AbsSender sender, final MessageContext context) throws TelegramApiException {

        final UserState userState = userStatesService.getStateByUserId(context.userId());
        if (userState == null) {
            beginCreationCard(sender, context);
            return;
        }
        String nextText = "";
        long cardId = ((CreationCardAdditionData) userState.getAdditionalData()).getCardId();
        final Card card = cardService.finaCardById(cardId);
        if (card.getName() == null) {
            card.setName(context.message());
            nextText = "Напишите обратную сторону карточки";
        } else {
            card.setDescription(context.message());
            userStatesService.removeStateByUserId(context.userId());
            nextText = "Карточка успешно создана!";
        }

        final SendMessage sendMessage = SendMessage
                .builder()
                .chatId(context.chatId())
                .text(nextText)
                //.replyMarkup(createInlineKeyboard())
                .build();

        sender.execute(sendMessage);
    }

    private void beginCreationCard(AbsSender sender, MessageContext context) throws TelegramApiException{

        final Card card = cardService.createCard(context.userId());
        final UserState state = new UserState(
                context.userId(),
                CommandEnum.CREATE_CARD,
                new CreationCardAdditionData(card.getId())
        );
        userStatesService.addState(state);
        sender.execute(
                SendMessage.builder()
                        .chatId(context.chatId())
                        .text("Напишите лицевую сторону карточки")
                        .build()
        );
    }
}
