CREATE TABLE users
(
    id UUID PRIMARY KEY,

    firebase_uid VARCHAR(255) NOT NULL UNIQUE,

    email VARCHAR(255) NOT NULL UNIQUE,

    name VARCHAR(255) NOT NULL,

    picture_url TEXT,

    created_at TIMESTAMP NOT NULL,

    last_login TIMESTAMP NOT NULL
);