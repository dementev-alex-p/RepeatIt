package com.github.dementev_alex_p.repeatit.tg_message;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tg_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TgMessage {
    @Id
    @Column(name = "tg_message_id")
    @EqualsAndHashCode.Include
    @ToString.Exclude
    private int tgMessageId;

    @Column(name = "user_id")
    @ToString.Exclude
    private long userId;

    @Column(name = "chat_id")
    @ToString.Exclude
    private long chatId;

    @Column(name = "command")
    @NotNull
    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    private CommandEnum command;

    @Column(name = "message_text")
    @NotNull
    @ToString.Exclude
    private String messageText;

    @Column(name = "created_at")
    @NotNull
    @ToString.Exclude
    private LocalDateTime createdAt;

    @Column(name = "is_deleted")
    @ToString.Exclude
    private boolean isDeleted;

    @Column(name = "is_answer_excepted")
    @ToString.Exclude
    private boolean isAnswerExcepted;

    @Column(name = "message_meta_info")
    @ToString.Exclude
    private String messageMetaInfo;

    public TgMessage(int tgMessageId, long userId, Long chatId, CommandEnum command, String text, boolean isAnswerExcepted, String messageMetaInfo) {
        this.messageText = text;
        this.command = command;
        this.chatId = chatId;
        this.userId = userId;
        this.tgMessageId = tgMessageId;
        this.createdAt = LocalDateTime.now();
        this.isDeleted = false;
        this.isAnswerExcepted = isAnswerExcepted;
        this.messageMetaInfo = messageMetaInfo;
    }
}
