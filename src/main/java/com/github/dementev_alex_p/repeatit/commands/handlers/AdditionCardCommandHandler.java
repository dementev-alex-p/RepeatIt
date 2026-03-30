package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.PublicCollectionsButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.commands.result.RIResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AdditionCardCommandHandler implements CommandHandler {

    private static final String ADD_CARD_TEXT = """
            <strong>Добавление карточек</strong>
            —————————————————————
            - Для создания карточки нажмите "➕ Создать"
            - Для добавления карточек из публичных коллекций нажмите "📚 Коллекции"
            - Для импорта карточек  "📥 Импорт"
            """;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.ADD_CARD;
    }

    @Override
    public ProcessingResult processCommand(MessageContext context) {
        final List<CommandLine> commandLines = Arrays.asList(
                new CommandLine(Arrays.asList(
                        new CommandButton(CommandEnum.CREATE_CARD),
                        new PublicCollectionsButton(CommandEnum.VIEW_COLLECTION_LIST.getDescription()),
                        new CommandButton(CommandEnum.IMPORT_CARDS)
                )),
                new CommandLine(new BackButton(CommandEnum.VIEW_CARD_LIST))
        );
        return new ProcessingResult(RIResponse
                .builder()
                .text(ADD_CARD_TEXT)
                .availableCommands(commandLines)
                .build()
        );
    }
}
