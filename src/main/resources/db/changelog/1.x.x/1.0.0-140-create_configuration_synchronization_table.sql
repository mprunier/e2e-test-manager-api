-- liquibase formatted sql
-- changeset mprunier:1.0.0-140-create_configuration_synchronization_table.sql

CREATE TABLE configuration_synchronization
(
    environment_id          BIGINT PRIMARY KEY,
    status                  VARCHAR(50) NOT NULL,
    last_synchronization_at TIMESTAMP WITH TIME ZONE,
    error                   VARCHAR(500),
    CONSTRAINT fk__configuration_suite__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE
);

ALTER TABLE environment
    DROP COLUMN last_synchronization_at,
    DROP COLUMN is_in_config_sync;

INSERT INTO configuration_synchronization (environment_id, status, last_synchronization_at)
VALUES ((SELECT id FROM environment LIMIT 1), 'NEVER_SYNC', TO_TIMESTAMP(0));


-- rollback DROP TABLE IF EXISTS configuration_synchronization;


