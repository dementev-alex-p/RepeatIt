package com.github.dementev_alex_p.repeatit.training.trainig_cards;

import lombok.Getter;

@Getter
public enum RecallScoreEnum {
      FAIL_RECALL("Не помню"),
      DIFFICULT_RECALL("Вспомнилось с трудом"),
      PERFECT_RECALL("Помню")
      ;
      private final String text;

    RecallScoreEnum(String text) {
        this.text = text;
    }

}
