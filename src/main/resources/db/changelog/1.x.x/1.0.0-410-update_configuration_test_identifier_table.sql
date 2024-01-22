-- liquibase formatted sql
-- changeset mprunier:1.0.0-410-update_configuration_test_identifier_table.sql

ALTER TABLE configuration_test_identifier_table
    RENAME COLUMN identifier TO tag;

ALTER TABLE configuration_test_identifier_table
    ADD CONSTRAINT fk__configuration_test_tag__configuration_test_id FOREIGN KEY (configuration_test_id) REFERENCES configuration_test (id) ON DELETE CASCADE,
    DROP CONSTRAINT fk__configuration_test_identifier_table__configuration_test_id;

ALTER TABLE configuration_test_identifier_table
    RENAME TO configuration_test_tag;

