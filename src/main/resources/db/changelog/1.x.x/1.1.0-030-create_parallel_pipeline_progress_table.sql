-- liquibase formatted sql
-- changeset mprunier:1.1.0-030-create_parallel_pipeline_progress_table.sql

CREATE TABLE parallel_pipeline_progress
(
    id                  BIGSERIAL PRIMARY KEY,
    environment_id      BIGINT NOT NULL,
    total_pipelines     BIGINT NOT NULL,
    completed_pipelines BIGINT NOT NULL,
    CONSTRAINT fk__parallel_pipeline_progress__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE
);


-- rollback DROP TABLE IF EXISTS parallel_pipeline_progress;


