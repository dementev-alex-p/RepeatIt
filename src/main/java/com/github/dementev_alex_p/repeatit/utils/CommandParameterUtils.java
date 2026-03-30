package com.github.dementev_alex_p.repeatit.utils;

import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;

import java.util.Optional;

public class CommandParameterUtils {

    public static final String ACTION_PARAMETER_CODE = "action";
    public static final String PAGE_PARAMETER_CODE = "page";
    public static final String CARD_PARAMETER_CODE = "card_id";
    public static final String COLLECTION_PARAMETER_CODE = "card_collection_id";
    public static final String LAST_MESSAGE_META_INFO = "last_message_meta_info";

    public static CommandParameter createCardIdParameter(final long cardId) {
        return new CommandParameter(CARD_PARAMETER_CODE, String.valueOf(cardId));
    }

    public static CommandParameter createCollectionIdParameter(final long collectionId) {
        return new CommandParameter(COLLECTION_PARAMETER_CODE, String.valueOf(collectionId));
    }

    public static CommandParameter createActionParameter(final String action) {
        return new CommandParameter(ACTION_PARAMETER_CODE, action);
    }

    public static CommandParameter createPageParameter(final int page) {
        return new CommandParameter(PAGE_PARAMETER_CODE, String.valueOf(page));
    }

    public static Optional<String> extractNullableAction(final MessageContext context) {
        return Optional
                .ofNullable(context.commandParameters())
                .map(parameters -> parameters.get(ACTION_PARAMETER_CODE));
    }

    public static long extractCollectionId(final MessageContext context) {
        return Optional
                .ofNullable(context.commandParameters().get(COLLECTION_PARAMETER_CODE))
                .map(Long::parseLong)
                .orElseThrow();
    }

    public static int extractPage(final MessageContext context) {
        return Optional
                .ofNullable(context.commandParameters().get(PAGE_PARAMETER_CODE))
                .map(Integer::parseInt)
                .orElse(1);
    }

    public static long extractCardId(final MessageContext context) {
        return Optional
                .ofNullable(context.commandParameters().get(CARD_PARAMETER_CODE))
                .map(Long::parseLong)
                .orElseThrow();
    }

    public static String extractLastMessageMetaInfo(final MessageContext context) {
        return Optional
                .ofNullable(context.commandParameters().get(LAST_MESSAGE_META_INFO))
                .orElseThrow();
    }
}
