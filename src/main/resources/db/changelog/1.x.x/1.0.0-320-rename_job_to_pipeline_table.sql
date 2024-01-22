-- liquibase formatted sql
-- changeset mprunier:1.0.0-320-rename_job_to_pipeline_table.sql

ALTER TABLE job RENAME TO pipeline;

-- rollback ALTER TABLE pipeline RENAME TO job;