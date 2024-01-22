-- liquibase formatted sql
-- changeset mprunier:1.0.0-060-create_job_table.sql

CREATE TABLE job
(
    id          BIGSERIAL PRIMARY KEY,
    pipeline_id VARCHAR(255)             NOT NULL,
    type        VARCHAR(255)             NOT NULL,
    status      VARCHAR(255)             NOT NULL,
    test_ids    TEXT,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE
);

-- rollback DROP TABLE IF EXISTS job;
