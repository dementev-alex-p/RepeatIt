package com.github.dementev_alex_p.repeatit.training;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.collection.CardCollection;
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
        findCurrentTraining(userId).ifPresent(this::finishTraining);
        final Training training = trainingRepository.save(new Training(userId, LocalDateTime.now()));
        final List<TrainingCard> trainingCards = trainingCardService.createCardsForTraining(cards, training.getId());
        training.setTrainingCards(trainingCards);
        return training;
    }

    public Training create(long userId, final CardCollection collection){
        findCurrentTraining(userId).ifPresent(this::finishTraining);

        final Training training = trainingRepository.save(new Training(userId, LocalDateTime.now(), collection));

        final List<TrainingCard> trainingCards = trainingCardService.createCardsForTraining(
                collection.getCards(), training.getId()
        );
        training.setTrainingCards(trainingCards);
        return training;
    }

    public Optional<Training> findCurrentTraining(long userId) {
        return trainingRepository.findByUserIdAndFinishedAtIsNull(userId);
    }

    public void finishTraining(final Training training) {
        trainingRepository.initFinishedAtByTrainingId(training.getId(), LocalDateTime.now());
    }

    public void deleteCardFromCurrentTraining(final Training training, final long cardId) {
        final Optional<TrainingCard> cardForDeletion = training
                .getTrainingCards()
                .stream()
                .filter(trainingCard -> trainingCard.getCardId() == cardId)
                .findAny();
        if (cardForDeletion.isEmpty()) {
            return;
        }

        //удаляем из тренировки для актуализации кеша persistence context
        final TrainingCard removedCard = training.getTrainingCards().remove(training.getTrainingCards().indexOf(cardForDeletion.get()));

        //удаляем из бд
        trainingCardService.delete(removedCard);

        //После удаления карточки нужно обновить нумерацию всех последующих карточек в тренировке
        final List<TrainingCard> trainingCardsForUpdate = training
                .getTrainingCards()
                .stream()
                .filter(trainingCard -> trainingCard.getOrderIndex() > removedCard.getOrderIndex())
                .toList();
        trainingCardService.incrementOrderIndexForCards(trainingCardsForUpdate);

    }
}
