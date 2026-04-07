package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.*;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ViewCardMenuHandler implements CommandHandler {

    private static final String YOUR_CARDS = """
            <strong>Карточки</strong>
            —————————————————————
            Всего карточек: %d
            
            Вы можете создать карточку вручную или
            - добавить к себе карточки из публичных коллекций -> "📚 Коллекции"
            - сгенерировать карточки на любую интересующую вас тему -> "✨ Генерация"
            """;
    private final CardService cardService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.VIEW_CARD_MENU;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        final int totalCardCount = cardService.findCardCountForUserId(context.userId());
        final List<CommandLine> commandLines = List.of(
                new CommandLine(new CommandButton(CommandEnum.SEARCH), new CreateCardButton()),
                new CommandLine(
                        new PublicCollectionsButton(CommandEnum.VIEW_COLLECTION_LIST.getDescription()),
                        new GenerateCardsButton()
                ),
                new CommandLine(new BackButton())
        );

        return CommandResponse
                .builder()
                .text(String.format(YOUR_CARDS, totalCardCount))
                .availableCommands(commandLines)
                .build();
    }
}
