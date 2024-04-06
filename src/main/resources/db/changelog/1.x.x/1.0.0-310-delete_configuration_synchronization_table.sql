-- liquibase formatted sql
-- changeset mprunier:1.0.0-310-delete_configuration_synchronization_table.sql

DROP TABLE IF EXISTS configuration_synchronization;


-- rollback CREATE TABLE configuration_synchronization
-- rollback (
-- rollback     environment_id          BIGINT PRIMARY KEY,
-- rollback     status                  VARCHAR(50) NOT NULL,
-- rollback     last_synchronization_at TIMESTAMP WITH TIME ZONE,
-- rollback     error                   VARCHAR(500),
-- rollback     CONSTRAINT fk__configuration_suite__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE
-- rollback );
-- rollback
-- rollback ALTER TABLE environment
-- rollback     DROP COLUMN last_synchronization_at,
-- rollback     DROP COLUMN is_in_config_sync;
-- rollback
-- rollback INSERT INTO configuration_synchronization (environment_id, status, last_synchronization_at)
-- rollback VALUES ((SELECT id FROM environment LIMIT 1), 'NEVER_SYNC', TO_TIMESTAMP(0));

