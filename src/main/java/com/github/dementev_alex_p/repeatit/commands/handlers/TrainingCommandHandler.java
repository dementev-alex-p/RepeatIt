package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.*;
import com.github.dementev_alex_p.repeatit.commands.result.*;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.training.Training;
import com.github.dementev_alex_p.repeatit.training.TrainingService;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.RecallScoreEnum;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.TrainingCard;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.TrainingCardService;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class TrainingCommandHandler implements CommandHandler {

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
    private static final String PROGRESS_ITEM_FILLED = "✅ ";
    private static final String PROGRESS_ITEM = "◻️";
    private static final String NOT_FOUND_CARDS_FOR_TRAINING = "Для начала тренировки необходимо добавить карточки";
    private static final String NEXT_CARD_TEXT = """
            Карточка %d
            —————————————————————
            %s
            """;
    private static final String END_TRAINING = """
            <strong>Тренировка завершена!</strong>
            —————————————————————
            Пройдено: %d/%d (%d%%)
            Из них:
            🚀 вспомнили сразу: %d
            ⏳ вспомнили с трудом: %d
            ❓ не удалось вспомнить: %d
            """;
    public static final String START_ACTION_CODE = "start";
    public static final String SHOW_BACK_SIDE = "show_back_side";

    public final TrainingService trainingService;
    public final TrainingCardService trainingCardService;
    public final CardService cardService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.TRAINING;
    }

    @Override
    public CommandResponse processCommand(final MessageContext context) {

        if (isStartTrainingCommands(context)) {
            return startTraining(context);
        }

        final Training training = trainingService.findCurrentTraining(context.userId())
                .orElseThrow(() -> new RuntimeException("Не удалось найти тренировку для завершения"));

        if (isFinishTrainingCommand(context)) {
            return finishTraining(training);
        }

        if (isShowBackSideCommand(context)) {
            return showBackSide(context, training);
        }

        scorePreviousCardIfRequired(training, context);

        final Optional<TrainingCard> nextCard = extractNextCard(training);

        if (nextCard.isEmpty()) {
            return finishTraining(training);
        }

        return continueTraining(training, nextCard.get());
    }

    private CommandResponse showBackSide(final MessageContext context, final Training training) {
        final long cardId = Long.parseLong(context.commandParameters().get(CommandParameterUtils.CARD_PARAMETER_CODE));
        final Card card = cardService.findCardById(cardId);

        final List<CommandLine> commandLines = Arrays.asList(
                new CommandLine(new ViewCardButton(cardId)),
                createScoreCommandLine(cardId)
        );
        final String newCardText = String.format(
                NEXT_CARD_TEXT,
                training.getTrainingCards().stream().filter(c -> c.getCardId() == cardId).findAny().orElseThrow().getOrderIndex(),
                CardTextConverter.forTraining(card, true)
        );

        return CommandResponse
                .builder()
                .text(newCardText)
                .availableCommands(commandLines)
                .build();
    }

    private boolean isShowBackSideCommand(final MessageContext context) {
        return CommandParameterUtils.extractNullableAction(context)
                .filter(action -> action.equals(SHOW_BACK_SIDE))
                .isPresent();
    }

    private boolean isStartTrainingCommands(final MessageContext context) {
        return CommandParameterUtils.extractNullableAction(context)
                .filter(action -> action.equals(START_ACTION_CODE))
                .isPresent();
    }

    private boolean isFinishTrainingCommand(final MessageContext context) {
        return CommandParameterUtils.extractNullableAction(context)
                .filter(action -> action.equals(FinishTrainingButton.ACTION_CODE))
                .isPresent();
    }

    private void scorePreviousCardIfRequired(final Training training, final MessageContext context) {
        Optional.ofNullable(context.commandParameters().get(CommandParameterUtils.CARD_PARAMETER_CODE))
                .map(Long::parseLong)
                .flatMap(cardId -> extractTrainingCardById(training, cardId))
                .ifPresent(previousCard ->
                        trainingCardService.scoreRecall(previousCard, extractRecallScore(context))
                );
    }

    private Optional<TrainingCard> extractTrainingCardById(final Training training, final long cardId) {
        return training.getTrainingCards()
                .stream()
                .filter(tc -> tc.getCardId() == cardId)
                .findAny();
    }

    private Optional<TrainingCard> extractNextCard(final Training training) {
        return training
                .getTrainingCards()
                .stream()
                .sorted(Comparator.comparingInt(TrainingCard::getOrderIndex))
                .filter(card -> card.getRecallScore() == null)
                .findFirst();
    }

    private CommandResponse continueTraining(final Training training, final TrainingCard trainingCard) {
        final Card card = cardService.findCardById(trainingCard.getCardId());

        final int currentOrderIndex = trainingCard.getOrderIndex() - 1;
        final int totalCardsCount = training.getTrainingCards().size();
        final int percentage = currentOrderIndex * 100 / totalCardsCount;

        final CommandResponse statisticMessage = CommandResponse
                .builder()
                .text(String.format(START_TRAINING, currentOrderIndex, totalCardsCount, percentage, createProgressBar(percentage)))
                .availableCommands(Collections.singletonList(new CommandLine(new FinishTrainingButton())))
                .build();

        return CommandResponse
                .builder()
                .text(String.format(NEXT_CARD_TEXT, trainingCard.getOrderIndex(), CardTextConverter.forTraining(card, false)))
                .availableCommands(createCommandLineForCard(card))
                .trainingStatisticMessage(statisticMessage)
                .build();

    }

    private String createProgressBar(final int progressPercentage) {
        final StringBuilder progress = new StringBuilder();
        for (int i = 1; i <= 10; i++) {
            if (i <= progressPercentage / 10) {
                progress.append(PROGRESS_ITEM_FILLED);
            } else {
                progress.append(PROGRESS_ITEM);
            }
        }
        return progress.toString();
    }

    private CommandResponse finishTraining(Training training) {
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
                scoredCards.size() * 100 / training.getTrainingCards().size(),
                statistic.getOrDefault(RecallScoreEnum.PERFECT_RECALL, 0L),
                statistic.getOrDefault(RecallScoreEnum.DIFFICULT_RECALL, 0L),
                statistic.getOrDefault(RecallScoreEnum.FAIL_RECALL, 0L)
        );
        return CommandResponse
                .builder()
                .text(statisticText)
                .availableCommands(List.of(new CommandLine(new CommandButton(CommandEnum.MAIN_MENU))))
                .isChatClearRequired(true)
                .build();
    }

    private CommandResponse startTraining(final MessageContext context) {
        final List<Card> cardsForTraining = findCardsForTraining(context.userId());
        if (CollectionUtils.isEmpty(cardsForTraining)) {
            return sendNotFoundCardResponse();
        }
        final Training training = trainingService.create(context.userId(), cardsForTraining);
        final long firstCardId = extractNextCard(training).map(TrainingCard::getCardId).orElseThrow();
        final Card firstCard = cardsForTraining
                .stream()
                .filter(card -> card.getId().equals(firstCardId))
                .findAny()
                .orElseThrow();
        final int totalCardCount = training.getTrainingCards().size();

        final CommandResponse statisticMessage = CommandResponse
                .builder()
                .text(String.format(START_TRAINING, 0, totalCardCount, 0, createProgressBar(0)))
                .availableCommands(List.of(new CommandLine(new FinishTrainingButton())))
                .build();

       return CommandResponse
               .builder()
               .text(String.format(NEXT_CARD_TEXT, 1, CardTextConverter.forTraining(firstCard, false)))
               .availableCommands(createCommandLineForCard(firstCard))
               .trainingStatisticMessage(statisticMessage)
               .isChatClearRequired(true)
               .build();
    }

    private CommandResponse sendNotFoundCardResponse() {
        return CommandResponse
                .builder()
                .text(NOT_FOUND_CARDS_FOR_TRAINING)
                .availableCommands(List.of(
                        new CommandLine(new CommandButton(CommandEnum.ADD_CARD)),
                        new CommandLine(new CommandButton(CommandEnum.MAIN_MENU))
                ))
                .build();
    }

    private List<CommandLine> createCommandLineForCard(final Card card) {
        final List<CommandLine> commandLines = new ArrayList<>();
        if (card.getBackSide() != null) {
            commandLines.add(new CommandLine(new ShowBackSideButton(card.getId())));
        } else {
            commandLines.add(new CommandLine(new ViewCardButton(card.getId())));
        }
        commandLines.add(createScoreCommandLine(card.getId()));
        return commandLines;
    }

    private List<Card> findCardsForTraining(final long userId) {
        final List<Card> cardsForTraining = cardService.findCardsForDailyTraining(userId);
        if (cardsForTraining.isEmpty()) {
            return cardService.findCardsForExtraTraining(userId);
        }
        return cardsForTraining;
    }


    private CommandLine createScoreCommandLine(final long cardId) {
        return new CommandLine(Stream.of(
                        RecallScoreEnum.PERFECT_RECALL,
                        RecallScoreEnum.DIFFICULT_RECALL,
                        RecallScoreEnum.FAIL_RECALL
                )
                .map(score -> (CommandButton) new ScoreButton(score, cardId))
                .toList()
        );
    }

    private RecallScoreEnum extractRecallScore(MessageContext context) {
        return Optional.ofNullable(context.commandParameters().get("score"))
                .map(RecallScoreEnum::valueOf)
                .orElseThrow();
    }
}
