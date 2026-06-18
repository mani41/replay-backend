DROP TABLE IF EXISTS replay_event_embedding;
CREATE TABLE replay_event_embedding
(
    replay_event_id BIGINT NOT NULL,
    model           VARCHAR(50),
    embedding       VECTOR(768) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_replay_event_embedding PRIMARY KEY (replay_event_id)
);