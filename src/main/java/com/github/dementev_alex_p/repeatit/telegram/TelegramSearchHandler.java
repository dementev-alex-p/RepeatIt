package com.github.dementev_alex_p.repeatit.telegram;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.collections.CardCollection;
import com.github.dementev_alex_p.repeatit.collections.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessage;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TelegramSearchHandler {

    private final TgMessageService tgMessageService;
    private final CardCollectionService cardCollectionService;
    private final CardService cardService;

    public void search(final TelegramBot telegramBot, final InlineQuery inlineQuery) {
        final long userId = inlineQuery.getFrom().getId();
        final String query = inlineQuery.getQuery();

        final List<InlineQueryResult> inlineResults = processSearch(userId, query);

        final AnswerInlineQuery answer = AnswerInlineQuery
                .builder()
                .inlineQueryId(inlineQuery.getId())
                .results(inlineResults)
                .cacheTime(10)
                .isPersonal(true)
                .build();
        try {
            telegramBot.execute(answer);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private List<InlineQueryResult> processSearch(final long userId, final String query) {
        final Optional<TgMessage> lastMessage = tgMessageService.findLastEditableByUserId(userId);

        if (isCollectionSearch(lastMessage)) {
            return searchCollection(userId, query);
        }
        if (isCardInCollectionSearch(lastMessage)) {
            return searchCardInCollection(userId, query, lastMessage);
        }
        return searchCard(userId, query);
    }

    private List<InlineQueryResult> searchCardInCollection(final long userId, final String query, final Optional<TgMessage> lastMessage) {
        final Long collectionId = lastMessage
                .map(TgMessage::getCommandParameters)
                .flatMap(CommandParameterUtils::extractNullableCollectionId)
                .orElseThrow();

        final List<Card> cards = cardService.searchCardInCollection(userId, query, collectionId);

        return cards
                .stream()
                .map(this::toInlineQueryResult)
                .collect(Collectors.toList());
    }

    private boolean isCardInCollectionSearch(final Optional<TgMessage> lastMessage) {
        return lastMessage
                .map(TgMessage::getCommand)
                .filter(CommandEnum.VIEW_COLLECTION::equals)
                .isPresent();
    }
    private boolean isCollectionSearch(final Optional<TgMessage> lastMessage) {
        return lastMessage
                .map(TgMessage::getCommand)
                .filter(CommandEnum.EDIT_CARD_COLLECTION::equals)
                .isPresent();
    }

    private List<InlineQueryResult> searchCard(final long userId, final String query) {
        final List<Card> cards = cardService.searchCard(userId, query);

        return cards
                .stream()
                .map(this::toInlineQueryResult)
                .collect(Collectors.toList());
    }

    public InlineQueryResult toInlineQueryResult(Card card) {

        final InputTextMessageContent messageContent = InputTextMessageContent.builder()
                .messageText(String.format("/%s %d", CommandEnum.VIEW_CARD.getCode(), card.getId()))
                .build();

        return InlineQueryResultArticle.builder()
                .id(card.getId().toString())
                .title(card.getFrontSide())
                .description(card.getBackSide())
                .inputMessageContent(messageContent)
                .build();
    }

    private List<InlineQueryResult> searchCollection(final long userId, final String query) {
        final List<CardCollection> collections = cardCollectionService.searchCollection(userId, query);

        return collections
                .stream()
                .map(this::toInlineQueryResult)
                .collect(Collectors.toList());
    }

    public InlineQueryResult toInlineQueryResult(final CardCollection collection) {

        final InputTextMessageContent messageContent = InputTextMessageContent.builder()
                .messageText(String.format("/%s %d", CommandEnum.EDIT_CARD_COLLECTION.getCode(), collection.getId()))
                .build();

        return InlineQueryResultArticle.builder()
                .id(String.valueOf(collection.getId()))
                .title(collection.getName())
                .inputMessageContent(messageContent)
                .build();
    }

}
