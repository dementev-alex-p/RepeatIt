package com.github.dementev_alex_p.repeatit.tg_message;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TgMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private long messageId;

    @Column(name = "tg_message_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private int tgMessageId;

    @Column(name = "user_id")
    @ToString.Include
    private long userId;

    @Column(name = "chat_id")
    @ToString.Include
    private long chatId;

    @Column(name = "command")
    @NotNull
    @ToString.Include
    @Enumerated(EnumType.STRING)
    private CommandEnum command;

    @Column(name = "message_text")
    @NotNull
    @ToString.Include
    private String messageText;

    @Column(name = "created_at")
    @NotNull
    @ToString.Include
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    @ToString.Include
    private LocalDateTime deletedAt;

    @Column(name = "is_answer_excepted")
    @ToString.Include
    private boolean isAnswerExcepted;

    @Column(name = "is_chat_clear_required")
    @ToString.Include
    private boolean isChatClearRequired;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parameters", columnDefinition = "jsonb")
    @ToString.Include
    private List<CommandParameter> commandParameters;

    public TgMessage(int tgMessageId, long userId, long chatId, CommandEnum command, String text, boolean isAnswerExcepted, final List<CommandParameter> commandParameters, boolean isChatClearRequired) {
        this.tgMessageId = tgMessageId;
        this.messageText = text;
        this.command = command;
        this.chatId = chatId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.isAnswerExcepted = isAnswerExcepted;
        this.isChatClearRequired = isChatClearRequired;
        this.commandParameters = commandParameters;
    }
}
