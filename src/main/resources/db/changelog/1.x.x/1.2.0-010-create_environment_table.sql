-- liquibase formatted sql
-- changeset mprunier:1.0.0-010-create_environment_table.sql

CREATE TABLE environment
(
    id                       UUID PRIMARY KEY,
    description              VARCHAR(255)             NOT NULL UNIQUE,
    project_id               VARCHAR(255)             NOT NULL,
    token                    VARCHAR(255)             NOT NULL,
    branch                   VARCHAR(255)             NOT NULL,
    is_enabled               BOOLEAN DEFAULT TRUE     NOT NULL,
    max_parallel_test_number INTEGER DEFAULT 1        NOT NULL,
    created_by               VARCHAR(500)             NOT NULL,
    updated_by               VARCHAR(500),
    created_at               TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITH TIME ZONE

);

INSERT INTO environment (id, description, project_id, token, branch, is_enabled, max_parallel_test_number, created_by, updated_by, created_at, updated_at)
VALUES ('8d8ea7dd-6115-4437-94f3-67c4999d9468', 'To_Be_Defined', 'To_Be_Defined', 'To_Be_Defined', 'To_Be_Defined', TRUE, 1, 'Init System', 'Init System', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- rollback DROP TABLE environment;

