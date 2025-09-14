package com.github.dementev_alex_p.repeatit.cards.collection;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.users.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@Table(name = "card_collection")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CardCollection {
    @Id
    @Column(name = "card_collection_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    @Size(min = 1, max = 100)
    @NotBlank
    private String name;

    @Column(name = "is_public")
    private boolean isPublic;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(mappedBy = "cardCollectionId")
    private List<Card> cards;

    @Nullable
    @Column(name = "parent_collection_id")
    private Long parentCollectionId;

    public CardCollection(final User user, final String name, final long parentCollectionId, final boolean isPublic) {
        this.author = user;
        this.name = name;
        this.parentCollectionId = parentCollectionId;
        this.isPublic = isPublic;
    }


}
