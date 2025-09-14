package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.users.User;
import com.github.dementev_alex_p.repeatit.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

    private final UserService userService;

    private static final String GREETING = """
            %s, приветствую!
            """;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.START;
    }

    @Override
    public CommandProcessingResult processCommand(final AbsSender sender, final MessageContext context) throws TelegramApiException {
        if (userService.findUserById(context.userId()).isEmpty()) {
            userService.saveUser(new User(
                    context.userId(),
                    context.userName()
            ));
        }

        return new CommandProcessingResult(
                String.format(GREETING, context.userName()),
                Stream.of(CommandEnum.START_TRAINING, CommandEnum.ADD_CARD, CommandEnum.VIEW_CARDS)
                        .map(CommandLine::new)
                        .toList()
        );
    }


}
