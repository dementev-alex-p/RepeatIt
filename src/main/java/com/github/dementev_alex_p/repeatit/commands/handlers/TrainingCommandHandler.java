package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.commands.result.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToSend;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.training.Training;
import com.github.dementev_alex_p.repeatit.training.TrainingService;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.RecallScoreEnum;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.TrainingCard;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.TrainingCardService;
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
    private static final String START_TRAINING = """
            Начнем тренировку!
            Карточек для повторения: %d
            Постарайтесь вспомнить карточку и оцените результат:
            😃 - Сразу вспомнил
            🤔 - Вспомнил с трудом
            😩 - Не вспомнил
            
            Карточка 1.
            %s
            """;
    private static final String NOT_FOUND_CARDS_FOR_TRAINING = "Для начала тренировки необходимо добавить карточки";
    private static final String CONTINUE_TRAINING = """
            Карточка %d/%d (%d%%)
            """;
    private static final String END_TRAINING = """
            Тренировка завершена!
            Статистика:
             - всего повторили: %d/%d
             - вспомнили: %d
             - вспомнили с трудом: %d
             - не удалось вспомнить: %d
            """;
    private static final String NEXT_CARD = "\n%s --> <tg-spoiler>%s</tg-spoiler>";

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
            return endTraining(training);
        }

        scorePreviousCardIfRequired(training, context);

        final Optional<TrainingCard> nextCard = extractNextCard(training);

        if (nextCard.isEmpty()) {
            return endTraining(training);
        }

        return continueTraining(training, nextCard.get());
    }

    private boolean isStartTrainingCommands(final MessageContext context) {
        return CollectionUtils.isEmpty(context.commandParameters());
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
        final String nextCardText = String.format(NEXT_CARD, card.getFrontSide(), card.getBackSide());
        final int percentage = trainingCard.getOrderIndex() * 100 / training.getTrainingCards().size();
        final String continueTrainingText = String.format(
                CONTINUE_TRAINING, trainingCard.getOrderIndex(), training.getTrainingCards().size(), percentage
        );
        final List<CommandLine> commandLines = new ArrayList<>(
                createAvailableScoreForCard(trainingCard.getCardId())
        );
        return new ProcessingResult(new MessageToSend(
                continueTrainingText + nextCardText,
                commandLines
        ));
    }

    private ProcessingResult endTraining(Training training) {
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
        return new ProcessingResult(new MessageToSend(
                statisticText, new CommandLine(CommandEnum.START), new CommandLine(CommandEnum.TRAINING)
        ));
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

        final String cardText = String.format(NEXT_CARD, firstCard.getFrontSide(), firstCard.getBackSide());

        return new ProcessingResult(new MessageToSend(
                String.format(START_TRAINING, training.getTrainingCards().size(), cardText),
                createAvailableScoreForCard(firstCardId)
        ));
    }

    private List<Card> findCardsForTraining(final long userId) {
        final List<Card> cardsForTraining = cardService.findCardsForDailyTraining(userId);
        if (cardsForTraining.isEmpty()) {
            return cardService.findCardsForExtraTraining(userId);
        }
        return cardsForTraining;
    }


    private List<CommandLine> createAvailableScoreForCard(final long cardId) {
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
        commandLines.add(new CommandLine(scores));
        commandLines.add(new CommandLine(
                new CommandButton(
                        CommandEnum.TRAINING,
                        "Завершить тренировку",
                        CommandButtonUtils.createActionParameter("end"))
        ));
        return commandLines;

    }

    private RecallScoreEnum extractRecallScore(MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("score"))
                .map(RecallScoreEnum::valueOf)
                .orElseThrow();
    }
}
