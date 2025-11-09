package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.collection.CardCollection;
import com.github.dementev_alex_p.repeatit.cards.collection.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToEdit;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToSend;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.CollectionNumberButton;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.NextCollectionsButton;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.PreviousCollectionsButton;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessage;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CollectionsCommandHandler implements CommandHandler {

    private static final String COLLECTIONS_TEXT = """
            <strong>Коллекции</strong>
            —————————————————————
            Ниже коллекции c %d по %d (всего %d)
            
            %s
            💡 Для просмотра и изменения коллекции нажмите на ее номер:
            """;
    private static final String ZERO_COLLECTIONS_TEXT = """
            <strong>Коллекции</strong>
            —————————————————————
            💡 Коллекции позволяют объединить карточки по темам и изучать их отдельно от остальных.
            Вы можете создать собственную коллекцию или добавить к себе публичную коллекцию.
            """;
    private static final String VIEW_COLLECTION_TEXT = "%d. %s (карточек %d)\n";
    private static final int COUNT_COLLECTIONS_ON_PAGE = 5;
    private final CardCollectionService cardCollectionService;
    private final TgMessageService tgMessageService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.COLLECTIONS;
    }

    @Override
    @Transactional
    public ProcessingResult processCommand(AbsSender sender, MessageContext context) {
        final int totalUserCollections = cardCollectionService.findCountByAuthorId(context.userId());
        if (totalUserCollections == 0) {
            return new ProcessingResult(new MessageToSend(
                    ZERO_COLLECTIONS_TEXT,
                    new CommandLine(new CommandButton(CommandEnum.CREATE_COLLECTION)),
                    new CommandLine(new CommandButton(CommandEnum.PUBLIC_COLLECTIONS)),
                    new CommandLine(new CommandButton(CommandEnum.START))
            ));
        } else {
            final int page = extractCurrentPage(context);
            final List<CardCollection> userCollections = cardCollectionService.findByAuthorId(
                    context.userId(), COUNT_COLLECTIONS_ON_PAGE, (page - 1) * COUNT_COLLECTIONS_ON_PAGE
            );
            final int firstNumber = (page - 1) * COUNT_COLLECTIONS_ON_PAGE + 1;
            final int lastNumber = firstNumber + userCollections.size() - 1;
            final MessageToSend message = new MessageToSend(
                    String.format(COLLECTIONS_TEXT, firstNumber, lastNumber, totalUserCollections, covertToString(userCollections)),
                    new CommandLine(createNumberButtons(userCollections, totalUserCollections, page)),
                    new CommandLine(new CommandButton(CommandEnum.CREATE_COLLECTION)),
                    new CommandLine(new CommandButton(CommandEnum.PUBLIC_COLLECTIONS)),
                    new CommandLine(new CommandButton(CommandEnum.START))
            );

            if (page == 1) {
                return new ProcessingResult(message);
            } else {
                final Optional<TgMessage> lastMessage = tgMessageService.findLastAvailableByUserId(context.userId());
                return lastMessage.isEmpty()
                        ? new ProcessingResult(message)
                        : new ProcessingResult(new MessageToEdit(lastMessage.get().getTgMessageId(), message));
            }
        }

    }

    private int extractCurrentPage(final MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get(CommandButtonUtils.PAGE_PARAMETER_TEXT))
                .map(Integer::parseInt)
                .orElse(1);
    }

    private List<CommandButton> createNumberButtons(
            final List<CardCollection> userCollections,
            final int totalUserCollections,
            final int pageNumber
    ) {
        final ArrayList<CommandButton> commandButtons = new ArrayList<>();

        if (pageNumber > 1) {
            commandButtons.add(new PreviousCollectionsButton(pageNumber - 1));
        }
        final AtomicInteger number = new AtomicInteger();
        commandButtons.addAll(userCollections
                .stream()
                .map(collection -> (CommandButton) new CollectionNumberButton(number.incrementAndGet(), collection.getId()))
                .toList()
        );
        if (totalUserCollections > COUNT_COLLECTIONS_ON_PAGE * pageNumber) {
            commandButtons.add(new NextCollectionsButton(pageNumber + 1));
        }

        return commandButtons;
    }

    private String covertToString(final List<CardCollection> userCollections) {
        final AtomicInteger number = new AtomicInteger();
        return userCollections
                .stream()
                .map(collection -> String.format(
                        VIEW_COLLECTION_TEXT,
                        number.incrementAndGet(),
                        collection.getName(),
                        collection.getCards().size()
                )).collect(Collectors.joining());
    }
}
