package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ViewCardListHandler implements CommandHandler {

    private static final String YOUR_CARDS = """
            <strong>Карточки</strong>
            —————————————————————
            Всего карточек: %d
            💡 Для изменения и удаления карточки воспользуйтесь "🔍︎ Поиск"
            """;
    private final CardService cardService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.VIEW_CARD_LIST;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        final int totalCardCount = cardService.findCardCountForUserId(context.userId());
        final List<CommandLine> commandLines = new ArrayList<>();
        commandLines.add(new CommandLine(
                new CommandButton(CommandEnum.SEARCH),
                new CommandButton(CommandEnum.ADD_CARD)
        ));
        commandLines.add(new CommandLine(new CommandButton(CommandEnum.MAIN_MENU)));

        return CommandResponse
                .builder()
                .text(String.format(YOUR_CARDS,  totalCardCount))
                .availableCommands(commandLines)
                .build();
    }
}
