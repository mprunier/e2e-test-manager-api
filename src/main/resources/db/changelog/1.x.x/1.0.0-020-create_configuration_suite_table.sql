-- liquibase formatted sql
-- changeset mprunier:1.0.0-020-create_configuration_suite_table.sql

CREATE TABLE configuration_suite
(
    id BIGSERIAL PRIMARY KEY,
    environment_id                BIGINT        NOT NULL,
    status                        VARCHAR(255)  NOT NULL,
    file                          VARCHAR(500)  NOT NULL,
    title                         VARCHAR(1000) NOT NULL,
    parent_configuration_suite_id BIGINT,
    created_at                    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                    TIMESTAMP WITH TIME ZONE,
    last_played_at                TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk__configuration_suite__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE
);

-- rollback DROP TABLE IF EXISTS configuration_suite;


