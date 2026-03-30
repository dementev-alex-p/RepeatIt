package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CreateAnotherCardButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.ViewCardButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.SkipBackSideButton;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CreationCardCommandHandler implements CommandHandler {

    private final CardService cardService;
    private final TgMessageService tgMessageService;

    private static final String TITLE_TEXT = """
            <strong>Создание карточки</strong>
            —————————————————————
            Карточка:
            %s
            %s
            """;
    private static final String FINISH_CREATION_TEXT = """
            ✅ Карточка успешно создана!
            """;
    private static final String WRITE_FRONT_SIDE = """
            ✍ <i>Введите <strong>обложку</strong>...</i>
          
            <code>💡 Для быстрого создания карточки из любого пункта меню, просто введите и отправьте мне ее обложку </code>
            """;
    private static final String WRITE_BACK_SIDE = "✅ Обложка сохранена!\n\n✍ <i>Введите <strong>содержание</strong>...</i>";


    @Override
    public CommandEnum getCommand() {
        return CommandEnum.CREATE_CARD;
    }

    @Override
    public ProcessingResult processCommand(final AbsSender sender, final MessageContext context) {
        if (isStartCreationCard(context)) {
            return startCreationCard();
        }
        if (isSkipBackSideCommand(context)) {
            return skipBackSide(context);
        }
        final String message = context.message().orElseThrow();
        final Optional<Card> creationCard = cardService.findDraftCardByUserId(context.userId());
        if (creationCard.isEmpty()) {
            return createCard(context, message);
        } else {
            return finishCreation(context, creationCard.get(), message);
        }
    }

    private ProcessingResult skipBackSide(final MessageContext context) {
        final long cardId = Long.parseLong(context.commandParameters().get(CommandParameterUtils.CARD_PARAMETER_CODE));
        final Card card = cardService.findCardById(cardId);
        return finishCreation(context, card, null);
    }

    private ProcessingResult createCard(final MessageContext context, final String frontSideText) {
        final Card card = cardService.createCard(context.userId(), frontSideText);
        final String startCreationText = String.format(
                TITLE_TEXT,
                CardTextConverter.convertForCreatingCard(frontSideText),
                WRITE_BACK_SIDE
        );
        final List<CommandLine> commandLines = List.of(
                new CommandLine(new SkipBackSideButton(CommandEnum.CREATE_CARD, card.getId()))
        );


        return new ProcessingResult(RIResponse
                .builder()
                .text(startCreationText)
                .availableCommands(commandLines)
                .build()
        );
    }

    private ProcessingResult startCreationCard() {

        final String startCreationText = String.format(
                TITLE_TEXT,
                CardTextConverter.convertForCreatingCard(null),
                WRITE_FRONT_SIDE
        );
        return new ProcessingResult(RIResponse.builder()
                .text(startCreationText)
                .availableCommands(List.of(new CommandLine(new BackButton(CommandEnum.ADD_CARD))))
                .build()
        );
    }

    private boolean isStartCreationCard(final MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("action")).filter("start"::equals).isPresent();
    }

    private ProcessingResult finishCreation(final MessageContext context, final Card card, final String backSide) {
        final Card updatedCard = cardService.complitCreationCard(card, backSide);


        final List<CommandLine> commands = createCommandsForFinishMessage(card);

        final String message = String.format(
                        TITLE_TEXT,
                        CardTextConverter.convertCardToTextForView(updatedCard),
                        FINISH_CREATION_TEXT
        );
        return new ProcessingResult(RIResponse
                .builder()
                .text(message)
                .availableCommands(commands)
                .build()
        );
    }

    private List<CommandLine> createCommandsForFinishMessage(final Card card) {
        final CommandLine firstLine = new CommandLine(
                new ViewCardButton(card.getId()),
                new CreateAnotherCardButton()
        );
        return Arrays.asList(
                firstLine,
                new CommandLine(CommandEnum.CARDS),
                new CommandLine(CommandEnum.START)
        );
    }

    private boolean isSkipBackSideCommand(final MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("action"))
                .filter(SkipBackSideButton.ACTION_VALUE::equals)
                .isPresent();
    }

}
