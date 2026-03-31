package com.github.dementev_alex_p.repeatit.training.trainig_cards;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "training_card")
@Getter
@DynamicInsert
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TrainingCard {

    @Id
    @Column(name = "training_card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long trainingCardId;

    @Column(name = "training_id")
    private Long trainingId;

    @Column(name = "card_id")
    private long cardId;

    @Column(name = "order_index")
    private int orderIndex;

    @Column(name = "recall_score")
    @Enumerated(EnumType.STRING)
    private RecallScoreEnum recallScore;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    public TrainingCard(final long cardId, final long trainingId,  final int orderIndex) {
        this.cardId = cardId;
        this.trainingId = trainingId;
        this.orderIndex = orderIndex;
    }
}
