-- liquibase formatted sql
-- changeset mprunier:1.0.0-050-create_scheduler_table.sql

CREATE TABLE scheduler
(
    id             BIGSERIAL PRIMARY KEY,
    environment_id BIGINT                   NOT NULL,
    pipeline_id    VARCHAR(255),
    status         VARCHAR(255)             NOT NULL,
    suites         INTEGER,
    tests          INTEGER,
    passes         INTEGER,
    failures       INTEGER,
    skipped        INTEGER,
    pass_percent   INTEGER,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE,
    created_by     VARCHAR(500)             NOT NULL,
    CONSTRAINT fk__scheduler__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE
);
-- rollback DROP TABLE IF EXISTS scheduler;
