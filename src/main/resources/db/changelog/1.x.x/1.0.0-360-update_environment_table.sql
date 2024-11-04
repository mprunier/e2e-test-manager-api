-- liquibase formatted sql
-- changeset mprunier:1.0.0-360-update_environment_table.sql

ALTER TABLE environment
    ADD COLUMN scheduler_status VARCHAR(255) NOT NULL DEFAULT 'SUCCESS';

-- rollback ALTER TABLE executor DROP COLUMN scheduler_status;