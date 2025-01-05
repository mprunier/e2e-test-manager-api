-- liquibase formatted sql
-- changeset mprunier:1.2.0-030-create_file_configuration_table.sql

CREATE TABLE file_configuration
(
    file_name      VARCHAR(1000),
    environment_id UUID,
    group_name     VARCHAR(255),
    created_by     VARCHAR(500)             NOT NULL,
    updated_by     VARCHAR(500),
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (file_name, environment_id)
);

CREATE INDEX idx_file_configuration_env ON file_configuration (environment_id);


-- rollback DROP TABLE IF EXISTS file_configuration;