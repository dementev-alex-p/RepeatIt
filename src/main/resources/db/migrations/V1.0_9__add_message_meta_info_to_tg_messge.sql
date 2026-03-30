SET search_path TO repeat_it;
ALTER TABLE tg_message
ADD COLUMN message_meta_info VARCHAR(100);
