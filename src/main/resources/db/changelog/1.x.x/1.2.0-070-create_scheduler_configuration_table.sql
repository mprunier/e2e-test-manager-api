-- liquibase formatted sql
-- changeset mprunier:1.2.0-070-create_scheduler_configuration_table.sql

CREATE TABLE scheduler_configuration
(
    environment_id UUID PRIMARY KEY,
    is_enabled     BOOLEAN                  NOT NULL,
    hour           INTEGER                  NOT NULL,
    minute         INTEGER                  NOT NULL,
    days_of_week   VARCHAR(255)             NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by     VARCHAR(255)             NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE,
    updated_by     VARCHAR(255)
);

INSERT INTO scheduler_configuration (environment_id, is_enabled, hour, minute, days_of_week, created_at, created_by, updated_at, updated_by)
VALUES ('8d8ea7dd-6115-4437-94f3-67c4999d9468', FALSE, 0, 0, 'MONDAY;TUESDAY;WEDNESDAY;THURSDAY;FRIDAY', CURRENT_TIMESTAMP, 'Init System', CURRENT_TIMESTAMP, 'Init System');

--rollback DROP TABLE scheduler_configuration;