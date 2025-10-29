package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CardsCommand implements CommandHandler{

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

    //        final List<Card> cards = cardService.findByUserId(
//                context.userId(),
//                COUNT_CARDS_ON_PAGE,
//                (currentPage - 1) * COUNT_CARDS_ON_PAGE
//        );
//        final List<CommandLine> lastCardButton = cards
//                .stream()
//                .map(CommandButtonUtils::createForEditCard)
//                .map(CommandLine::new)
//                .toList();
//        commandLines.addAll(lastCardButton);
//        commandLines.add(createPaginationLine(totalCardCount, currentPage));

    private CommandLine createPaginationLine(final int totalCardCount, final int currentPage) {
        final List<CommandButton> buttons = new ArrayList<>();
        if (currentPage > 1) {
            buttons.add(new CommandButton(
                    CommandEnum.CARDS,
                    "< Предыдущие 10 карточек",
                    new CommandParameter(PAGINATION_PARAMETER_TEXT, String.valueOf(currentPage - 1))
            ));
        }
        final int nextPageCardCount = totalCardCount - currentPage * COUNT_CARDS_ON_PAGE;
        if (nextPageCardCount > 0) {
            buttons.add(new CommandButton(
                    CommandEnum.CARDS,
                    String.format("Следующие %d карточек >", nextPageCardCount),
                    new CommandParameter(PAGINATION_PARAMETER_TEXT, String.valueOf(currentPage + 1))
            ));
        }
        return new CommandLine(buttons);
    }
}
