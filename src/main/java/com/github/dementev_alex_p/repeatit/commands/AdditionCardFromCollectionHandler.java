package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.cards.collection.CardCollection;
import com.github.dementev_alex_p.repeatit.cards.collection.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.result.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.user_states.UserState;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class AdditionCardFromCollectionHandler implements CommandHandler {
    private final UserStatesService userStatesService;
    private final CardCollectionService cardCollectionService;
    private final CardService cardService;

    private final static String CHOOSE_COLLECTION_MSG = "Выберите коллекцию из списка";
    private final static String NOT_FOUND_COLLECTIONS_MSG = "Публичных коллекций не найдено";
    private final static String SUCCESS_ADDITION_MSG = "Коллекция '%s' их %d карточек добавлена!";

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.ADD_CARDS_FROM_COLLECTION;
    }

    @Override
    @Transactional
    public CommandProcessingResult processCommand(AbsSender sender, MessageContext context) throws TelegramApiException {
        final Optional<UserState> userState = userStatesService.getStateByUserId(context.userId());

        if (userState.isEmpty()) {
            return beginChoosingCollection(context);
        }
        if (userState.get().getCurrentCommand() != CommandEnum.ADD_CARDS_FROM_COLLECTION) {
            userStatesService.removeStateByUserId(context.userId());
            return beginChoosingCollection(context);
        }
        userStatesService.removeStateByUserId(context.userId());
        long chosenCollectionId = Long.parseLong(StringUtils.substringAfter(context.data().orElseThrow(), "/"));
        final CardCollection chosenCollection = cardCollectionService.findById(chosenCollectionId).orElseThrow();
        cardCollectionService.forkCardCollection(chosenCollection, context.userId());
        return new CommandProcessingResult(
                String.format(SUCCESS_ADDITION_MSG, chosenCollection.getName(), chosenCollection.getCards().size()),
                new CommandLine(CommandEnum.VIEW_CARDS)
        );
    }

    private CommandProcessingResult beginChoosingCollection(MessageContext context) {
        final List<CardCollection> collections = cardCollectionService.findAvailableForUser(context.userId());
        if (collections.isEmpty()) {
            return new CommandProcessingResult(NOT_FOUND_COLLECTIONS_MSG);
        }
        userStatesService.addState(new UserState(context.userId(), getCommand(), null));
        final AtomicInteger number = new AtomicInteger(1);
        final List<CommandLine> commandLines = collections
                .stream()
                .map(c -> new CommandButton(
                        CommandEnum.ADD_CARDS_FROM_COLLECTION,
                        String.format("%s. %s", number.getAndIncrement(), c.getName()),
                        String.valueOf(c.getId())
                ))
                .map(CommandLine::new)
                .toList();
        return new CommandProcessingResult(CHOOSE_COLLECTION_MSG, commandLines);
    }
}
