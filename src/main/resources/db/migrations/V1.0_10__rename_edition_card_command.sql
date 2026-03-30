SET search_path TO repeat_it;
UPDATE tg_message SET command = 'VIEW_CARD' WHERE command = 'EDIT_CARD';