package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToEdit;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToSend;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.HideBackSideButton;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.ShowBackSideButton;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessage;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import com.github.dementev_alex_p.repeatit.training.Training;
import com.github.dementev_alex_p.repeatit.training.TrainingService;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.RecallScoreEnum;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.TrainingCard;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.TrainingCardService;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class TrainingCommandHandler implements CommandHandler {
    public final TrainingService trainingService;
    public final TrainingCardService trainingCardService;
    public final CardService cardService;
    public final TgMessageService tgMessageService;
    private static final String START_TRAINING = """
            <strong>Тренировка</strong>
            —————————————————————
            Постарайтесь вспомнить карточку и оцените результат:
            🚀 - Сразу вспомнилось
            ⏳ - Вспомнилось с трудом
            ❓ - Не удалось вспомнить
            
            Прогресс: %d/%d (%d%%)
            %s
            """;
    private static final String PROGRESS_ITEM_FULLED = "✅ ";
    private static final String PROGRESS_ITEM = "◻️";
    private static final String NOT_FOUND_CARDS_FOR_TRAINING = "Для начала тренировки необходимо добавить карточки";
    private static final String NEXT_CARD_TEXT = """
            Карточка %d
            —————————————————————
            %s
            """;
    private static final String END_TRAINING = """
            <strong>Тренировка завершена!</strong>
            Статистика:
             - всего повторили: %d/%d
             - вспомнили: %d
             - вспомнили с трудом: %d
             - не удалось вспомнить: %d
            """;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.TRAINING;
    }

    @Transactional
    @Override
    public ProcessingResult processCommand(final AbsSender sender, final MessageContext context) {

        if (isStartTrainingCommands(context)) {
            return startTraining(context);
        }

        final Training training = trainingService.findCurrentTrainig(context.userId()).orElseThrow();

        if (isFinishTrainingCommand(context)) {
            return finishTraining(training);
        }

        if (isShowBackSideCommand(context)) {
            return showBackSide(context, training, true);
        }
        if (isHideBackSideCommand(context)) {
            return showBackSide(context, training, false);
        }

        scorePreviousCardIfRequired(training, context);

        final Optional<TrainingCard> nextCard = extractNextCard(training);

        if (nextCard.isEmpty()) {
            return finishTraining(training);
        }

        return continueTraining(training, nextCard.get());
    }

    private ProcessingResult showBackSide(final MessageContext context, final Training training, final boolean isShowBackSide) {
        final long cardId = Long.parseLong(context.commandParameters().get("card_id"));
        final Card card = cardService.findCardById(cardId);
        return new ProcessingResult(
                Collections.emptyList(),
                Collections.singletonList(
                        new MessageToEdit(
                                tgMessageService.findLastByUserId(training.getUserId()).getTgMessageId(),
                                String.format(
                                        NEXT_CARD_TEXT,
                                        training.getTrainingCards().stream().filter(c -> c.getCardId() == cardId).findAny().orElseThrow().getOrderIndex(),
                                        CardTextConverter.forTraining(card, isShowBackSide)
                                ),
                                createAvailableScoreForCard(cardId, isShowBackSide),
                                false
                        )
                ),
                Collections.emptyList()
        );
    }

    private boolean isHideBackSideCommand(final MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("action"))
                .filter(action -> action.equals("hide_back_side"))
                .isPresent();
    }

    private boolean isShowBackSideCommand(final MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("action"))
                .filter(action -> action.equals("show_back_side"))
                .isPresent();
    }

    private boolean isStartTrainingCommands(final MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("action"))
                .filter(action -> action.equals("start"))
                .isPresent();
    }

    private boolean isFinishTrainingCommand(final MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("action"))
                .filter(action -> action.equals("end"))
                .isPresent();
    }

    private void scorePreviousCardIfRequired(Training training, MessageContext context) {
        Optional.ofNullable(context.commandParameters().get("card_id"))
                .map(Long::parseLong)
                .map(cardId -> extractTrainingCardById(training, cardId))
                .ifPresent(previousCard ->
                        trainingCardService.scoreRecall(previousCard, extractRecallScore(context))
                );
    }

    private TrainingCard extractTrainingCardById(final Training training, final long cardId) {
        return training.getTrainingCards()
                .stream()
                .filter(tc -> tc.getCardId() == cardId)
                .findAny()
                .orElseThrow();
    }

    private Optional<TrainingCard> extractNextCard(final Training training) {
        return training
                .getTrainingCards()
                .stream()
                .sorted(Comparator.comparingInt(TrainingCard::getOrderIndex))
                .filter(card -> card.getRecallScore() == null)
                .findFirst();
    }

    private ProcessingResult continueTraining(final Training training, final TrainingCard trainingCard) {
        final Card card = cardService.findCardById(trainingCard.getCardId());

        final List<CommandLine> commandLines = new ArrayList<>(
                createAvailableScoreForCard(trainingCard.getCardId(), false)
        );
        final List<TgMessage> previousMessages = tgMessageService
                .findNotDeletedByUserIdAndCommand(training.getUserId(), getCommand())
                .stream()
                .sorted(Comparator.comparing(TgMessage::getCreatedAt))
                .toList();

        final int currentOrderIndex = trainingCard.getOrderIndex() - 1;
        final int totalCardsCount = training.getTrainingCards().size();
        final int percentage = currentOrderIndex * 100 / totalCardsCount;
        final MessageToEdit statisticMessage = new MessageToEdit(
                previousMessages.get(previousMessages.size() - 2).getTgMessageId(),
                String.format(START_TRAINING, currentOrderIndex, totalCardsCount, percentage, createProgressBar(percentage)),
                Collections.singletonList(new CommandLine(
                        new CommandButton(
                                CommandEnum.TRAINING,
                                "Завершить тренировку",
                                CommandButtonUtils.createActionParameter("end"))
                )),
                false
        );

        final MessageToEdit newCardMessage = new MessageToEdit(
                previousMessages.get(previousMessages.size() - 1).getTgMessageId(),
                String.format(NEXT_CARD_TEXT, trainingCard.getOrderIndex(), CardTextConverter.forTraining(card, false)),
                commandLines,
                false
        );
        return new ProcessingResult(
                Collections.emptyList(),
                Arrays.asList(newCardMessage, statisticMessage),
                Collections.emptyList()
        );
    }

    private String createProgressBar(final int progressPercentage) {
        final StringBuilder progress = new StringBuilder();
        for (int i = 1; i <= 10; i++) {
            if (i <= progressPercentage/10) {
                progress.append(PROGRESS_ITEM_FULLED);
            } else {
                progress.append(PROGRESS_ITEM);
            }
        }
        return progress.toString();
    }

    private ProcessingResult finishTraining(Training training) {
        trainingService.finishTraining(training);
        final List<TrainingCard> scoredCards = training
                .getTrainingCards()
                .stream()
                .filter(trainingCard -> trainingCard.getRecallScore() != null)
                .toList();
        final Map<RecallScoreEnum, Long> statistic = scoredCards
                .stream()
                .collect(Collectors.groupingBy(TrainingCard::getRecallScore, Collectors.counting()));

        final String statisticText = String.format(
                END_TRAINING,
                scoredCards.size(),
                training.getTrainingCards().size(),
                statistic.getOrDefault(RecallScoreEnum.PERFECT_RECALL, 0L),
                statistic.getOrDefault(RecallScoreEnum.DIFFICULT_RECALL, 0L),
                statistic.getOrDefault(RecallScoreEnum.FAIL_RECALL, 0L)
        );
        final MessageToSend messageToSend = new MessageToSend(
                statisticText, new CommandLine(CommandEnum.START), new CommandLine(CommandEnum.TRAINING)
        );

        return new ProcessingResult(
                Collections.singletonList(messageToSend),
                Collections.emptyList(),
                tgMessageService.findMessageIdsForDeletion(training.getUserId())
        );
    }

    private ProcessingResult startTraining(final MessageContext context) {
        final List<Card> cardsForTraining = findCardsForTraining(context.userId());
        if (CollectionUtils.isEmpty(cardsForTraining)) {
            return new ProcessingResult(new MessageToSend(
                    NOT_FOUND_CARDS_FOR_TRAINING,
                    new CommandLine(CommandEnum.ADD_CARD),
                    new CommandLine(CommandEnum.START)
            ));
        }

        final Training training = trainingService.create(context.userId(), cardsForTraining);
        final long firstCardId = extractNextCard(training).map(TrainingCard::getCardId).orElseThrow();
        final Card firstCard = cardsForTraining
                .stream()
                .filter(card -> card.getId().equals(firstCardId))
                .findAny()
                .orElseThrow();
        final int totalCardCount = training.getTrainingCards().size();
        final MessageToSend startTrainingMessage = new MessageToSend(
                String.format(START_TRAINING, 0, totalCardCount, 0, createProgressBar(0)),
                new CommandLine(
                        new CommandButton(
                                CommandEnum.TRAINING,
                                "Завершить тренировку",
                                CommandButtonUtils.createActionParameter("end"))
                )
        );
        final MessageToSend firstCardMessage = new MessageToSend(
                String.format(NEXT_CARD_TEXT, 1, CardTextConverter.forTraining(firstCard, false)),
                createAvailableScoreForCard(firstCardId, false)
        );


        return new ProcessingResult(
                List.of(startTrainingMessage, firstCardMessage),
                Collections.emptyList(),
                tgMessageService.findMessageIdsForDeletion(context.userId())
        );
    }

    private List<Card> findCardsForTraining(final long userId) {
        final List<Card> cardsForTraining = cardService.findCardsForDailyTraining(userId);
        if (cardsForTraining.isEmpty()) {
            return cardService.findCardsForExtraTraining(userId);
        }
        return cardsForTraining;
    }


    private List<CommandLine> createAvailableScoreForCard(final long cardId, final boolean isShowBackSide) {
        final List<CommandButton> scores = Stream.of(RecallScoreEnum.PERFECT_RECALL, RecallScoreEnum.DIFFICULT_RECALL, RecallScoreEnum.FAIL_RECALL)
                .map(score -> new CommandButton(
                        CommandEnum.TRAINING,
                        score.getText(),
                        Arrays.asList(
                                new CommandParameter("score", score.name()),
                                CommandButtonUtils.createCardIdParameter(cardId)
                        )
                ))
                .toList();
        final List<CommandLine> commandLines = new ArrayList<>();
        if (isShowBackSide) {
            commandLines.add(new CommandLine(new HideBackSideButton(cardId)));
        } else {
            commandLines.add(new CommandLine(new ShowBackSideButton(cardId)));
        }
        commandLines.add(new CommandLine(scores));
//        commandLines.add(new CommandLine(
//                new CommandButton(
//                        CommandEnum.TRAINING,
//                        "Завершить тренировку",
//                        CommandButtonUtils.createActionParameter("end"))
//        ));
        return commandLines;

    }

    private RecallScoreEnum extractRecallScore(MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("score"))
                .map(RecallScoreEnum::valueOf)
                .orElseThrow();
    }
}
