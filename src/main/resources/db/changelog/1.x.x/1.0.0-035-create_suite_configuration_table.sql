-- liquibase formatted sql
-- changeset mprunier:1.0.0-035-create_suite_configuration_table.sql

CREATE TABLE suite_configuration
(
    id                                UUID PRIMARY KEY,
    title                             VARCHAR(1000) NOT NULL,
    status                            VARCHAR(50)   NOT NULL,
    file_configuration_name           VARCHAR(255),
    file_configuration_environment_id UUID,
    tags                              VARCHAR[],
    variables                         VARCHAR[],
    last_played_at                    TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_file_configuration
        FOREIGN KEY (file_configuration_name, file_configuration_environment_id)
            REFERENCES file_configuration (file_name, environment_id)
            ON DELETE CASCADE
);

CREATE INDEX idx_suite_configuration_file ON suite_configuration (file_configuration_name, file_configuration_environment_id);

-- rollback DROP TABLE IF EXISTS suite_configuration;


