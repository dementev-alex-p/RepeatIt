package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EditCardFrontSideHandler implements CommandHandler {


    private static final String TITLE_TEXT = """
            <strong>Редактирование карточки</strong>
            —————————————————————
            %s
            ✍ Введите новую обложку
            """;
    private final CardService cardService;
    private final ViewCardHandler viewCardHandler;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.EDIT_CARD_FRONT_SIDE;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        long cardId = CommandParameterUtils.extractCardId(context);
        if (context.message().isEmpty()) {
            final Card card = cardService.findCardById(cardId);
            final BackButton backButton = new BackButton(CommandEnum.VIEW_CARD, CommandParameterUtils.createCardIdParameter(cardId));
            return CommandResponse
                    .builder()
                    .text(String.format(TITLE_TEXT, CardTextConverter.convertCardToTextForEdition(card)))
                    .availableCommands(List.of(new CommandLine(backButton)))
                    .isAnswerExcepted(true)
                    .build();
        } else {
            cardService.updateFrontSideByCardId(cardId, context.message().get());
            return viewCardHandler
                    .processCommand(context)
                    .withCommand(CommandEnum.VIEW_CARD);
        }
    }

}
