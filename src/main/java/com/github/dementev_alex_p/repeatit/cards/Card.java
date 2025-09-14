package com.github.dementev_alex_p.repeatit.cards;

import com.github.dementev_alex_p.repeatit.cards.collection.CardCollection;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "card")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(AccessLevel.PRIVATE)
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    @Column(name = "user_id")
    @NotNull
    private Long userId;

    @With
    @Column(name = "front_side")
    private String name;

    @With
    @Column(name = "back_side")
    private String description;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "card_collection_id")
    @Nullable
    private Long cardCollectionId;

    public Card(Long userId) {
        this.userId = userId;
    }

    public Card(final String name, final String description, final long userId, final long cardCollectionId) {
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.cardCollectionId = cardCollectionId;
    }

}
