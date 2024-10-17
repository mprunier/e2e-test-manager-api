-- liquibase formatted sql
-- changeset mprunier:1.1.0-020-update_environment_table.sql

ALTER TABLE environment
    ADD COLUMN max_parallel_test_number INT NOT NULL DEFAULT 1,
    DROP COLUMN last_all_tests_error,
    DROP COLUMN is_running_all_tests;

-- rollback ALTER TABLE environment DROP COLUMN max_parallel_test_number,
-- rollback ADD COLUMN is_running_all_tests BOOLEAN NOT NULL DEFAULT FALSE,
-- rollback ADD COLUMN last_all_tests_error VARCHAR(1000);