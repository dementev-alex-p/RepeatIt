CREATE SCHEMA IF NOT EXISTS repeat_it;

SET search_path TO repeat_it;

CREATE TABLE tg_user
(
    user_id  BIGINT PRIMARY KEY,
    username VARCHAR(100) NOT NULL
);

CREATE TABLE card_collection
(
    card_collection_id   BIGSERIAL PRIMARY KEY,
    author_id            BIGINT       NOT NULL REFERENCES tg_user (user_id),
    parent_collection_id BIGINT,
    name                 VARCHAR(100) NOT NULL,
    is_public            BOOLEAN      NOT NULL
);
ALTER TABLE card_collection ADD FOREIGN KEY (parent_collection_id) REFERENCES card_collection(card_collection_id);

CREATE TABLE card
(
    card_id            BIGSERIAL PRIMARY KEY,
    user_id            BIGINT NOT NULL REFERENCES tg_user (user_id),
    card_collection_id BIGINT REFERENCES card_collection (card_collection_id),
    front_side         TEXT,
    back_side          TEXT,
    streak             INTEGER NOT NULL DEFAULT 0,
    easiness_factor    DECIMAL(4,2) NOT NULL DEFAULT 2.5,
    interval_days      INTEGER NOT NULL DEFAULT 1,
    next_repeat_date   DATE DEFAULT (NOW() + INTERVAL '1 day'),
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
    practice_id BIGSERIAL PRIMARY KEY,
    user_id     BIGINT                    NOT NULL REFERENCES tg_user (user_id),
    started_at  TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT NOW(),
    finished_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TYPE recall_score AS ENUM (
  'FAIL_RECALL',
  'DIFFICULT_RECALL',
  'PERFECT_RECALL'
);

CREATE TABLE practice_card
(
    practice_card_id BIGSERIAL PRIMARY KEY,
    practice_id      BIGINT NOT NULL REFERENCES practice (practice_id),
    card_id          BIGINT NOT NULL REFERENCES card (card_id),
    recall_score     recall_score,
    reviewed_at      TIMESTAMP WITH TIME ZONE
);