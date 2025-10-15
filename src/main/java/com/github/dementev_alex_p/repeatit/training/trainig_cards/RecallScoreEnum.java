package com.github.dementev_alex_p.repeatit.training.trainig_cards;

import lombok.Getter;

@Getter
public enum RecallScoreEnum {
      FAIL_RECALL("\uD83D\uDD34 0", 0),
      DIFFICULT_RECALL("\uD83D\uDFE1 50/50", 3),
      PERFECT_RECALL("\uD83D\uDFE2 100", 5)
      ;
      private final String text;
      private final int veight;

    RecallScoreEnum(String text, int veight) {
        this.text = text;
        this.veight = veight;
    }

}
