package com.github.dementev_alex_p.repeatit.commands.handlers.collection;

import com.github.dementev_alex_p.repeatit.collections.CardCollection;
import com.github.dementev_alex_p.repeatit.collections.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CollectionNumberButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.NextCollectionsButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.PreviousCollectionsButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.PublicCollectionsButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import com.github.dementev_alex_p.repeatit.utils.NumberTextConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ViewCollectionListHandler implements CommandHandler {

    private static final String COLLECTIONS_TEXT = """
            <strong>Коллекции</strong>
            —————————————————————
            Ниже ваши коллекции c %d по %d (всего %d):
            
            %s💡 Для перехода к коллекции нажмите на ее номер:
            """;
    private static final String ZERO_COLLECTIONS_TEXT = """
            <strong>Коллекции</strong>
            —————————————————————
            💡 Коллекции позволяют объединить карточки по темам и изучать их отдельно от остальных.
            Вы можете создать собственную коллекцию или добавить к себе публичную коллекцию.
            """;
    private static final String VIEW_COLLECTION_TEXT = """
            %s %s (карточек %d)
            
            """;
    protected static final int COUNT_COLLECTIONS_ON_PAGE = 5;

    protected final CardCollectionService cardCollectionService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.VIEW_COLLECTION_LIST;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        final int userCollectionsCount = cardCollectionService.findCountByAuthorId(context.userId());
        if (userCollectionsCount == 0) {
            return sendZeroCollectionMessage();
        } else {
            final int page = CommandParameterUtils.extractPage(context);
            final List<CardCollection> userCollections = cardCollectionService
                    .findByAuthorId(context.userId(), COUNT_COLLECTIONS_ON_PAGE, (page - 1) * COUNT_COLLECTIONS_ON_PAGE)
                    .stream()
                    .toList();

            final int firstNumber = (page - 1) * COUNT_COLLECTIONS_ON_PAGE + 1;
            final int lastNumber = firstNumber + userCollections.size() - 1;

            final List<CommandLine> commandLines = Arrays.asList(
                    createNumberButtons(userCollections, userCollectionsCount, page),
                    new CommandLine(new CommandButton(CommandEnum.CREATE_COLLECTION)),
                    new CommandLine(new PublicCollectionsButton()),
                    new CommandLine(new BackButton())
            );

            return CommandResponse
                    .builder()
                    .text(String.format(COLLECTIONS_TEXT, firstNumber, lastNumber, userCollectionsCount, covertToString(userCollections)))
                    .availableCommands(commandLines)
                    .build();
        }
    }

    private CommandResponse sendZeroCollectionMessage() {
        final List<CommandLine> lines = List.of(
                new CommandLine(new CommandButton(CommandEnum.CREATE_COLLECTION)),
                new CommandLine(new PublicCollectionsButton()),
                new CommandLine(new BackButton())
        );
        return CommandResponse.builder().text(ZERO_COLLECTIONS_TEXT).availableCommands(lines).build();
    }


    protected CommandLine createNumberButtons(
            final List<CardCollection> userCollections,
            final int totalUserCollections,
            final int pageNumber
    ) {
        final ArrayList<CommandButton> commandButtons = new ArrayList<>();

        if (pageNumber > 1) {
            commandButtons.add(new PreviousCollectionsButton(pageNumber - 1, getCommand()));
        }
        final AtomicInteger number = new AtomicInteger();
        commandButtons.addAll(userCollections
                .stream()
                .map(collection -> (CommandButton) new CollectionNumberButton(number.incrementAndGet(), collection.getId()))
                .toList()
        );
        if (totalUserCollections > COUNT_COLLECTIONS_ON_PAGE * pageNumber) {
            commandButtons.add(new NextCollectionsButton(pageNumber + 1, getCommand()));
        }

        return new CommandLine(commandButtons);
    }

    protected String covertToString(final List<CardCollection> userCollections) {
        final AtomicInteger number = new AtomicInteger();
        return userCollections
                .stream()
                .map(collection -> String.format(
                        VIEW_COLLECTION_TEXT,
                        NumberTextConverter.convert(number.incrementAndGet()),
                        collection.getName(),
                        collection.getCards().size()
                )).collect(Collectors.joining());
    }
}
