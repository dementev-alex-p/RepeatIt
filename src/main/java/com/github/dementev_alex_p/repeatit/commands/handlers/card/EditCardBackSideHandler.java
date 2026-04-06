package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.DeleteCardBackSideButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EditCardBackSideHandler implements CommandHandler {


    private static final String TITLE_TEXT = """
            <strong>Редактирование карточки</strong>
            —————————————————————
            %s
            ✍ Введите новое содержание
            """;
    private final CardService cardService;
    private final ViewCardHandler viewCardHandler;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.EDIT_CARD_BACK_SIDE;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        if (context.message().isEmpty()) {
            long cardId = CommandParameterUtils.extractCardId(context);
            final Card card = cardService.findCardById(cardId);
            final List<CommandLine> commandLines = new ArrayList<>();
            if (!StringUtils.isBlank(card.getBackSide())) {
                commandLines.add(new CommandLine(new DeleteCardBackSideButton(cardId)));
            }
            commandLines.add(new CommandLine(new BackButton()));

            return CommandResponse
                    .builder()
                    .text(String.format(TITLE_TEXT, CardTextConverter.convertCardToTextForEdition(card)))
                    .availableCommands(commandLines)
                    .isAnswerExcepted(true)
                    .build();
        } else {
            final long cardId = CommandParameterUtils.extractCardId(context);
            cardService.updateBackSideByCardId(cardId, context.message().get());
            return viewCardHandler.processCommand(context).withCommand(CommandEnum.VIEW_CARD);
        }
    }
}
