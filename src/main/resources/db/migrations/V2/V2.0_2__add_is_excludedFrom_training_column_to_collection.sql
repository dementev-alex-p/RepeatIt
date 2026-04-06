
ALTER TABLE card_collection
    ADD COLUMN is_excluded_from_training BOOLEAN NOT NULL DEFAULT FALSE;


