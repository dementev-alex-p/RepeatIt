package com.github.dementev_alex_p.repeatit.commands.result;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.util.List;

/**
 * RepeatItResponse содержит информацию для ответа пользователю на его запрос
 * На уровне контроллера будет конвертирован в сообщение для пользователя
 */
@Getter
@Builder
public class RIResponse {

        private final String text;
        @With
        private final List<CommandLine> availableCommands;
        private final boolean isAnswerExcepted;
        /*
        Доп информация. Служит для сохранения контекста
        */
        private final String messageMetaInfo;

}
