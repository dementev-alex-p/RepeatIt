package com.github.dementev_alex_p.repeatit.cards;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "card")
@Getter
@Setter(AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card {
    @Id
    @Column(name = "card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Exclude
    private Long id;

    @Column(name = "user_id")
    @NotNull
    @ToString.Exclude
    private Long userId;

    @Column(name = "card_collection_id")
    private Long cardCollectionId;

    @Column(name = "front_side")
    @ToString.Exclude
    private String frontSide;

    @Column(name = "back_side")
    @ToString.Exclude
    private String backSide;

    @Column(name = "streak")
    private int streak;

    @Column(name = "easiness_factor", columnDefinition = "numeric")
    private float easinessFactor;

    @Column(name = "interval_days")
    private int intervalDays;

    @Column(name = "next_repeat_date")
    private LocalDate nextRepeatDate = LocalDate.now();

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    protected Card(final String frontSide, final String backSide, final long userId, final long cardCollectionId, final CardStatus status) {
        this(userId, frontSide, status);
        this.backSide = backSide;
        this.cardCollectionId = cardCollectionId;
    }

    protected Card(final Long userId, final String frontSide, final CardStatus status) {
        this.userId = userId;
        this.frontSide = frontSide;
        this.status = status;
        streak = 0;
        easinessFactor = 2.5F;
        intervalDays = 0;
        nextRepeatDate = LocalDate.now();
    }

}
