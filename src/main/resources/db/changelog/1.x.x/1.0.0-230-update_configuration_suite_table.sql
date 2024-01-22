-- liquibase formatted sql
-- changeset mprunier:1.0.0-230-update_configuration_suite_table.sql
ALTER TABLE configuration_suite
    ADD COLUMN variables VARCHAR(1000);

-- rollback ALTER TABLE configuration_suite DROP COLUMN variables;