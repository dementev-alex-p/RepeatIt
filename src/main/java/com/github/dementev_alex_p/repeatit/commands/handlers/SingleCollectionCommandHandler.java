package com.github.dementev_alex_p.repeatit.commands.handlers;

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
import com.github.dementev_alex_p.repeatit.commands.buttons.ViewCardButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.commands.result.RIResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
public class SingleCollectionCommandHandler implements CommandHandler {

    private static final String COLLECTION_VIEW_TEXT = """
            <strong>Коллекция</strong>
            —————————————————————
            Название: %s
            
            %s
            
            %s
            """;
    //Прийти к этому
    private static final String COLLECTION_VIEW_TEXT_2 = """
            <strong>Коллекция</strong>
            —————————————————————
            Название: %s
            
            Команды:
            Изучать
            Показать карточки
            Изменить название
            Удалить
            """;
    private static final String COLLECTION_HINT = "💡 Для редактирования карточки нажмите на ее номер";
    private static final String PUBLIC_COLLECTION_HINT = "💡 Вы можете добавить публичную коллекцию к себе для изучения";
    private static final int COUNT_CARDS_ON_PAGE = 5;
    private static final String CARD_DELIMITER = "—————————————————————\n\n";
    private static final String ZERO_CARDS_VIEW = "В коллекции еще нет карточек";
    private static final String CARDS_VIEW = """
            Ниже карточки c %d по %d (всего %d):
            
            %s
            """;

    private final CardCollectionService cardCollectionService;
    private final CardService cardService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.VIEW_SINGLE_COLLECTION;
    }

    @Override
    @Transactional
    public ProcessingResult processCommand(AbsSender sender, MessageContext context) {
        final CardCollection collection = cardCollectionService.findById(CommandParameterUtils.extractCollectionId(context))
                .orElseThrow(() -> new RuntimeException("Коллекция не найдена"));
        final int page = CommandParameterUtils.extractPage(context);
        final int totalCardCount = cardService.findCardCountByCollectionId(collection.getId());
        final List<Card> cards = cardService.findCardByCollectionId(
                collection.getId(), COUNT_CARDS_ON_PAGE, (page - 1) * COUNT_CARDS_ON_PAGE
        );
        if (collection.isPublic()) {
            return viewPublicCollection(collection, page, totalCardCount, cards);
        } else {
            return viewLocalCollection(collection, page, totalCardCount, cards);
        }

    }

    private ProcessingResult viewLocalCollection(
            final CardCollection collection, final int page, final int totalCardCount, final List<Card> cards
    ) {

        final String messageText = String.format(
                COLLECTION_VIEW_TEXT,
                collection.getName(),
                convertForView(cards, totalCardCount, page),
                COLLECTION_HINT
        );
        final List<CommandLine> commandLines = Stream.of(
                createCardNumbersLine(cards, page, totalCardCount, collection.getId()),
                createCardAdditionLine(collection.getId()),
                createCollectionActionsLine(collection),
                new CommandLine(new BackButton(CommandEnum.COLLECTIONS))
        ).toList();

        return new ProcessingResult(RIResponse
                .builder()
                .text(messageText)
                .availableCommands(commandLines)
                .build()
        );
    }

    private ProcessingResult viewPublicCollection(
            final CardCollection collection, final int page, final int totalCardCount, final List<Card> cards
    ) {
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
        lines.add(new CommandLine(new BackButton(CommandEnum.COLLECTIONS, CommandParameterUtils.createActionParameter(CollectionsCommandHandler.PUBLIC_COLLECTIONS_ACTION))));
        return new ProcessingResult(RIResponse
                .builder()
                .text(messageText)
                .availableCommands(lines).build()
        );
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

    private CommandLine createCollectionActionsLine(final CardCollection collection) {
        return new CommandLine(
                new EditCollectionTitleButton(collection.getId()),
                new DeleteCollectionButton(collection.getId())
        );
    }

    private CommandLine createCardAdditionLine(final long collectionId) {
        return new CommandLine(
                new CreateCardWithCollectionId(collectionId),
                new CommandButton(CommandEnum.SEARCH)
        );
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
        final String cardText = cards
                .stream()
                .map(CardTextConverter::convertCardToTextForView)
                .collect(Collectors.joining(CARD_DELIMITER));
        return String.format(CARDS_VIEW, firstNumber, lastNumber, totalCardCount, cardText);
    }
}
