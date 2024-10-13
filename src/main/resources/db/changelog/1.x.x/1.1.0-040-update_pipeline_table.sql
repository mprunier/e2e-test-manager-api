-- liquibase formatted sql
-- changeset mprunier:1.1.0-040-update_pipeline_table.sql

ALTER TABLE pipeline
    ADD COLUMN parallel_pipeline_progress_id BIGINT,
    ADD COLUMN report_error                  VARCHAR(1000),
    ADD CONSTRAINT fk__pipeline__parallel_pipeline_progress_id FOREIGN KEY (parallel_pipeline_progress_id) REFERENCES environment (id) ON DELETE CASCADE;

-- rollback ALTER TABLE pipeline DROP COLUMN parallel_pipeline_progress_id;