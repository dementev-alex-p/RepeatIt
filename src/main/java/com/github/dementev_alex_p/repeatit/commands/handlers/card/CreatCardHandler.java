package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.SkipBackSideButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.commands.result.RIResponse;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessage;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CreatCardHandler implements CommandHandler {

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

    private final CardService cardService;
    private final TgMessageService tgMessageService;
    private final ViewCardHandler viewCardHandler;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.CREATE_CARD;
    }

    @Override
    public ProcessingResult processCommand(final MessageContext context) {
        if (isStartCreationCard(context)) {
            return startCreationCard();
        }
        if (isSkipBackSideCommand(context)) {
            return skipBackSide(context);
        }
        final String message = context.message().orElseThrow();
        final Optional<Long> createdCardId = getCreatedCardIdFromLastMessage(context);
        //Если предыдущее сообщение не содержало id карты, то значит карточка еще не создана и текущий ввод - это лицевая сторона
        boolean isFrontSideInput = createdCardId.isEmpty();
        if (isFrontSideInput) {
            return createCardWithFrontSide(context, message);
        }
        return saveCardBackSide(context, createdCardId.get(), message);
    }

    private Optional<Long> getCreatedCardIdFromLastMessage(final MessageContext context) {
        return tgMessageService.findLastByUserId(context.userId())
                .filter(m -> m.getCommand() == CommandEnum.CREATE_CARD)
                .map(TgMessage::getMessageMetaInfo)
                .map(Long::parseLong);
    }

    private ProcessingResult skipBackSide(final MessageContext context) {
        final ProcessingResult response = viewCardHandler.processCommand(context);
        return response.withAlter(FINISH_CREATION_TEXT);
    }

    private ProcessingResult createCardWithFrontSide(final MessageContext context, final String frontSideText) {
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
                .isAnswerExcepted(true)
                .messageMetaInfo(card.getId().toString())
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
                .isAnswerExcepted(true)
                .build()
        );
    }

    private boolean isStartCreationCard(final MessageContext context) {
        return CommandParameterUtils.extractNullableAction(context).filter("start"::equals).isPresent();
    }

    private ProcessingResult saveCardBackSide(final MessageContext context, final long cardId, final String backSide) {
        cardService.updateBackSideByCardId(cardId, backSide);
        context.commandParameters().put(CommandParameterUtils.CARD_PARAMETER_CODE, String.valueOf(cardId));
        final MessageContext newContext = context.withCommand(CommandEnum.VIEW_CARD);
        final ProcessingResult response = viewCardHandler.processCommand(newContext);
        return response.withAlter(FINISH_CREATION_TEXT);
    }



    private boolean isSkipBackSideCommand(final MessageContext context) {
        return CommandParameterUtils.extractNullableAction(context)
                .filter(SkipBackSideButton.ACTION_VALUE::equals)
                .isPresent();
    }

}
