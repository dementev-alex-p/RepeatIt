package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.collection.CardCollection;
import com.github.dementev_alex_p.repeatit.cards.collection.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CollectionNumberButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.NextCollectionsButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.PreviousCollectionsButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.PublicCollectionsButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToEdit;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.commands.result.RIResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ViewCollectionListCommandHandler implements CommandHandler {

    private static final String COLLECTIONS_TEXT = """
            <strong>Коллекции</strong>
            —————————————————————
            Ниже коллекции c %d по %d (всего %d)
            
            %s
            💡 Для просмотра и изменения коллекции нажмите на ее номер:
            """;
    private static final String PUBLIC_COLLECTIONS_TEXT = """
            <strong>Публичные коллекции</strong>
            —————————————————————
            Ниже коллекции c %d по %d (всего %d)
            
            %s
            💡 Для просмотра коллекции нажмите на ее номер:
            """;
    private static final String ZERO_COLLECTIONS_TEXT = """
            <strong>Коллекции</strong>
            —————————————————————
            💡 Коллекции позволяют объединить карточки по темам и изучать их отдельно от остальных.
            Вы можете создать собственную коллекцию или добавить к себе публичную коллекцию.
            """;
    private static final String ZERO_PUBLIC_COLLECTIONS_TEXT = """
            <strong>Публичные коллекции</strong>
            —————————————————————
            Все публичные коллекции уже добавлены в вашу библиотеку!
            """;
    private static final String VIEW_COLLECTION_TEXT = "%d. %s (карточек %d)\n";
    private static final int COUNT_COLLECTIONS_ON_PAGE = 5;
    public static final String PUBLIC_COLLECTIONS_ACTION = "public_collections";

    private final CardCollectionService cardCollectionService;
    @Override
    public CommandEnum getCommand() {
        return CommandEnum.VIEW_COLLECTION_LIST;
    }

    @Override
    @Transactional
    public ProcessingResult processCommand(MessageContext context) {
        if (isPublicCollectionView(context)) {
            return viewPublicCollections(context);
        } else {
            return viewUserCollections(context);
        }
    }

    private ProcessingResult viewUserCollections(final MessageContext context) {
        final int userCollectionsCount = cardCollectionService.findCountByAuthorId(context.userId());
        if (userCollectionsCount == 0) {
            return sendZeroCollectionMessage();
        } else {
            final int page = CommandParameterUtils.extractPage(context);
            final List<CardCollection> userCollections = cardCollectionService.findByAuthorId(
                    context.userId(), COUNT_COLLECTIONS_ON_PAGE, (page - 1) * COUNT_COLLECTIONS_ON_PAGE
            );

            final int firstNumber = (page - 1) * COUNT_COLLECTIONS_ON_PAGE + 1;
            final int lastNumber = firstNumber + userCollections.size() - 1;

            final List<CommandLine> commandLines = Arrays.asList(
                    createNumberButtons(userCollections, userCollectionsCount, page, false),
                    new CommandLine(new CommandButton(CommandEnum.CREATE_COLLECTION)),
                    new CommandLine(new PublicCollectionsButton()),
                    new CommandLine(new CommandButton(CommandEnum.START))
            );

            return new ProcessingResult(
                    RIResponse
                            .builder()
                            .text(String.format(COLLECTIONS_TEXT, firstNumber, lastNumber, userCollectionsCount, covertToString(userCollections)))
                            .availableCommands(commandLines)
                            .build()
            );
        }
    }

    private ProcessingResult sendZeroCollectionMessage() {
        final List<CommandLine> lines = List.of(
                new CommandLine(new CommandButton(CommandEnum.CREATE_COLLECTION)),
                new CommandLine(new PublicCollectionsButton()),
                new CommandLine(new CommandButton(CommandEnum.START))
        );
        return new ProcessingResult(RIResponse.builder().text(ZERO_COLLECTIONS_TEXT).availableCommands(lines).build());
    }

    private ProcessingResult viewPublicCollections(final MessageContext context) {
        final int publicCollectionsCount = cardCollectionService.findCountPublicAvailableForUser(context.userId());
        if (publicCollectionsCount == 0) {
            return new ProcessingResult(new MessageToEdit(
                    MessageToEdit.LAST_MESSAGE,
                    ZERO_PUBLIC_COLLECTIONS_TEXT,
                    new CommandLine(new BackButton(CommandEnum.VIEW_COLLECTION_LIST)))
            );
        }
        final int page =  CommandParameterUtils.extractPage(context);
        final List<CardCollection> publicCollections = cardCollectionService.findPublicAvailableForUser(
                context.userId(), COUNT_COLLECTIONS_ON_PAGE, (page - 1) * COUNT_COLLECTIONS_ON_PAGE
        );
        final int firstNumber = (page - 1) * COUNT_COLLECTIONS_ON_PAGE + 1;
        final int lastNumber = firstNumber + publicCollections.size() - 1;

        final List<CommandLine> commandLines = Arrays.asList(
                createNumberButtons(publicCollections, publicCollectionsCount, page, true),
                new CommandLine(new BackButton(CommandEnum.VIEW_COLLECTION_LIST))
        );

        return new ProcessingResult(
                RIResponse
                        .builder()
                        .text(String.format(PUBLIC_COLLECTIONS_TEXT, firstNumber, lastNumber, publicCollectionsCount, covertToString(publicCollections)))
                        .availableCommands(commandLines)
                        .build()
        );
    }

    private boolean isPublicCollectionView(final MessageContext context) {
        return Optional
                .ofNullable(context.commandParameters().get(CommandParameterUtils.ACTION_PARAMETER_CODE))
                .filter(PUBLIC_COLLECTIONS_ACTION::equals)
                .isPresent();
    }

    private CommandLine createNumberButtons(
            final List<CardCollection> userCollections,
            final int totalUserCollections,
            final int pageNumber,
            final boolean isPublic
    ) {
        final ArrayList<CommandButton> commandButtons = new ArrayList<>();

        if (pageNumber > 1) {
            commandButtons.add(new PreviousCollectionsButton(pageNumber - 1, isPublic));
        }
        final AtomicInteger number = new AtomicInteger();
        commandButtons.addAll(userCollections
                .stream()
                .map(collection -> (CommandButton) new CollectionNumberButton(number.incrementAndGet(), collection.getId()))
                .toList()
        );
        if (totalUserCollections > COUNT_COLLECTIONS_ON_PAGE * pageNumber) {
            commandButtons.add(new NextCollectionsButton(pageNumber + 1, isPublic));
        }

        return new CommandLine(commandButtons);
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
