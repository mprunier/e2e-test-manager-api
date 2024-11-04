-- liquibase formatted sql
-- changeset mprunier:1.0.0-370-update_test_table.sql

ALTER TABLE test
    DROP COLUMN pipeline_id;

-- rollback ALTER TABLE executor ADD COLUMN pipeline_id VARCHAR(255) NOT NULL;