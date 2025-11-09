package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CardsCommandHandler implements CommandHandler {

    private static final String YOUR_CARDS = """
            <strong>Карточки</strong>
            Всего карточек: %d
            💡 Для изменения и удаления карточки воспользуйтесь "🔍︎ Поиск"
            """;
    private static final int COUNT_CARDS_ON_PAGE = 5;
    private static final String PAGINATION_PARAMETER_TEXT = "page";
    private final CardService cardService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.CARDS;
    }

    @Override
    public ProcessingResult processCommand(AbsSender sender, MessageContext context) {
        final int totalCardCount = cardService.findCardCountForUserId(context.userId());
        final List<CommandLine> commandLines = new ArrayList<>();
        commandLines.add(new CommandLine(
                new CommandButton(CommandEnum.SEARCH),
                new CommandButton(CommandEnum.ADD_CARD)
        ));

        commandLines.add(new CommandLine(new CommandButton(CommandEnum.START)));
        return new ProcessingResult(new MessageToSend(String.format(YOUR_CARDS,  totalCardCount), commandLines));
    }
}
