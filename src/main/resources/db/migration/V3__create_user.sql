CREATE TABLE users (
    id                      UUID PRIMARY KEY,
    email                   VARCHAR(254)  NOT NULL UNIQUE,
    password_hash           VARCHAR(72)   NOT NULL,
    name                    VARCHAR(120)  NOT NULL,
    phone_number            VARCHAR(20)   NOT NULL,
    enabled                 BOOLEAN       NOT NULL DEFAULT TRUE,
    clearance_expires_on    DATE          NOT NULL,
    skills_assessed_on      DATE          NOT NULL,
    created_at              TIMESTAMPTZ   NOT NULL DEFAULT now(),
    version                 BIGINT        NOT NULL DEFAULT 0,

    CONSTRAINT users_email_lowercase CHECK (email = lower(email))
);

CREATE TABLE user_roles (
    user_id  UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role     VARCHAR(40) NOT NULL,
    PRIMARY KEY (user_id, role)
);

CREATE TABLE refresh_tokens (
    id          UUID         PRIMARY KEY,
    user_id     UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_hash  CHAR(64)     NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ  NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens (user_id);