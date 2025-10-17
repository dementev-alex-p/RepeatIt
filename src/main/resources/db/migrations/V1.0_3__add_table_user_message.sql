SET search_path TO repeat_it;
DROP TABLE tg_message;
CREATE TABLE tg_message (
    tg_message_id INT PRIMARY KEY ,
    user_id BIGINT NOT NULL REFERENCES tg_user(user_id),
    chat_id BIGINT NOT NULL,
    command TEXT NOT NULL,
    message_text TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false
);