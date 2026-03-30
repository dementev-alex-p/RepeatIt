package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.commands.result.RIResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeletionCardCommandHandler implements CommandHandler {


    private static final String DELETION_TEXT = "Карточка успешно удалена!";
    private static final String CONFIRM_TEXT = "\nВы уверены, что хотите удалить карточку?";
    private static final String CONFIRMED_DELETION_ACTION = "confirmed_deletion";
    private final CardService cardService;
    private final CardsCommandHandler cardsCommandHandler;
    private final SingleCardCommandHandler singleCardCommandHandler;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.DELETE_CARD;
    }

    @Override
    public ProcessingResult processCommand(final MessageContext context) {
        if (isDeletionConfirmed(context)) {
            return deleteCard(context);
        }
        return sendConfirmation(context);
        }

    private ProcessingResult sendConfirmation(final MessageContext context) {
        long cardId = CommandParameterUtils.extractCardId(context);
        final RIResponse response = singleCardCommandHandler.processCommand(context).getResponse();
        final CommandLine commandLine = new CommandLine(
                new CommandButton(
                        CommandEnum.DELETE_CARD,
                        CommandEnum.DELETE_CARD.getDescription(),
                        CommandParameterUtils.createActionParameter(CONFIRMED_DELETION_ACTION),
                        CommandParameterUtils.createCardIdParameter(cardId)
                ),
                new BackButton(
                        CommandEnum.VIEW_CARD,
                        CommandParameterUtils.createCardIdParameter(cardId)
                )
        );
        return new ProcessingResult(RIResponse
                .builder()
                .text(response.getText() + CONFIRM_TEXT)
                .availableCommands(List.of(commandLine))
                .build()
        );
    }

    private ProcessingResult deleteCard(final MessageContext context) {
        long cardId = CommandParameterUtils.extractCardId(context);
        cardService.softDeleteCardById(cardId);
        final ProcessingResult processingResult = cardsCommandHandler.processCommand(context);
        return new ProcessingResult(processingResult.getResponse().withAlter(DELETION_TEXT));
    }


    private boolean isDeletionConfirmed(final MessageContext context) {
        return CommandParameterUtils.extractNullableAction(context)
                .filter(CONFIRMED_DELETION_ACTION::equals)
                .isPresent();
    }

}
