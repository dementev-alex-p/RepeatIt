package com.github.dementev_alex_p.repeatit.user_states;

import com.github.dementev_alex_p.repeatit.cards.Card;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class TrainingAdditionData implements AdditionData{
    private final List<Card> cardsForStudy;
    private final int totalNumber;
    private final AtomicInteger currentNumber;
    private final AtomicInteger successNumber;
    private final AtomicInteger failNumber;



    public TrainingAdditionData(List<Card> cardsForStudy) {
        this.cardsForStudy = cardsForStudy;
        this.totalNumber = cardsForStudy.size();
        currentNumber = new AtomicInteger(1);
        successNumber = new AtomicInteger();
        failNumber = new AtomicInteger();
    }

}
