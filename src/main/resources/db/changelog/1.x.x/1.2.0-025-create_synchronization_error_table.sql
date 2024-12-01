-- liquibase formatted sql
-- changeset mprunier:1.2.0-025-create_synchronization_error_table.sql

CREATE TABLE synchronization_error
(
    synchronization_id UUID      NOT NULL,
    file               TEXT      NOT NULL,
    error              TEXT      NOT NULL,
    error_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (synchronization_id, file, error, error_at),
    CONSTRAINT fk__synchronization_error__synchronization_id
        FOREIGN KEY (synchronization_id)
            REFERENCES synchronization (environment_id) ON DELETE CASCADE
);

-- rollback DROP TABLE IF EXISTS synchronization_error;


