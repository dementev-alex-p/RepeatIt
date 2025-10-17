package com.github.dementev_alex_p.repeatit.tg_message;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String command;

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

    public TgMessage(Integer messageId, long userId, Long chatId, String commandCode, String text) {
        this.messageText = text;
        this.command = commandCode;
        this.chatId = chatId;
        this.userId = userId;
        this.tgMessageId = messageId;
        this.createdAt = LocalDateTime.now();
        this.isDeleted = false;
    }
}
