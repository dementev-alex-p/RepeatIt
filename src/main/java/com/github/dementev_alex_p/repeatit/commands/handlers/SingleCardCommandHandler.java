package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.*;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SingleCardCommandHandler implements CommandHandler {

    private static final String TITLE_TEXT = """
            <strong>Карточка</strong>
            —————————————————————
            %s
            """;


    private final CardService cardService;


    @Override
    public CommandEnum getCommand() {
        return CommandEnum.VIEW_CARD;
    }

    @Override
    public ProcessingResult processCommand(MessageContext context) {

        final long cardId = CommandParameterUtils.extractCardId(context);
        final Card card = cardService.findCardById(cardId);
        final List<CommandLine> commandLines = List.of(
                new CommandLine(new EditCardFrontSideButton(card.getId())),
                new CommandLine(new EditCardBackSideButton(card.getId())),
                new CommandLine(new DeleteCardButton(card.getId())),
                new CommandLine(new BackButton(CommandEnum.CARDS))
        );

        return new ProcessingResult(RIResponse.builder()
                .text(String.format(TITLE_TEXT, CardTextConverter.convertCardToTextForView(card)))
                .availableCommands(commandLines).build()
        );
    }

}
