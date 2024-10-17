-- liquibase formatted sql
-- changeset mprunier:1.1.0-040-update_pipeline_table.sql

ALTER TABLE pipeline
    ADD COLUMN type              VARCHAR(50),
    ADD COLUMN pipeline_group_id BIGINT,
    ADD COLUMN report_error      VARCHAR(1000),
    ADD CONSTRAINT fk__pipeline__pipeline_group_id FOREIGN KEY (pipeline_group_id) REFERENCES environment (id) ON DELETE CASCADE;

-- rollback ALTER TABLE pipeline DROP COLUMN type;
-- rollback ALTER TABLE pipeline DROP COLUMN pipeline_group_id;
-- rollback ALTER TABLE pipeline DROP COLUMN report_error;