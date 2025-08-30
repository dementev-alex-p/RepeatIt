CREATE SCHEMA repeat_it;

SET search_path TO repeat_it;

CREATE TABLE tg_user
(
    user_id   BIGINT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100)
);

CREATE TABLE card
(
    card_id    BIGSERIAL PRIMARY KEY,
    user_id    BIGINT                   NOT NULL REFERENCES tg_user (user_id),
    front_side TEXT                     NOT NULL,
    back_side  TEXT                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE user_state
(
    user_state_id BIGSERIAL PRIMARY KEY,
    user_id       BIGINT                   NOT NULL REFERENCES tg_user (user_id),
    command       VARCHAR(100)             NOT NULL,
    state_data    JSONB,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE practice
(
    practice_id              BIGSERIAL PRIMARY KEY,
    user_id                  BIGINT                   NOT NULL REFERENCES tg_user (user_id),
    current_practice_card_id BIGINT                   NOT NULL,
    start_at                 TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    end_at                   TIMESTAMP                NOT NULL DEFAULT NOW()
);

CREATE TABLE practice_card
(
    practice_card_id BIGSERIAL PRIMARY KEY,
    card_id          BIGINT NOT NULL REFERENCES card (card_id),
    was_known        BOOLEAN,
    reviewed_at      TIMESTAMP WITH TIME ZONE
);

ALTER TABLE practice
    ADD FOREIGN KEY (current_practice_card_id) REFERENCES practice_card (practice_card_id);