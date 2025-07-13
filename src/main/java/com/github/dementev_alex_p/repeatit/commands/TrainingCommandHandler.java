package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.user_states.TrainingAdditionData;
import com.github.dementev_alex_p.repeatit.user_states.UserState;
import com.github.dementev_alex_p.repeatit.user_states.UserStatesService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class TrainingCommandHandler implements CommandHandler {
    public final UserStatesService userStatesService;
    public final CardService cardService;
    private static final Pair<String, String> YES_ANSWER = Pair.of("yes", "✅");
    private static final Pair<String, String> NO_ANSWER = Pair.of("no", "❌");

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.TRAINING;
    }

    @Override
    public void handleCommand(AbsSender sender, Update update) throws TelegramApiException {
        final Long userId = update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom().getId()
                : update.getMessage().getFrom().getId();

        final Long chatId = update.hasCallbackQuery()
                ? update.getCallbackQuery().getMessage().getChatId()
                : update.getMessage().getChatId();

        final UserState userState = userStatesService.getStateByUserId(userId);
        if (userState == null) {
            final List<Card> userCards = cardService.findByUserId(userId);
            if (userCards.isEmpty()) {
                throw new TelegramApiException("У пользователя нет карточек");//TODO Заменить на ответное сособщение
            }
            final UserState state = new UserState(userId, CommandEnum.TRAINING, new TrainingAdditionData(userCards));
            userStatesService.addState(state);
            sender.execute(SendMessage.builder().chatId(chatId).text("Начнем тренировку").build());
            sendNextCard(state, chatId, sender);
            return;
        }

        if (userState.getCurrentState() != CommandEnum.TRAINING) {
            throw new TelegramApiException("You are not in a training state");
        }
        final TrainingAdditionData trainingPlan = (TrainingAdditionData) userState.getAdditionalData();
        final Card previousCard = trainingPlan.getPlan().remove(0); //todo обратока ответа от пользоватетя по предыдущей карте
        if (StringUtils.substringAfter(update.getCallbackQuery().getData(), "?").equals(YES_ANSWER.getKey())){
            trainingPlan.getSuccessNumber().incrementAndGet();
        } else {
            trainingPlan.getFailNumber().incrementAndGet();
        }


        trainingPlan.getCurrentNumber().incrementAndGet();
        sendNextCard(userState, chatId, sender);
    }

    private void sendNextCard(UserState state, Long chatId, AbsSender sender) throws TelegramApiException {
        final TrainingAdditionData trainingPlan = (TrainingAdditionData) state.getAdditionalData();

        if (trainingPlan.getPlan().isEmpty()) {
            final SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text(String.format("Тренировка завершена! \n Правильных :%d, Неправильных %d", trainingPlan.getSuccessNumber().get(), trainingPlan.getFailNumber().get()))
                    .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(Collections.singletonList(InlineKeyboardButton.builder().text("Вернуться в главное меню").callbackData("/start").build())).build())
                    .build();
            userStatesService.removeState(state.getUserId());
            sender.execute(sendMessage);
            return;
        }
        final Card nextCard = trainingPlan.getPlan().iterator().next();
        final String nextCardText = String.format("%s --> <tg-spoiler>%s</tg-spoiler>", nextCard.getName(), nextCard.getDescription());

        final SendMessage sendMessage = SendMessage
                .builder()
                .chatId(chatId)
                .text(String.format(
                        "\n Карточка %d/%d\n%s",
                        trainingPlan.getCurrentNumber().get(),
                        trainingPlan.getTotalNumber(),
                        nextCardText
                ))
                .parseMode("HTML")
                .replyMarkup(createInlineKeyboard())
                .build();

        sender.execute(sendMessage);
    }

    private ReplyKeyboard createInlineKeyboard() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(Stream.of(YES_ANSWER, NO_ANSWER).map( answer ->
                        InlineKeyboardButton
                                .builder()
                                .text(answer.getValue())
                                .callbackData(String.format("%s?%s", getCommand().getCode(), answer.getKey()))
                                .build()
                )
                .collect(Collectors.toList()))
                .build();
    }
}
