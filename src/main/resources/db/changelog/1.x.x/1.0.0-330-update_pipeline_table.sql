-- liquibase formatted sql
-- changeset mprunier:1.0.0-330-rename_job_to_pipeline_table.sql

ALTER TABLE pipeline
    ALTER COLUMN id TYPE VARCHAR(255) USING id::TEXT,
    DROP COLUMN pipeline_id,
    ADD COLUMN environment_id BIGINT NOT NULL DEFAULT 1,
    ADD CONSTRAINT fk__scheduler__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE;

-- rollback ALTER TABLE pipeline ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
-- rollback ALTER TABLE pipeline ADD COLUMN pipeline_id VARCHAR(255)             NOT NULL;
-- rollback ALTER TABLE pipeline DROP COLUMN environment_id;