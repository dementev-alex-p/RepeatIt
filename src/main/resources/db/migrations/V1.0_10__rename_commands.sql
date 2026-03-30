UPDATE tg_message SET command = 'VIEW_CARD' WHERE command = 'EDIT_CARD';
UPDATE tg_message SET command = 'VIEW_CARD_LIST' WHERE command = 'CARDS';
UPDATE tg_message SET command = 'VIEW_COLLECTION_LIST' WHERE command = 'COLLECTIONS';
UPDATE tg_message SET command = 'VIEW_LIST' WHERE command = 'VIEW_SINGLE_COLLECTION';