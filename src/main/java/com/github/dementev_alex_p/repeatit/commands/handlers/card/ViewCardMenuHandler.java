package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.*;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ViewCardMenuHandler implements CommandHandler {

    private static final String YOUR_CARDS = """
            <strong>Карточки</strong>
            —————————————————————
            Всего карточек в вашей библиотеке: <strong>%d</strong>
            %s
            """;
    private static final String HINT_TEXT = """
            
            🔍︎ Поиск -> начните вводить обложку или содержание и выберите карточку
            ➕ Создать -> ручное создание карточки
            📚 Коллекции -> добавление карточек из публичных коллекций
            ✨ Генерация -> создание карточек с помощью ИИ на любую тему
            """;

    private final CardService cardService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.VIEW_CARD_MENU;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        final int totalCardCount = cardService.findCardCountForUserId(context.userId());
        final boolean isMessageWithHint = CommandParameterUtils.isViewHintRequired(context);

        final List<CommandLine> commandLines = addHintButtonIfRequired(context, List.of(
                new CommandLine(new CommandButton(CommandEnum.SEARCH), new CreateCardButton()),
                new CommandLine(
                        new PublicCollectionsButton(CommandEnum.VIEW_COLLECTION_LIST.getDescription()),
                        new GenerateCardsButton()),
                new CommandLine(new BackButton())
        ));

        return CommandResponse
                .builder()
                .text(String.format(YOUR_CARDS, totalCardCount, isMessageWithHint ? HINT_TEXT : ""))
                .availableCommands(commandLines)
                .build();
    }
}
