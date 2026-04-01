package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.DeleteCardButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.EditCardBackSideButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.EditCardCollectionButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.EditCardFrontSideButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.commands.result.RIResponse;
import com.github.dementev_alex_p.repeatit.training.TrainingService;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewCardHandler implements CommandHandler {

    private static final String TITLE_TEXT = """
            <strong>Карточка</strong>
            —————————————————————
            %s
            💡 Для редактирования нажмите на соответствующий значок ниже
            """;


    private final CardService cardService;
    private final TrainingService trainingService;


    @Override
    public CommandEnum getCommand() {
        return CommandEnum.VIEW_CARD;
    }

    @Override
    public ProcessingResult processCommand(MessageContext context) {

        final long cardId = CommandParameterUtils.extractCardId(context);
        final Card card = cardService.findCardById(cardId);
        final List<CommandLine> commandLines = List.of(
                createEditionCommandLine(card.getId()),
                new CommandLine(new DeleteCardButton(card.getId())),
                createBackButtonLine(context)
        );

        return new ProcessingResult(RIResponse.builder()
                .text(String.format(TITLE_TEXT, CardTextConverter.convertCardToTextForView(card)))
                .availableCommands(commandLines)
                .messageMetaInfo(String.valueOf(cardId))
                .build()
        );
    }

    private CommandLine createBackButtonLine(final MessageContext context) {
        final CommandEnum previousCommand = trainingService.findCurrentTraining(context.userId()).isPresent()
                ? CommandEnum.TRAINING
                : CommandEnum.VIEW_CARD_LIST;
        return new CommandLine(new BackButton(previousCommand));
    }

    private CommandLine createEditionCommandLine(final Long cardId) {
        return new CommandLine(
                new EditCardFrontSideButton(cardId),
                new EditCardBackSideButton(cardId),
                new EditCardCollectionButton(cardId)
        );
    }

}
