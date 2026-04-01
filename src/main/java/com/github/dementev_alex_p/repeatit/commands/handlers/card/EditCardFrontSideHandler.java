package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.commands.result.RIResponse;
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
    public ProcessingResult processCommand(MessageContext context) {
        if (context.message().isEmpty()) {
            long cardId = CommandParameterUtils.extractCardId(context);
            final Card card = cardService.findCardById(cardId);
            final BackButton backButton = new BackButton(CommandEnum.VIEW_CARD, CommandParameterUtils.createCardIdParameter(cardId));
            return new ProcessingResult(RIResponse
                    .builder()
                    .text(String.format(TITLE_TEXT, CardTextConverter.convertCardToTextForEdition(card)))
                    .availableCommands(List.of(new CommandLine(backButton)))
                    .isAnswerExcepted(true)
                    .messageMetaInfo(String.valueOf(cardId))
                    .build()
            );
        } else {
            final long cardId = Long.parseLong(CommandParameterUtils.extractLastMessageMetaInfo(context));
            cardService.updateFrontSideByCardId(cardId, context.message().get());
            context.commandParameters().put(CommandParameterUtils.CARD_PARAMETER_CODE, String.valueOf(cardId));
            return viewCardHandler.processCommand(context);
        }
    }

}
