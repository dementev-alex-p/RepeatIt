package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToSend;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.commands.result.RIResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.tg_message.TgMessageService;
import com.github.dementev_alex_p.repeatit.users.User;
import com.github.dementev_alex_p.repeatit.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

    private final UserService userService;
    private final CardService cardService;
    private final TgMessageService tgMessageService;

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
    public ProcessingResult processCommand(final MessageContext context) {
        if (userService.findUserById(context.userId()).isEmpty()) {
            userService.saveUser(new User(
                    context.userId(),
                    context.userName()
            ));
            final List<CommandLine> commandLines = List.of(
                    new CommandLine(CommandEnum.TRAINING),
                    new CommandLine(CommandEnum.CARDS),
                    new CommandLine(CommandEnum.COLLECTIONS),
                    new CommandLine(CommandEnum.SETTINGS)
            );
            return new ProcessingResult(RIResponse
                    .builder()
                    .text(String.format(GREETING, context.userName()))
                    .availableCommands(commandLines)
                    .build());
        }
        final int countForDailyTraining = cardService.findCountForDailyTrainingByUserId(context.userId());

        return new ProcessingResult(
                Collections.singletonList(new MessageToSend(
                    String.format(GREETING, context.userName()) + String.format(DAILY_CARD_COUNT, countForDailyTraining),
                    new CommandLine(CommandEnum.TRAINING),
                    new CommandLine(CommandEnum.CARDS),
                    new CommandLine(CommandEnum.COLLECTIONS)
                )),
                Collections.emptyList(),
                tgMessageService.findMessageIdsForDeletion(context.userId())
        );
    }


}
