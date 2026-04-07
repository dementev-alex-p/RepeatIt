DELETE FROM message
WHERE command = 'ADD_CARD';

UPDATE message SET command = 'VIEW_CARD_MENU'
WHERE command = 'VIEW_CARD_LIST';