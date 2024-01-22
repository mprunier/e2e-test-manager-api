-- liquibase formatted sql
-- changeset mprunier:1.0.0-400-update_metrics_table.sql

ALTER TABLE metrics
    ADD COLUMN is_all_tests_run BOOLEAN NOT NULL DEFAULT TRUE;

-- rollback ALTER TABLE metrics DROP COLUMN is_all_tests_run