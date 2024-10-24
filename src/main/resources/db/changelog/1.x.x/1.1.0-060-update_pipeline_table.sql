-- liquibase formatted sql
-- changeset mprunier:1.1.0-060-update_pipeline_table.sql

ALTER TABLE pipeline
    ADD COLUMN files_filter TEXT,
    DROP COLUMN report_error;

-- rollback DROP COLUMN files_filter;
