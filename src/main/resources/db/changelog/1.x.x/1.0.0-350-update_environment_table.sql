-- liquibase formatted sql
-- changeset mprunier:1.0.0-350-update_environment_table.sql

ALTER TABLE environment
    ADD COLUMN is_locked BOOLEAN NOT NULL DEFAULT FALSE;

-- rollback ALTER TABLE pipeline DROP COLUMN is_locked;