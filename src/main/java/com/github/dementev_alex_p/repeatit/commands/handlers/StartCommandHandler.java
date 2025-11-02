package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToSend;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.users.User;
import com.github.dementev_alex_p.repeatit.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

    private final UserService userService;
    private final CardService cardService;

    private static final String GREETING = """
            %s, приветствую!
            """;
    private static final String STREAK = """
            Ударный режим: %d
            """;
    private static final String DAILY_CARD_COUNT = """
            Сегодня карточек к повторению: %d
            """;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.START;
    }

    @Override
    public ProcessingResult processCommand(final AbsSender sender, final MessageContext context) {
        if (userService.findUserById(context.userId()).isEmpty()) {
            userService.saveUser(new User(
                    context.userId(),
                    context.userName()
            ));
            return new ProcessingResult(new MessageToSend(
                    String.format(GREETING, context.userName()),
                    new CommandLine(CommandEnum.TRAINING),
                    new CommandLine(CommandEnum.CARDS),
                    new CommandLine(CommandEnum.SETTINGS)
            ));
        }
        final int countForDailyTraining = cardService.findCountForDailyTrainingByUserId(context.userId());

        return new ProcessingResult(new MessageToSend(
                String.format(GREETING, context.userName()) + String.format(DAILY_CARD_COUNT, countForDailyTraining),
                new CommandLine(CommandEnum.TRAINING),
                new CommandLine(CommandEnum.CARDS),
                new CommandLine(CommandEnum.SETTINGS)
        ));
    }


}
