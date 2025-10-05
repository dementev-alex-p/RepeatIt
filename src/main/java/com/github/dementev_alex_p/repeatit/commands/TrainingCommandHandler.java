package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.result.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.training.Training;
import com.github.dementev_alex_p.repeatit.training.TrainingService;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.RecallScoreEnum;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.TrainingCard;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.TrainingCardService;
import com.github.dementev_alex_p.repeatit.user_states.UserState;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class TrainingCommandHandler implements CommandHandler {
    public final UserStatesService userStatesService;
    public final TrainingService trainingService;
    public final TrainingCardService trainingCardService;
    public final CardService cardService;
    private static final String START_TRAINING = """
            Начнем тренировку!
            Карточек к изучению: %d.
            Карточка 1.
            """;
    private static final String CONTINUE_TRAINING = "Карточка %d/%d";
    private static final String END_TRAINING = """
            Тренировка завершена!
            Статистика:
             - всего повторили: %d/%d
             - вспомнили: %d
             - вспомнили с трудом: %d
             - не удалось вспомнить: %d
            """;
    private static final String NEXT_CARD = "\n%s --> <tg-spoiler>%s</tg-spoiler>";
    private static final String AVAILABLE_SCORE_PARAMETER = "score=%s&tc_id=%d";
    private static final String FINISH_TRAINING_PARAMETER = "action=end";

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.TRAINING;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(AbsSender sender, MessageContext context) {

        final boolean isNewTraining = CollectionUtils.isEmpty(context.commandParameters());
        final Training training = isNewTraining
                ? createTraining(context)
                : trainingService.findCurrentTrainig(context.userId()).orElseGet(() -> createTraining(context));

        if (isFinishTrainingCommand(context)) {
            return endTraining(training);
        }

        scorePreviousCardIfRequired(training, context);

        final Optional<TrainingCard> nextCard = extractNextCard(training);

        if (nextCard.isEmpty()) {
            return endTraining(training);
        }

        return continueTraining(training, nextCard.get());
    }

    private boolean isFinishTrainingCommand(final MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("action"))
                .filter(action -> action.equals("end"))
                .isPresent();
    }

    private void scorePreviousCardIfRequired(Training training, MessageContext context) {
        Optional.ofNullable(context.commandParameters().get("tc_id"))
                .map(Long::parseLong)
                .map(trainingCardId -> extractTrainingCardById(training, trainingCardId))
                .ifPresent(previousCard ->
                        trainingCardService.scoreRecall(previousCard, extractRecallScore(context))
                );
    }

    private TrainingCard extractTrainingCardById(Training training, long trainingCardId) {
        return training.getTrainingCards()
                .stream()
                .filter(tc -> tc.getTrainingCardId().equals(trainingCardId))
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

    private CommandProcessingResult continueTraining(final Training training, final TrainingCard trainingCard) {
        final Card card = cardService.findCardById(trainingCard.getCardId());
        final String nextCardText = String.format(NEXT_CARD, card.getFrontSide(), card.getBackSide());
        final String continueTrainingText = String.format(
                CONTINUE_TRAINING, trainingCard.getOrderIndex(), training.getTrainingCards().size()
        );
        final List<CommandLine> commandLines = new ArrayList<>(
                createAvailableScoreForCard(trainingCard.getTrainingCardId())
        );
        commandLines.add(createFinishTrainingCommand());
        return new CommandProcessingResult(
                continueTrainingText + nextCardText,
                commandLines
        );
    }

    private CommandProcessingResult endTraining(Training training) {
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
        return CommandProcessingResult.createWithVerticalButtons(statisticText, CommandEnum.START, CommandEnum.TRAINING);
    }

    private Training createTraining(MessageContext context) {
        final List<Card> userCards = cardService.findByUserId(context.userId());
        if (userCards.isEmpty()) {
            throw new RuntimeException("У пользователя нет карточек");//TODO Заменить на ответ с предложением завести карточки
        }
        return trainingService.create(context.userId(), userCards);

//        final Card firstCard = userCards.iterator().next();
//
//        final String message = String.format(START_TRAINING, userCards.size())
//                + String.format(NEXT_CARD, firstCard.getFrontSide(), firstCard.getBackSide());
//        final List<CommandLine> availableAnswers = createAvailableScoreForCard(firstCard.getId());
//        availableAnswers.add(createFinishTrainingCommand());
//        return new CommandProcessingResult(message, createAvailableScoreForCard(firstCard.getId()));
    }

    private List<CommandLine> createAvailableScoreForCard(final long id) {
        return Stream.of(RecallScoreEnum.PERFECT_RECALL, RecallScoreEnum.DIFFICULT_RECALL, RecallScoreEnum.FAIL_RECALL)
                .map(score -> new CommandButton(
                        CommandEnum.TRAINING,
                        score.getText(),
                        String.format(AVAILABLE_SCORE_PARAMETER, score.name(), id)
                )).map(CommandLine::new)
                .toList();
    }

    private CommandLine createFinishTrainingCommand() {
        return new CommandLine(
                new CommandButton(CommandEnum.TRAINING, "Завершить тренировку", FINISH_TRAINING_PARAMETER)
        );
    }

    private RecallScoreEnum extractRecallScore(MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("score"))
                .map(RecallScoreEnum::valueOf)
                .orElseThrow();
    }
}
