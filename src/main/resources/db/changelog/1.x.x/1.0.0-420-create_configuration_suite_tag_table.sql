-- liquibase formatted sql
-- changeset mprunier:1.0.0-420-create_configuration_suite_tag_table.sql

CREATE TABLE configuration_suite_tag
(
    id                     BIGSERIAL PRIMARY KEY,
    configuration_suite_id BIGINT       NOT NULL,
    tag                    VARCHAR(500) NOT NULL,
    environment_id         BIGINT       NOT NULL,
    CONSTRAINT fk__configuration_suite_tag__configuration_suite_id FOREIGN KEY (configuration_suite_id) REFERENCES configuration_suite (id) ON DELETE CASCADE
);

-- rollback DROP TABLE IF EXISTS configuration_suite_tag;


