package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.collections.CardCollection;
import com.github.dementev_alex_p.repeatit.collections.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.SkipBackSideButton;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessage;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreatCardHandler implements CommandHandler {

    private static final String TITLE_TEXT = """
            <strong>Создание карточки</strong>
            —————————————————————
            Карточка:
            %s
            %s
            %s
            """;
    private static final String FINISH_CREATION_TEXT = """
            ✅ Карточка успешно создана!
            """;
    private static final String WRITE_FRONT_SIDE = """
            ✍ <i>Введите <strong>обложку</strong>...</i>
            """;
    private static final String WRITE_BACK_SIDE = "✅ Обложка сохранена!\n\n✍ <i>Введите <strong>содержание</strong>...</i>";
    private static final String HINT = """
        💡 Для быстрого создания карточки из любого пункта меню, просто введите и отправьте мне ее обложку
        Например:
        • Узнали информацию, которую хотите запомнить -> Переходите бота и просто присылайте ее. Бот увидит от вас сообщение и запустит режим создания карточки!
        • Во время тренировки вам пришла идея для карточки -> Опишите идею и присылайте. А после создания карточки вы сможете вернуться к тренировке!
        """;
    private static final String START_ACTION = "start";
    private final CardService cardService;
    private final TgMessageService tgMessageService;
    private final ViewCardHandler viewCardHandler;
    private final CardCollectionService cardCollectionService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.CREATE_CARD;
    }

    @Override
    public CommandResponse processCommand(final MessageContext context) {
        if (isStartCreationCard(context)) {
            return startCreationCard(context);
        }
        if (isSkipBackSideCommand(context)) {
            return skipBackSide(context);
        }
        final String message = context.message().orElseThrow();
        final Optional<TgMessage> lastMessage = tgMessageService.findLastEditableByUserId(context.userId());
        final Optional<Long> createdCardId = getCreatedCardIdFromLastMessage(lastMessage);
        //Если предыдущее сообщение не содержало id карты, то значит карточка еще не создана и текущий ввод - это лицевая сторона
        boolean isFrontSideInput = createdCardId.isEmpty();
        if (isFrontSideInput) {
            return createCardWithFrontSide(context, message, lastMessage);
        }
        return saveCardBackSide(context, createdCardId.get(), message);
    }

    private Optional<Long> getCreatedCardIdFromLastMessage(final Optional<TgMessage> lastMessage) {
        return lastMessage
                .filter(m -> m.getCommand() == CommandEnum.CREATE_CARD)
                .flatMap(message -> CommandParameterUtils.extractCardId(message.getCommandParameters()));
    }

    private CommandResponse skipBackSide(final MessageContext context) {
        final CommandResponse response = viewCardHandler.processCommand(context);
        return response
                .withAlter(FINISH_CREATION_TEXT)
                .withCommand(CommandEnum.VIEW_CARD);
    }

    private CommandResponse createCardWithFrontSide(final MessageContext context, final String frontSideText, final Optional<TgMessage> lastMessage) {
        final Optional<CardCollection> collection = lastMessage
                .map(TgMessage::getCommandParameters)
                .flatMap(CommandParameterUtils::extractNullableCollectionId)
                .map(cardCollectionService::findById);

        final Card card = cardService.createCard(context.userId(), frontSideText, collection);
        final String startCreationText = String.format(
                TITLE_TEXT,
                collection.isEmpty()
                        ? CardTextConverter.convertForCreatingCard(frontSideText)
                        : CardTextConverter.convertForCreatingCardWithCollection(frontSideText, collection.get()),
                WRITE_BACK_SIDE,
                ""
        );
        final List<CommandLine> commandLines = List.of(
                new CommandLine(new SkipBackSideButton(card.getId()))
        );


        return CommandResponse
                .builder()
                .text(startCreationText)
                .availableCommands(commandLines)
                .isAnswerExcepted(true)
                .parameters(List.of(CommandParameterUtils.createCardIdParameter(card.getId())))
                .build();
    }

    private CommandResponse startCreationCard(final MessageContext context) {
        final Optional<Long> chosenCollectionId = CommandParameterUtils.extractNullableCollectionId(context);
        if (chosenCollectionId.isPresent()) {
            return showCreationMessageWithCollection(context, chosenCollectionId.get());
        }
        final String startCreationText = String.format(
                TITLE_TEXT,
                CardTextConverter.convertForCreatingCard(null),
                WRITE_FRONT_SIDE,
                CommandParameterUtils.isViewHintRequired(context) ? HINT : ""
        );
        return CommandResponse
                .builder()
                .text(startCreationText)
                .availableCommands(addHintButtonIfRequired(context, List.of(new CommandLine(new BackButton()))))
                .isAnswerExcepted(true)
                .build();
    }

    private CommandResponse showCreationMessageWithCollection(final MessageContext context, final long chosenCollectionId) {
        final CardCollection collection = cardCollectionService.findById(chosenCollectionId);
        final String startCreationText = String.format(
                TITLE_TEXT,
                CardTextConverter.convertForCreatingCardWithCollection(null, collection),
                WRITE_FRONT_SIDE,
                CommandParameterUtils.isViewHintRequired(context) ? HINT : ""
        );
        return CommandResponse
                .builder()
                .text(startCreationText)
                .availableCommands(addHintButtonIfRequired(context, List.of(new CommandLine(new BackButton()))))
                .isAnswerExcepted(true)
                .build();
    }

    private boolean isStartCreationCard(final MessageContext context) {
        return context.message().isEmpty() &&
                CommandParameterUtils.extractNullableAction(context).filter(START_ACTION::equals).isPresent();
    }

    private CommandResponse saveCardBackSide(final MessageContext context, final long cardId, final String backSide) {
        cardService.updateBackSideByCardId(cardId, backSide);
        context.commandParameters().put(CommandParameterUtils.CARD_PARAMETER_CODE, String.valueOf(cardId));
        final MessageContext newContext = context.withCommand(CommandEnum.VIEW_CARD);
        final CommandResponse response = viewCardHandler.processCommand(newContext);
        return response
                .withAlter(FINISH_CREATION_TEXT)
                .withCommand(CommandEnum.VIEW_CARD)
                .withParameters(CommandParameterUtils.convert(context.commandParameters()));
    }


    private boolean isSkipBackSideCommand(final MessageContext context) {
        return CommandParameterUtils.extractNullableAction(context)
                .filter(SkipBackSideButton.ACTION_VALUE::equals)
                .isPresent();
    }

}
