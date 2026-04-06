package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.StartTrainingButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.users.User;
import com.github.dementev_alex_p.repeatit.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ViewMainMenuHandler implements CommandHandler {

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
        return CommandEnum.MAIN_MENU;
    }

    @Override
    public CommandResponse processCommand(final MessageContext context) {
        if (userService.findUserById(context.userId()).isEmpty()) {
            userService.saveUser(new User(
                    context.userId(),
                    context.userName()
            ));
            final List<CommandLine> commandLines = List.of(
                    new CommandLine(new StartTrainingButton()),
                    new CommandLine(CommandEnum.VIEW_CARD_LIST),
                    new CommandLine(CommandEnum.VIEW_COLLECTION_LIST)
            );
            return CommandResponse
                    .builder()
                    .text(String.format(GREETING, context.userName()))
                    .availableCommands(commandLines)
                    .build();
        }
        final int countForDailyTraining = cardService.findCountForDailyTrainingByUserId(context.userId());
        final List<CommandLine> commandLines = List.of(new CommandLine(new StartTrainingButton()),
                new CommandLine(CommandEnum.VIEW_CARD_LIST),
                new CommandLine(CommandEnum.VIEW_COLLECTION_LIST)
        );
        return CommandResponse
                .builder()
                .text(String.format(GREETING, context.userName()) + String.format(DAILY_CARD_COUNT, countForDailyTraining))
                .availableCommands(commandLines)
                .isChatClearRequired(true)
                .build();
    }


}
