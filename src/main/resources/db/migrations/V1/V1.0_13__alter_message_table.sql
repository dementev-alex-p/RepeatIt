
CREATE TABLE IF NOT EXISTS message
(
    message_id             BIGSERIAL PRIMARY KEY,
    tg_message_id          INT                      NOT NULL,
    user_id                BIGINT                   NOT NULL REFERENCES tg_user (user_id),
    chat_id                BIGINT                   NOT NULL,
    command                TEXT                     NOT NULL,
    message_text           TEXT                     NOT NULL,
    parameters             JSONB,
    is_answer_excepted     BOOLEAN                  NOT NULL DEFAULT false,
    is_chat_clear_required BOOLEAN                  NOT NULL DEFAULT false,
    alert_message          TEXT,
    created_at             TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at             TIMESTAMP WITH TIME ZONE
) ;

INSERT INTO message (tg_message_id, user_id, chat_id, command, message_text, created_at, deleted_at)
SELECT
    tg_message_id,
    user_id,
    chat_id,
    command,
    message_text,
    created_at,
    CASE WHEN is_deleted THEN CURRENT_TIMESTAMP END
FROM tg_message;

DROP TABLE tg_message;
