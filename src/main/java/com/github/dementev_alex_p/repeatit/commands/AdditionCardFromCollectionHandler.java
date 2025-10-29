package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.cards.collection.CardCollection;
import com.github.dementev_alex_p.repeatit.cards.collection.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.user_states.UserState;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdditionCardFromCollectionHandler implements CommandHandler {
    private final UserStatesService userStatesService;
    private final CardCollectionService cardCollectionService;

    private static final String CHOOSE_COLLECTION_MSG = "Выберите коллекцию из списка";
    private static final String COLLECTION_ID = "collection_id";
    private static final String NOT_FOUND_COLLECTIONS_MSG = "Публичных коллекций не найдено";
    private static final String SUCCESS_ADDITION_MSG = "Коллекция '%s' их %d карточек добавлена!";

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.ADD_CARDS_FROM_COLLECTION;
    }

    @Override
    @Transactional
    public ProcessingResult processCommand(AbsSender sender, MessageContext context) {
        final Optional<UserState> userState = userStatesService.getStateByUserId(context.userId());

        if (userState.isEmpty()) {
            return beginChoosingCollection(context);
        }
        if (userState.get().getCurrentCommand() != CommandEnum.ADD_CARDS_FROM_COLLECTION) {
            userStatesService.removeStateByUserId(context.userId());
            return beginChoosingCollection(context);
        }
        userStatesService.removeStateByUserId(context.userId());
        final long chosenCollectionId = Long.parseLong(context.commandParameters().get(COLLECTION_ID));
        final CardCollection chosenCollection = cardCollectionService.findById(chosenCollectionId).orElseThrow();
        cardCollectionService.forkCardCollection(chosenCollection, context.userId());
        final List<CommandLine> commandLines = Stream.of(CommandEnum.CARDS, CommandEnum.START)
                .map(CommandLine::new)
                .toList();

        return new ProcessingResult(new MessageToSend(
                String.format(SUCCESS_ADDITION_MSG, chosenCollection.getName(), chosenCollection.getCards().size()),
                commandLines
        ));
    }

    private ProcessingResult beginChoosingCollection(MessageContext context) {
        final List<CardCollection> collections = cardCollectionService.findAvailableForUser(context.userId());
        if (collections.isEmpty()) {
            return new ProcessingResult(new MessageToSend(NOT_FOUND_COLLECTIONS_MSG));
        }
        userStatesService.addState(new UserState(context.userId(), getCommand(), null));
        final AtomicInteger number = new AtomicInteger(1);
        final List<CommandLine> commandLines = collections
                .stream()
                .map(c -> new CommandButton(
                        CommandEnum.ADD_CARDS_FROM_COLLECTION,
                        String.format("%s. %s", number.getAndIncrement(), c.getName()),
                        new CommandParameter(COLLECTION_ID, String.valueOf(c.getId()))
                ))
                .map(CommandLine::new)
                .toList();
        return new ProcessingResult(new MessageToSend(CHOOSE_COLLECTION_MSG, commandLines));
    }
}
