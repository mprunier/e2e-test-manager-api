-- liquibase formatted sql
-- changeset mprunier:1.0.0-150-create_configuration_scheduler_table.sql

CREATE TABLE configuration_scheduler
(
    environment_id BIGINT PRIMARY KEY,
    is_enabled     BOOLEAN NOT NULL,
    hour           INTEGER NOT NULL,
    minute         INTEGER NOT NULL,
    CONSTRAINT fk__configuration_suite__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE
);

INSERT INTO configuration_scheduler (environment_id, is_enabled, hour, minute)
VALUES ((SELECT id FROM environment LIMIT 1), TRUE, 23, 30);

-- rollback DROP TABLE IF EXISTS configuration_scheduler;


