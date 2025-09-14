package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.result.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.user_states.TrainingAdditionData;
import com.github.dementev_alex_p.repeatit.user_states.UserState;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class TrainingCommandHandler implements CommandHandler {
    public final UserStatesService userStatesService;
    public final CardService cardService;
    private static final Pair<String, String> YES_ANSWER = Pair.of("yes", "✅");
    private static final Pair<String, String> NO_ANSWER = Pair.of("no", "❌");
    private static final String START_TRAINING = """
            Начнем тренировку!
            Карточек к изучению: %d.
            Карточка 1.
            """;
    private static final String CONTINUE_TRAINING = "Карточка %d/%d";
    private static final String END_TRAINING = """
            Тренировка завершена!
            Правильных :%d, Неправильных %d
            """;
    private static final String NEXT_CARD = "\n%s --> <tg-spoiler>%s</tg-spoiler>";

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.START_TRAINING;
    }

    @Override
    public CommandProcessingResult processCommand(AbsSender sender, MessageContext context) throws TelegramApiException {
        final Optional<UserState> userStateOpt = userStatesService.getStateByUserId(context.userId());
        if (userStateOpt.isEmpty()) {
            return startTraining(context);
        }
        final UserState userState = userStateOpt.get();
        if (userState.getCurrentCommand() != CommandEnum.START_TRAINING) {
            userStatesService.removeStateByUserId(context.userId());
        }
        final TrainingAdditionData trainingPlan = (TrainingAdditionData) userState.getAdditionalData();
        final Card previousCard = trainingPlan.getCardsForStudy().remove(0); //todo обратока ответа от пользоватетя по предыдущей карте
        if (isYesAnswer(context)) {
            trainingPlan.getSuccessNumber().incrementAndGet();
        } else {
            trainingPlan.getFailNumber().incrementAndGet();
        }
        trainingPlan.getCurrentNumber().incrementAndGet();

        if (trainingPlan.getCardsForStudy().isEmpty()) {
            return endTraining(trainingPlan);
        }

        return continueTraining(trainingPlan);
    }

    private CommandProcessingResult continueTraining(TrainingAdditionData trainingState) {
        final Card nextCard = trainingState.getCardsForStudy().iterator().next();
        final String nextCardText = String.format(NEXT_CARD, nextCard.getName(), nextCard.getDescription());
        final String continueTrainingText = String.format(
                CONTINUE_TRAINING, trainingState.getCurrentNumber().get(), trainingState.getTotalNumber()
        );
        return new CommandProcessingResult(
                continueTrainingText + nextCardText,
                createAvailableAnswers()
            );
    }

    private CommandProcessingResult endTraining(TrainingAdditionData trainingPlan) {
        return new CommandProcessingResult(
                String.format(END_TRAINING,  trainingPlan.getSuccessNumber().get(), trainingPlan.getFailNumber().get())
        );
    }

    private CommandProcessingResult startTraining(MessageContext context) {
        final List<Card> userCards = cardService.findByUserId(context.userId());
        if (userCards.isEmpty()) {
            throw new RuntimeException("У пользователя нет карточек");//TODO Заменить на ответ с предложением завести карточки
        }
        final UserState state = new UserState(
                context.userId(), CommandEnum.START_TRAINING, new TrainingAdditionData(userCards)
        );
        userStatesService.addState(state);
        final Card firstCard = userCards.iterator().next();

        final String message = String.format(START_TRAINING, userCards.size())
                + String.format(NEXT_CARD, firstCard.getName(), firstCard.getDescription());

        return new CommandProcessingResult(message, createAvailableAnswers());
    }

    private List<CommandLine> createAvailableAnswers() {
        return Collections.singletonList(new CommandLine(
                Stream.of(YES_ANSWER, NO_ANSWER)
                        .map(answer -> new CommandButton(CommandEnum.START_TRAINING, answer.getRight(), answer.getLeft()))
                        .toList()
        ));
    }

    private boolean isYesAnswer(MessageContext context) {
        return StringUtils.substringAfter(context.data().orElseThrow(), "?").equals(YES_ANSWER.getKey());
    }
}
