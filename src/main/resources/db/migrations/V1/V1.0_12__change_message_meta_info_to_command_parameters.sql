
ALTER TABLE tg_message
    DROP COLUMN message_meta_info;

ALTER TABLE tg_message
    ADD COLUMN command_parameters JSONB;
