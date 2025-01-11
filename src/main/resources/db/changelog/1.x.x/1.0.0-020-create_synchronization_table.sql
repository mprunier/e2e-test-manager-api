-- liquibase formatted sql
-- changeset mprunier:1.0.0-020-create_synchronization_table.sql

CREATE TABLE synchronization
(
    environment_id UUID PRIMARY KEY,
    is_in_progress BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_by     VARCHAR(500)             NOT NULL,
    updated_by     VARCHAR(500),
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx__synchronization__environment_id ON synchronization (environment_id);

INSERT INTO synchronization (environment_id, is_in_progress, created_by, updated_by, created_at, updated_at)
VALUES ('8d8ea7dd-6115-4437-94f3-67c4999d9468', FALSE, 'Init System', 'Init System', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- rollback DROP TABLE synchronization;

