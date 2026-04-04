package com.github.dementev_alex_p.repeatit.cards.collection;

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
    @ToString.Exclude
    private long id;

    @Column(name = "name")
    @Size(min = 1, max = 100)
    @NotBlank
    private String name;

    @Column(name = "is_public")
    private boolean isPublic;

    @Column(name = "author_id")
    private long authorId;

    @OneToMany(mappedBy = "cardCollection")
    private List<Card> cards;

    @Nullable
    @Column(name = "parent_collection_id")
    private Long parentCollectionId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public CardCollection(final long userId, final String name, final long parentCollectionId, final boolean isPublic) {
        this.authorId = userId;
        this.name = name;
        this.parentCollectionId = parentCollectionId;
        this.isPublic = isPublic;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


}
