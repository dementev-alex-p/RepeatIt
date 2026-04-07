package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.*;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AddCardHandler implements CommandHandler {

    private static final String ADD_CARD_TEXT = """
            <strong>Добавление карточек</strong>
            —————————————————————
            - Для создания карточки нажмите "➕ Создать"
            - Для добавления карточек из публичных коллекций нажмите "📚 Коллекции"
            - Для генерации карточек на любую интересующую вас тему нажмите  "📥 Генерация"
            """;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.ADD_CARD;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        final List<CommandLine> commandLines = Arrays.asList(
                new CommandLine(Arrays.asList(
                        new CreateCardButton(),
                        new PublicCollectionsButton(CommandEnum.VIEW_COLLECTION_LIST.getDescription()),
                        new GenerateCardsButton()
                )),
                new CommandLine(new BackButton())
        );
        return CommandResponse
                .builder()
                .text(ADD_CARD_TEXT)
                .availableCommands(commandLines)
                .build();
    }
}
