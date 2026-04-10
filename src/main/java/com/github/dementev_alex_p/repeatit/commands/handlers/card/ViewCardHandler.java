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
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
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
            %s
            """;
    private static final String HINT = """
            Для редактирования нажмите
            📘 - изменить обложку
            📖 - изменить содержание
            📚 - изменить коллекцию
            
            Если эту карточку вы добавили из публичной коллекции, то не бойтесь ее редактировать. Ваши действия распространяются только на вашу карточку, публичная карточка останется без изменений.
            """;


    private final CardService cardService;


    @Override
    public CommandEnum getCommand() {
        return CommandEnum.VIEW_CARD;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {

        final long cardId = extractCardIdFromContext(context);
        final Card card = cardService.findCardById(cardId);
        final List<CommandLine> commandLines = List.of(
                createEditionCommandLine(card.getId()),
                new CommandLine(new DeleteCardButton(card.getId())),
                new CommandLine(new BackButton())
        );
        final String text = String.format(
                TITLE_TEXT,
                CardTextConverter.convertCardToTextForView(card),
                CommandParameterUtils.isViewHintRequired(context) ? HINT : ""
        );
        return CommandResponse
                .builder()
                .text(text)
                .availableCommands(addHintButtonIfRequired(context, commandLines))
                .build();
    }

    private long extractCardIdFromContext(final MessageContext context) {
        boolean isViewAfterSearch = context
                .message()
                .filter(m -> m.startsWith("/" + CommandEnum.VIEW_CARD.getCode()))
                .isPresent();
        if (isViewAfterSearch) {
            long cardId = Long.parseLong(context.message().get().substring(CommandEnum.VIEW_CARD.getCode().length() + 1).trim());
            context.commandParameters().put(CommandParameterUtils.CARD_PARAMETER_CODE, String.valueOf(cardId));
            return cardId;
        }
        return CommandParameterUtils.extractCardId(context);
    }



    private CommandLine createEditionCommandLine(final Long cardId) {
        return new CommandLine(
                new EditCardFrontSideButton(cardId),
                new EditCardBackSideButton(cardId),
                new EditCardCollectionButton(cardId)
        );
    }

}
