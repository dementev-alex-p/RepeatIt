CREATE SCHEMA IF NOT EXISTS repeat_it;

SET
search_path TO repeat_it;

CREATE TABLE tg_user
(
    user_id  BIGINT PRIMARY KEY,
    username VARCHAR(100) NOT NULL
);

CREATE TABLE card_collection
(
    card_collection_id BIGSERIAL PRIMARY KEY,
    author_id          BIGINT NOT NULL REFERENCES tg_user(user_id),
    name               VARCHAR(100) NOT NULL,
    is_public          BOOLEAN      NOT NULL
);

CREATE TABLE card
(
    card_id            BIGSERIAL PRIMARY KEY,
    user_id            BIGINT NOT NULL REFERENCES tg_user (user_id),
    card_collection_id BIGINT REFERENCES card_collection (card_collection_id),
    front_side         TEXT,
    back_side          TEXT,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
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
    started_at               TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    finished_at              TIMESTAMP                NOT NULL DEFAULT NOW()
);

CREATE TABLE practice_card
(
    practice_card_id BIGSERIAL PRIMARY KEY,
    practice_id      BIGINT NOT NULL REFERENCES practice (practice_id),
    card_id          BIGINT NOT NULL REFERENCES card (card_id),
    was_known        BOOLEAN,
    reviewed_at      TIMESTAMP WITH TIME ZONE
);

ALTER TABLE practice
    ADD FOREIGN KEY (current_practice_card_id) REFERENCES practice_card (practice_card_id);