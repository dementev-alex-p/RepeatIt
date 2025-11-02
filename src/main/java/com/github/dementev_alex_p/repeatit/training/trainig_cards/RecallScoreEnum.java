package com.github.dementev_alex_p.repeatit.training.trainig_cards;

import lombok.Getter;

@Getter
public enum RecallScoreEnum {
      FAIL_RECALL("❓", 0),
      DIFFICULT_RECALL("⏳", 3),
      PERFECT_RECALL("🚀", 5)
      ;
      private final String text;
      private final int veight;

    RecallScoreEnum(String text, int veight) {
        this.text = text;
        this.veight = veight;
    }

}
