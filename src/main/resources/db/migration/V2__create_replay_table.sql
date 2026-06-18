CREATE TABLE replay (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE replay_event (
    id BIGSERIAL PRIMARY KEY,
    replay_id BIGINT NOT NULL,
    event_type VARCHAR(20) NOT NULL,
    content TEXT,
    file_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_replay_event FOREIGN KEY(replay_id) REFERENCES replay(id)
);