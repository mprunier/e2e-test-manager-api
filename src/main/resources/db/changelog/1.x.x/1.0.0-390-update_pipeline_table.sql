-- liquibase formatted sql
-- changeset mprunier:1.0.0-390-update_pipeline_table.sql

ALTER TABLE pipeline
    DROP COLUMN type;

-- rollback ALTER TABLE pipeline ADD COLUMN type VARCHAR(255)             NOT NULL;