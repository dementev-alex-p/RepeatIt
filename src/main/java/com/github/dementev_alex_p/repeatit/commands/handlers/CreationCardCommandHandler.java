package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToSend;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.SkipBackSideButton;
import com.github.dementev_alex_p.repeatit.utils.CardUtils;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CreationCardCommandHandler implements CommandHandler {

    private final CardService cardService;
    private static final String CARD_ID_PARAM_NAME = "card_id";

    private static final String START_CREATION_TEXT = """
                    <strong>Создание карточки</strong>
                    Введите лицевую сторону карточки
                    
                    <code>💡 Для быстрого создания карточки просто введите и отправьте лицевую сторону карточки из любого пункта меню</code>
                    """;
    private static final String SUCCESS_CREATION_TEXT = "Карточка успешно создана!\n";
    private static final String CARD_CREATION_TEXT = "Создание карточки\n";

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.CREATE_CARD;
    }

    @Override
    public ProcessingResult processCommand(final AbsSender sender, final MessageContext context) {

        if (isStartCreationCard(context)) {
            return new ProcessingResult(new MessageToSend(START_CREATION_TEXT, new CommandLine(CommandEnum.START)));
        }

        if (isSkipBackSideCommand(context)) {
            final Card card = cardService.findCardById(Long.parseLong(context.commandParameters().get(CARD_ID_PARAM_NAME)));
            return finishCreation(card, null);
        }

        final String message = context.message().orElseThrow();
        final Optional<Card> creationCard = cardService.findDraftCardByUserId(context.userId());
        if (creationCard.isEmpty()) {
            final Card card = cardService.createCard(context.userId(), message);
            return new ProcessingResult(new MessageToSend(
                    CARD_CREATION_TEXT + CardUtils.convertCardToTextForView(card),
                    new CommandLine(new SkipBackSideButton(CommandEnum.CREATE_CARD, card.getId()))
            ));
        } else {
            return finishCreation(creationCard.get(), message);
        }
    }

    private boolean isStartCreationCard(final MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("action")).filter("start"::equals).isPresent();
    }

    private ProcessingResult finishCreation(final Card card, final String message) {
        final Card updatedCard = cardService.complitCreationCard(card, message);

        final List<CommandLine> commands = Stream.of(
                CommandButtonUtils.createForEditCardAfterCreation(updatedCard.getId()),
                CommandButtonUtils.createForCreationAnotherCard(),
                new CommandButton(CommandEnum.CARDS),
                new CommandButton(CommandEnum.START)
        )
                .map(CommandLine::new)
                .toList();
        return new ProcessingResult(new MessageToSend(
                SUCCESS_CREATION_TEXT + CardUtils.convertCardToTextForView(updatedCard),
                commands
        ));
    }

    private boolean isSkipBackSideCommand(final MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("action"))
                .filter(SkipBackSideButton.ACTION_VALUE::equals)
                .isPresent();
    }

}
