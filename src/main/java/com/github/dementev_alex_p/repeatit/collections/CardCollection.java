package com.github.dementev_alex_p.repeatit.collections;

import com.github.dementev_alex_p.repeatit.cards.Card;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@Table(name = "card_collection")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SQLRestriction("deleted_at IS NULL")
public class CardCollection {
    @Id
    @Column(name = "card_collection_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private long id;

    @Column(name = "name")
    @Size(min = 1, max = 500)
    @NotBlank
    @ToString.Include
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_public")
    @ToString.Include
    private boolean isPublic;

    @Column(name = "author_id")
    @ToString.Include
    private long authorId;

    @OneToMany(mappedBy = "cardCollection")
    private List<Card> cards;

    @Nullable
    @Column(name = "parent_collection_id")
    private Long parentCollectionId;

    @Column(name = "is_excluded_from_training")
    private boolean isExcludedFromTraining;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static CardCollection forkCollection(final long userId, final CardCollection collection) {
        return new CardCollection(
                0,
                collection.name,
                collection.description,
                false,
                userId,
                List.of(),
                collection.id,
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    public CardCollection(final long userId, final String name) {
        this.authorId = userId;
        this.name = name;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


}
