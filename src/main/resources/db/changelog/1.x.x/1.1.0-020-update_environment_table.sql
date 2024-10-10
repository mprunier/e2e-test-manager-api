-- liquibase formatted sql
-- changeset mprunier:1.1.0-020-update_environment_table.sql

ALTER TABLE environment
    ADD COLUMN max_parallel_test_number INT NOT NULL DEFAULT 1;

-- rollback ALTER TABLE environment DROP COLUMN max_parallel_test_number;