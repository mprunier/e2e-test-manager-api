-- liquibase formatted sql
-- changeset mprunier:1.0.0-110-create_worker_table.sql

CREATE TABLE worker
(
    id             UUID PRIMARY KEY,
    environment_id UUID                     NOT NULL,
    type           VARCHAR(50)              NOT NULL,
    variables      TEXT,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by     VARCHAR(255)             NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE,
    updated_by     VARCHAR(255)
);

CREATE INDEX idx_worker_environment_id ON worker (environment_id);

--rollback DROP TABLE worker;