package com.github.dementev_alex_p.repeatit.training;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.TrainingCard;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.TrainingCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TrainingService {
    private final TrainingRepository trainingRepository;
    private final TrainingCardService trainingCardService;

    public Training create(long userId, List<Card> cards){
        final Training training = trainingRepository.save(new Training(userId, LocalDateTime.now()));
        final List<TrainingCard> trainingCards = trainingCardService.createCardsForTraining(cards, training.getId());
        training.setTrainingCards(trainingCards);
        return training;
    }

    public Optional<Training> findCurrentTrainig(long userId) {
        return trainingRepository.findByUserIdAndFinishedAtIsNull(userId);
    }

    public void finishTraining(final Training training) {
        trainingRepository.initFinishedAtByTrainingId(training.getId(), LocalDateTime.now());
    }
}
