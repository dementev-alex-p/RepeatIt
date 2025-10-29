package com.github.dementev_alex_p.repeatit.training.trainig_cards;

import lombok.Getter;

@Getter
public enum RecallScoreEnum {
      FAIL_RECALL("\uD83D\uDE29", 0),
      DIFFICULT_RECALL("\uD83E\uDD14", 3),
      PERFECT_RECALL("\uD83D\uDE03", 5)
      ;
      private final String text;
      private final int veight;

    RecallScoreEnum(String text, int veight) {
        this.text = text;
        this.veight = veight;
    }

}
