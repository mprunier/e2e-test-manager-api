-- liquibase formatted sql
-- changeset mprunier:1.1.0-070-update_test_table.sql

ALTER TABLE test
    DROP COLUMN updated_at;

-- rollback ALTER TABLE test DROP COLUMN updated_at;