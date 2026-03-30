package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.collection.CardCollection;
import com.github.dementev_alex_p.repeatit.cards.collection.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdditionCardFromCollectionHandler implements CommandHandler {

    private final CardCollectionService cardCollectionService;

    private static final String TITLE_TEXT = """
            <strong> Коллекции </strong>
            —————————————————————
            %s
            
            ✅ Коллекция успешно добавлена!
            Карточки доступны для изучения и начнут появляться в ежедневных тренировках.
            """;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.ADD_PUBLIC_COLLECTION;
    }

    @Override
    @Transactional
    public ProcessingResult processCommand(MessageContext context) {

        final long collectionId = CommandParameterUtils.extractCollectionId(context);

        final CardCollection collection = cardCollectionService.findById(collectionId).orElseThrow();
        final CardCollection fork = cardCollectionService.forkCardCollection(collection, context.userId());
        final String message = String.format(TITLE_TEXT, collection.getName());
        final List<CommandLine> commandLines = List.of(
                new CommandLine(new BackButton(CommandEnum.VIEW_SINGLE_COLLECTION, CommandParameterUtils.createCollectionIdParameter(fork.getId())))
        );
        return new ProcessingResult(RIResponse
                .builder()
                .text(message)
                .availableCommands(commandLines)
                .build()
        );

    }
}
