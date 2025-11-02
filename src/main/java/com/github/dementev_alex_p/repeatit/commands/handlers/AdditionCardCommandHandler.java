package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.MessageToSend;
import com.github.dementev_alex_p.repeatit.commands.result.ProcessingResult;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Arrays;

@Service
public class AdditionCardCommandHandler implements CommandHandler {

    private static final String ADD_CARD_TEXT = """
            <strong>Добавление карточек</strong>
            - Для создания карточки нажмите "➕ Создать"
            - Для добавления карточек из публичных коллекций нажмите "📚 Коллекции"
            - Для импорта карточек  "📥 Импорт"
            """;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.ADD_CARD;
    }

    @Override
    public ProcessingResult processCommand(AbsSender sender, MessageContext context) {

        return new ProcessingResult(new MessageToSend(
                ADD_CARD_TEXT,
                Arrays.asList(
                        new CommandLine(Arrays.asList(
                                new CommandButton(CommandEnum.CREATE_CARD),
                                new CommandButton(CommandEnum.ADD_CARDS_FROM_COLLECTION),
                                new CommandButton(CommandEnum.IMPORT_CARDS)
                        )),
                        new CommandLine(new CommandButton(CommandEnum.START))
                )
        ));
    }
}
