-- liquibase formatted sql
-- changeset mprunier:1.0.0-010-create_environment_table.sql

CREATE TABLE environment
(
    id                      BIGSERIAL PRIMARY KEY,
    description             VARCHAR(255)             NOT NULL UNIQUE,
    project_id              VARCHAR(255)             NOT NULL,
    token                   VARCHAR(255)             NOT NULL,
    branch                  VARCHAR(255)             NOT NULL,
    is_enabled              BOOLEAN                  NOT NULL,
    last_synchronization_at TIMESTAMP,
    is_in_config_sync       BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_by              VARCHAR(500)             NOT NULL,
    updated_by              VARCHAR(500),
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITH TIME ZONE
);

INSERT INTO environment (description, project_id, token, branch, is_enabled, last_synchronization_at, is_in_config_sync, created_by, updated_by, created_at, updated_at)
VALUES ('To_Be_Defined', 'To_Be_Defined', 'To_Be_Defined', 'To_Be_Defined', TRUE, NULL, FALSE, 'Init System', 'Init System', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- rollback DROP TABLE environment;

