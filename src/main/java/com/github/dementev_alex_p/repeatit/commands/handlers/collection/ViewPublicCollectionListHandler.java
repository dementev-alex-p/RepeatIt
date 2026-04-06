package com.github.dementev_alex_p.repeatit.commands.handlers.collection;

import com.github.dementev_alex_p.repeatit.collections.CardCollection;
import com.github.dementev_alex_p.repeatit.collections.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.*;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class ViewPublicCollectionListHandler extends ViewCollectionListHandler {

    private static final String PUBLIC_COLLECTIONS_TEXT = """
            <strong>Публичные коллекции</strong>
            —————————————————————
            Ниже публичные коллекции c %d по %d (всего %d)
            
            %s
            💡 Для просмотра карточек коллекции нажмите на ее номер:
            """;

    private static final String ZERO_PUBLIC_COLLECTIONS_TEXT = """
            <strong>Публичные коллекции</strong>
            —————————————————————
            Все публичные коллекции уже добавлены в вашу библиотеку!
            """;

    public ViewPublicCollectionListHandler(CardCollectionService cardCollectionService) {
        super(cardCollectionService);
    }

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.VIEW_PUBLIC_COLLECTION_LIST;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        final int publicCollectionsCount = cardCollectionService.findCountPublicAvailableForUser(context.userId());
        if (publicCollectionsCount == 0) {
            return CommandResponse
                    .builder()
                    .text(ZERO_PUBLIC_COLLECTIONS_TEXT)
                    .availableCommands(List.of(new CommandLine(new BackButton())))
                    .build();
        }
        final int page = CommandParameterUtils.extractPage(context);
        final List<CardCollection> publicCollections = cardCollectionService.findPublicAvailableForUser(
                context.userId(), COUNT_COLLECTIONS_ON_PAGE, (page - 1) * COUNT_COLLECTIONS_ON_PAGE
        );
        final int firstNumber = (page - 1) * COUNT_COLLECTIONS_ON_PAGE + 1;
        final int lastNumber = firstNumber + publicCollections.size() - 1;

        final List<CommandLine> commandLines = Arrays.asList(
                createNumberButtons(publicCollections, publicCollectionsCount, page),
                new CommandLine(new BackButton())
        );

        return CommandResponse
                .builder()
                .text(String.format(PUBLIC_COLLECTIONS_TEXT, firstNumber, lastNumber, publicCollectionsCount, covertToString(publicCollections)))
                .availableCommands(commandLines)
                .build();
    }
}
