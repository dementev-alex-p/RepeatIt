package com.github.dementev_alex_p.repeatit.training;

import com.github.dementev_alex_p.repeatit.training.trainig_cards.TrainingCard;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "training")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "user_id")
    @NotNull
    private long userId;

    @OneToMany(mappedBy = "trainingId")
    private List<TrainingCard> trainingCards;

    @Column(name = "started_at")
    @NotNull
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public Training(final long userId, final LocalDateTime startedAt) {
        this.userId = userId;
        this.startedAt = startedAt;
    }
}
