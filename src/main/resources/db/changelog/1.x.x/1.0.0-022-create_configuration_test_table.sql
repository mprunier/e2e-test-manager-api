-- liquibase formatted sql
-- changeset mprunier:1.0.0-022-create_configuration_test_table.sql

CREATE TABLE configuration_test
(
    id BIGSERIAL PRIMARY KEY,
    environment_id         BIGINT        NOT NULL,
    status                 VARCHAR(255)  NOT NULL,
    file                   VARCHAR(500)  NOT NULL,
    title                  VARCHAR(1000) NOT NULL,
    configuration_suite_id BIGINT        NOT NULL,
    variables              VARCHAR(1000),
    created_at             TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at             TIMESTAMP WITH TIME ZONE,
    last_played_at         TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk__configuration_test__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE,
    CONSTRAINT fk__configuration_test__configuration_suite_id FOREIGN KEY (configuration_suite_id) REFERENCES configuration_suite (id) ON DELETE CASCADE
);

-- rollback DROP TABLE IF EXISTS configuration_test;



