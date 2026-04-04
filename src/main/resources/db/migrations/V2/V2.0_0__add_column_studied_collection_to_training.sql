
ALTER TABLE training
    ADD COLUMN studied_collection_id BIGINT REFERENCES card_collection (card_collection_id);
