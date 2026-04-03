package com.github.dementev_alex_p.repeatit.commands.result;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import lombok.*;

import java.util.List;

@With
@Builder
@AllArgsConstructor
@Getter
public class CommandResponse {

    /**
     * Текст сообщения, который будет отображен пользователю. Обязательный параметр. На каждую команду бот возвращает ответ.
     */
    @NonNull
    private final String text;
    /**
     * Доступные команды. Каждое сообщение сопровождается кнопками, которые группируются в линии.
     */
    @NonNull
    private final List<CommandLine> availableCommands;
    /**
     * Признак того, что бот ждет текстового ответа от пользователя на это сообщение.
     */
    private final boolean isAnswerExcepted;
    /**
     * Команда, которая была обработана
     * Если null, то в базе сохранится команда из контекста.
     * Целесообразно задавать при перенаправлении с одной команды на другую.
     */
    private final CommandEnum command;
    /**
     * Параметры команды
     * Если null, то в базе сохранятся параметры команды из контекста.
     * Целесообразно задавать при перенаправлении с одной команды на другую
     */
    private final List<CommandParameter> parameters;
    /**
     * Если true, то ответ пользователю будет отправлен в виде нового сообщения
     * Если false, то ответ будет доставлен в виде изменения предыдущего сообщения (если это технически возможно)
     */
    private final boolean isNewMessage;
    /**
     * Тренировка - единственный режим в котором нарушается паттерн взаимодействия с пользователем через одно сообщение.
     * Ответ на команды в режиме тренировки подразумевает дополнительное сообщение со статистикой тренировки
     */
    private final CommandResponse trainingStatisticMessage;
    /**
     * Текст алерта будет отображен пользователю во всплывающем окне. Используется для информирования пользователя.
     */
    private final String alter;
    /**
     * Признак необходимости в чистке чата перед отправкой сообщений
     */
    private final boolean isChatClearRequired;


}
