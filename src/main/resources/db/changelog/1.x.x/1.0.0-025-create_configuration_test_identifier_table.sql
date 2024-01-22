-- liquibase formatted sql
-- changeset mprunier:1.0.0-025-create_configuration_test_identifier_table

CREATE TABLE configuration_test_identifier_table
(
    id BIGSERIAL PRIMARY KEY,
    configuration_test_id BIGINT       NOT NULL,
    identifier            VARCHAR(500) NOT NULL,
    environment_id        BIGINT       NOT NULL,
    CONSTRAINT fk__configuration_test_identifier_table__configuration_test_id FOREIGN KEY (configuration_test_id) REFERENCES configuration_test (id) ON DELETE CASCADE
);

-- rollback DROP TABLE IF EXISTS configuration_test_identifier_table;


