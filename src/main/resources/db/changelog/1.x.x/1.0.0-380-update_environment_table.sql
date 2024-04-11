-- liquibase formatted sql
-- changeset mprunier:1.0.0-380-update_environment_table.sql

ALTER TABLE environment
    ADD COLUMN is_running_all_tests BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN last_all_tests_error VARCHAR(1000),
    DROP COLUMN scheduler_status;

-- rollback ALTER TABLE environment
-- rollback     ADD COLUMN scheduler_status VARCHAR(255) NOT NULL DEFAULT 'SUCCESS';