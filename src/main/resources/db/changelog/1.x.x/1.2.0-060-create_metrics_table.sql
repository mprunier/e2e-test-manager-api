-- liquibase formatted sql
-- changeset mprunier:1.2.0-060-create_metrics_table.sql

CREATE TABLE metrics
(
    id             UUID PRIMARY KEY,
    environment_id UUID                     NOT NULL,
    type           VARCHAR(50)              NOT NULL,
    suites         INTEGER,
    tests          INTEGER,
    passes         INTEGER,
    failures       INTEGER,
    skipped        INTEGER,
    pass_percent   INTEGER,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_metrics_environment_id ON metrics (environment_id);

--rollback DROP TABLE metrics;