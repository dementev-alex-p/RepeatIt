package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.handlers.TrainingCommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.training.Training;
import com.github.dementev_alex_p.repeatit.training.TrainingService;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeleteCardHandler implements CommandHandler {


    private static final String CONFIRM_TEXT = """
            <strong>Удаление карточки</strong>
            —————————————————————
            %s
            Вы уверены, что хотите удалить карточку?
            """;
    private static final String DELETION_TEXT = "Карточка успешно удалена!";
    private static final String CONFIRMED_DELETION_ACTION = "confirmed_deletion";
    private final CardService cardService;
    private final TrainingService trainingService;
    private final TrainingCommandHandler trainingCommandHandler;
    private final ViewCardListHandler viewCardListHandler;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.DELETE_CARD;
    }

    @Override
    public CommandResponse processCommand(final MessageContext context) {
        if (isDeletionConfirmed(context)) {
            return deleteCard(context);
        }
        return sendConfirmation(context);
    }

    private CommandResponse sendConfirmation(final MessageContext context) {
        long cardId = CommandParameterUtils.extractCardId(context);
        final Card card = cardService.findCardById(cardId);
        final String message = String.format(CONFIRM_TEXT, CardTextConverter.convertCardToTextForView(card));
        final CommandLine commandLine = new CommandLine(
                new CommandButton(
                        CommandEnum.DELETE_CARD,
                        CommandEnum.DELETE_CARD.getDescription(),
                        CommandParameterUtils.createActionParameter(CONFIRMED_DELETION_ACTION),
                        CommandParameterUtils.createCardIdParameter(cardId)
                ),
                new BackButton()
        );
        return CommandResponse
                .builder()
                .text(message)
                .availableCommands(List.of(commandLine))
                .build();
    }

    private CommandResponse deleteCard(final MessageContext context) {
        long cardId = CommandParameterUtils.extractCardId(context);
        cardService.softDeleteCardById(cardId);

        Optional<Training> currentTraining = trainingService.findCurrentTraining(context.userId());
        final boolean isTrainingStartedNow = currentTraining.isPresent();

        if (isTrainingStartedNow) {
            trainingService.deleteCardFromCurrentTraining(currentTraining.get(), cardId);
            final CommandResponse commandResponse = trainingCommandHandler.processCommand(context);
            return commandResponse
                    .withAlter(DELETION_TEXT)
                    .withCommand(CommandEnum.TRAINING);
        } else {
            final CommandResponse commandResponse = viewCardListHandler.processCommand(context);
            return commandResponse
                    .withAlter(DELETION_TEXT)
                    .withCommand(CommandEnum.VIEW_CARD_LIST);
        }

    }


    private boolean isDeletionConfirmed(final MessageContext context) {
        return CommandParameterUtils.extractNullableAction(context)
                .filter(CONFIRMED_DELETION_ACTION::equals)
                .isPresent();
    }

}
