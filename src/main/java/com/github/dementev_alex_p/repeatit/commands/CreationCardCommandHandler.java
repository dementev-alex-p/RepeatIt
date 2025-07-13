package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.user_states.CreationCardAdditionData;
import com.github.dementev_alex_p.repeatit.user_states.UserState;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
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
    public void handleCommand(AbsSender sender, Update update) throws TelegramApiException {
        final Long userId = update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom().getId()
                : update.getMessage().getFrom().getId();
        final UserState userState = userStatesService.getStateByUserId(userId);
        if (userState == null) {
            beginCreationCard(sender, update, userId);
            return;
        }
        String nextText = "";
        final Message message = update.getMessage();
        long cardId = ((CreationCardAdditionData) userState.getAdditionalData()).getCardId();
        final Card card = cardService.finaCardById(cardId);
        if (card.getName() == null) {
            card.setName(message.getText());
            nextText = "Напишите обратную сторону карточки";
        } else {
            card.setDescription(message.getText());
            userStatesService.removeState(userId);
            nextText = "Карточка успешно создана!";
        }

        final SendMessage sendMessage = SendMessage
                .builder()
                .chatId(message.getChat().getId())
                .text(nextText)
                //.replyMarkup(createInlineKeyboard())
                .build();

        sender.execute(sendMessage);
    }

    private void beginCreationCard(AbsSender sender, Update update, Long userId) throws TelegramApiException{

        final Card card = cardService.createCard(userId);
        final UserState state = new UserState(
                userId,
                CommandEnum.CREATE_CARD,
                new CreationCardAdditionData(card.getId())
        );
        userStatesService.addState(state);
        sender.execute(
                SendMessage.builder()
                        .chatId(update.getCallbackQuery().getMessage().getChatId())
                        .text("Напишите лицевую сторону карточки")
                        .build()
        );
    }
}
