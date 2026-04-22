CREATE INDEX IF NOT EXISTS message_user_not_deleted_partial
    ON message (user_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS training_card_training_idx
    ON training_card (training_id);