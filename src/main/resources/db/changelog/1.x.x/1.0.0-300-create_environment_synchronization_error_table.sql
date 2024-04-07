-- liquibase formatted sql
-- changeset mprunier:1.0.0-300-create_environment_synchronization_error_table.sql

CREATE TABLE environment_synchronization_error
(
    id             BIGSERIAL PRIMARY KEY,
    environment_id BIGINT        NOT NULL,
    file           VARCHAR(1000) NOT NULL,
    error          VARCHAR(1000),
    error_at       TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk__configuration_synchronization_error__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE
);

-- rollback DROP TABLE IF EXISTS environment_synchronization_error;


