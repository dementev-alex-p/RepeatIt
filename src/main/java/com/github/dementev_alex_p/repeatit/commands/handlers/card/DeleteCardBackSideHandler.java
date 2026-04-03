package com.github.dementev_alex_p.repeatit.commands.handlers.card;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.CommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteCardBackSideHandler implements CommandHandler {


    private final CardService cardService;
    private final ViewCardHandler viewCardHandler;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.DELETE_CARD_BACK_SIDE;
    }

    @Override
    public CommandResponse processCommand(MessageContext context) {
        final long cardId = CommandParameterUtils.extractCardId(context);
        cardService.updateBackSideByCardId(cardId, null);
        return viewCardHandler
                .processCommand(context)
                .withCommand(CommandEnum.VIEW_CARD);

    }
}
