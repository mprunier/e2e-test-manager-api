-- liquibase formatted sql
-- changeset mprunier:1.1.0-080-update_configuration_test_table.sql

ALTER TABLE configuration_test
    ADD COLUMN position INTEGER;

-- rollback ALTER TABLE configuration_test DROP COLUMN position;