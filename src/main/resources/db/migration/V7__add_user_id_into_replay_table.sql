ALTER TABLE replay
    ADD COLUMN user_id UUID;

UPDATE replay
SET user_id = (
    SELECT id
    FROM users
    LIMIT 1
);

ALTER TABLE replay
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE replay
    ADD CONSTRAINT fk_replays_user
        FOREIGN KEY (user_id)
            REFERENCES users(id);

CREATE INDEX idx_replays_user_id
    ON replay(user_id);

CREATE INDEX idx_replays_user_created_at
    ON replay(user_id, created_at DESC);