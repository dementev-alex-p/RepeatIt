package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ViewCardsCommandHandler implements CommandHandler {

    private final CardService cardService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.VIEW_CARDS;
    }

    @Override
    public void handleCommand(AbsSender sender, MessageContext context) throws TelegramApiException {

        final List<Card> userCards = cardService.findByUserId(context.userId());
        final AtomicInteger number = new AtomicInteger(1);
        final String cards = userCards
                .stream()
                .map(card -> String.format("%d. %s --> %s",number.getAndIncrement(), card.getName(), card.getDescription()))
                .collect(Collectors.joining("\n"));

        //todo обработать кейс с пустым списокм кароточек
        final SendMessage sendMessage = SendMessage
                .builder()
                .chatId(context.chatId())
                .text(String.format("Всего карточек: %d.\n%s", userCards.size(), cards))
                //.replyMarkup(createInlineKeyboard())
                .build();

        sender.execute(sendMessage);
    }
}
