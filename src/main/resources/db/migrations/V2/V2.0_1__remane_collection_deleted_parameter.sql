
ALTER TABLE card_collection
    ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE;

UPDATE card_collection SET deleted_at = CURRENT_TIMESTAMP WHERE is_deleted;

ALTER TABLE card_collection
    DROP COLUMN is_deleted;
