CREATE  INDEX  IF NOT EXISTS message_user_id_created_at_idx
    ON message (user_id, created_at DESC);

CREATE  INDEX  IF NOT EXISTS card_user_id_not_deleted_partial
    ON card (user_id)
    WHERE deleted_at IS NULL;

CREATE  INDEX  IF NOT EXISTS card_collection_not_deleted_partial
    ON card (card_collection_id)
    WHERE deleted_at IS NULL;

CREATE  INDEX  IF NOT EXISTS card_collection_user_not_deleted_partial
    ON card_collection (author_id)
    WHERE deleted_at IS NULL;

CREATE  INDEX  IF NOT EXISTS training_user_not_finished_partial
    ON training (user_id)
    WHERE finished_at IS NULL;