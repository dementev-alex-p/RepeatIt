ALTER TABLE card_collection
    ALTER COLUMN name TYPE VARCHAR(500) using name::VARCHAR(500);