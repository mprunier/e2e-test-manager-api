-- liquibase formatted sql
-- changeset mprunier:1.1.0-090-update_test_table.sql

ALTER TABLE test
    ADD COLUMN pipeline_id VARCHAR(255),
    ADD COLUMN is_waiting  BOOLEAN NOT NULL DEFAULT FALSE;

-- rollback ALTER TABLE test DROP COLUMN is_waiting;
-- rollback ALTER TABLE test DROP COLUMN pipeline_id;