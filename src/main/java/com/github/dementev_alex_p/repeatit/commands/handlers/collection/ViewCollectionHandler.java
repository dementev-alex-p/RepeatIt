package com.github.dementev_alex_p.repeatit.commands.handlers.collection;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.cards.collection.CardCollection;
import com.github.dementev_alex_p.repeatit.cards.collection.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.AddPublicCollectionButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CreateCardWithCollectionId;
import com.github.dementev_alex_p.repeatit.commands.buttons.DeleteCollectionButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.EditCollectionTitleButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.NextCardsButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.PreviousCardsButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.SearchCardInCollectionButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.StudyCollectionButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.ViewCardButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.ViewCardsInCollectionButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
public class ViewCollectionHandler implements CommandHandler {

    private static final String COLLECTION_VIEW_TEXT = """
            <strong>Коллекция</strong>
            —————————————————————
            <strong>Название:</strong> %s
            %s
            %s
            """;
    private static final String COLLECTION_CARD_HINT = "💡 Для редактирования карточки нажмите на ее номер";
    private static final String COLLECTION_CARD_COUNT_TEXT = "Карточек в коллекции: %d";
    private static final String PUBLIC_COLLECTION_HINT = "💡 Вы можете добавить публичную коллекцию к себе для изучения";
    private static final int COUNT_CARDS_ON_PAGE = 5;
    private static final String CARD_DELIMITER = "—————————————————————\n";
    private static final String ZERO_CARDS_VIEW = "В коллекции еще нет карточек";
    private static final String CARDS_VIEW = """
            Ниже карточки c %d по %d (всего %d):
            
            %s
            """;
    private static final String CARD_WITH_NUMBER = "%d. \n%s";

    private final CardCollectionService cardCollectionService;
    private final CardService cardService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.VIEW_COLLECTION;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        final CardCollection collection = cardCollectionService.findById(
                CommandParameterUtils.extractCollectionId(context)
        );
        if (collection.isPublic()) {
            return viewPublicCollection(context, collection);
        } else {
            return viewLocalCollection(context, collection);
        }
    }

    private CommandResponse viewLocalCollection(final MessageContext context, final CardCollection collection) {
        if (isViewCardsAction(context)) {
            return viewLocalCollectionWithCards(context, collection);
        }
        final int cardCount = cardService.findCardCountByCollectionId(collection.getId());
        final String collectionText = String.format(
                COLLECTION_VIEW_TEXT,
                collection.getName(),
                String.format(COLLECTION_CARD_COUNT_TEXT, cardCount),
                ""
        );
        return CommandResponse
                .builder()
                .text(collectionText)
                .availableCommands(createCommandLinesForView(collection, cardCount))
                .build();
    }

    private List<CommandLine> createCommandLinesForView(final CardCollection collection, final int cardCount) {
        return cardCount > 0
                ? List.of(
                new CommandLine(new StudyCollectionButton(collection.getId())),
                new CommandLine(new ViewCardsInCollectionButton(collection.getId())),
                new CommandLine(new EditCollectionTitleButton(collection.getId()), new DeleteCollectionButton(collection.getId())),
                new CommandLine(new BackButton(CommandEnum.VIEW_COLLECTION_LIST)))
                : List.of(
                new CommandLine(new CreateCardWithCollectionId(collection.getId())),
                new CommandLine(new EditCollectionTitleButton(collection.getId()), new DeleteCollectionButton(collection.getId())),
                new CommandLine(new BackButton(CommandEnum.VIEW_COLLECTION_LIST))
        );
    }

    private CommandResponse viewLocalCollectionWithCards(final MessageContext context, final CardCollection collection) {
        final int page = CommandParameterUtils.extractPage(context);
        final int totalCardCount = cardService.findCardCountByCollectionId(collection.getId());
        final List<Card> cards = cardService.findCardsByCollectionId(
                collection.getId(), COUNT_CARDS_ON_PAGE, (page - 1) * COUNT_CARDS_ON_PAGE
        );
        final String messageText = String.format(
                COLLECTION_VIEW_TEXT,
                collection.getName(),
                convertForView(cards, totalCardCount, page),
                COLLECTION_CARD_HINT
        );
        final List<CommandLine> commandLines = Stream.of(
                createCardNumbersLine(cards, page, totalCardCount, collection.getId()),
                new CommandLine(new CreateCardWithCollectionId(collection.getId()), new SearchCardInCollectionButton()),
                new CommandLine(new BackButton(CommandEnum.VIEW_COLLECTION_LIST))
        ).toList();

        return CommandResponse
                .builder()
                .text(messageText)
                .availableCommands(commandLines)
                .build();
    }

    private boolean isViewCardsAction(final MessageContext context) {
        return CommandParameterUtils
                .extractNullableAction(context).filter(ViewCardsInCollectionButton.VIEW_CARDS_ACTION::equals).isPresent();
    }

    private CommandResponse viewPublicCollection(final MessageContext context, final CardCollection collection) {

        final int page = CommandParameterUtils.extractPage(context);
        final int totalCardCount = cardService.findCardCountByCollectionId(collection.getId());
        final List<Card> cards = cardService.findCardsByCollectionId(
                collection.getId(), COUNT_CARDS_ON_PAGE, (page - 1) * COUNT_CARDS_ON_PAGE
        );

        final String messageText = String.format(
                COLLECTION_VIEW_TEXT,
                collection.getName(),
                convertForView(cards, totalCardCount, page),
                PUBLIC_COLLECTION_HINT
        );
        final List<CommandLine> lines = new ArrayList<>();
        createPaginationLine(cards, page, totalCardCount, collection.getId())
                .ifPresent(lines::add);
        lines.add(new CommandLine(new AddPublicCollectionButton(collection.getId())));
        lines.add(new CommandLine(new BackButton(CommandEnum.VIEW_COLLECTION_LIST, CommandParameterUtils.createActionParameter(ViewCollectionListHandler.PUBLIC_COLLECTIONS_ACTION))));
        return CommandResponse
                .builder()
                .text(messageText)
                .availableCommands(lines)
                .build();
    }

    private Optional<CommandLine> createPaginationLine(final List<Card> cards, final int page, final int totalCardCount, final long collectionId) {
        if (cards.size() < COUNT_CARDS_ON_PAGE) {
            return Optional.empty();
        }
        final List<CommandButton> buttons = new ArrayList<>();
        if (page > 1) {
            buttons.add(new PreviousCardsButton(page - 1, collectionId));
        }
        if (totalCardCount > page * COUNT_CARDS_ON_PAGE) {
            buttons.add(new NextCardsButton(page + 1, collectionId));
        }
        return Optional.of(new CommandLine(buttons));
    }

    private CommandLine createCardNumbersLine(final List<Card> cards, final int page, final int totalCardCount, final long collectionId) {
        final List<CommandButton> buttons = new ArrayList<>();
        if (page > 1) {
            buttons.add(new PreviousCardsButton(page - 1, collectionId));
        }
        final AtomicInteger number = new AtomicInteger();
        buttons.addAll(cards
                .stream()
                .map(card -> (CommandButton) new ViewCardButton(number.incrementAndGet(), card.getId()))
                .toList()
        );
        if (totalCardCount > page * COUNT_CARDS_ON_PAGE) {
            buttons.add(new NextCardsButton(page + 1, collectionId));
        }
        return new CommandLine(buttons);
    }

    private String convertForView(final List<Card> cards, final int totalCardCount, final int page) {
        if (cards.isEmpty()) {
            return ZERO_CARDS_VIEW;
        }
        final int firstNumber = (page - 1) * COUNT_CARDS_ON_PAGE + 1;
        final int lastNumber = firstNumber + cards.size() - 1;
        final AtomicInteger number = new AtomicInteger();
        final String cardText = cards
                .stream()
                .map(CardTextConverter::convertCardToShortText)
                .map(text -> String.format(CARD_WITH_NUMBER, number.incrementAndGet(), text))
                .collect(Collectors.joining(CARD_DELIMITER));
        return String.format(CARDS_VIEW, firstNumber, lastNumber, totalCardCount, cardText);
    }
}
