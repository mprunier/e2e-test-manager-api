-- liquibase formatted sql
-- changeset mprunier:1.0.0-015-create_environment_variable_table.sql

CREATE TABLE environment_variable
(
    environment_id UUID                  NOT NULL,
    name           VARCHAR(500)          NOT NULL,
    default_value  VARCHAR(1000)         NOT NULL,
    is_hidden      BOOLEAN DEFAULT FALSE NOT NULL,
    description    VARCHAR(1000),
    PRIMARY KEY (environment_id, name),
    CONSTRAINT fk__environment_variable__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE
);

-- rollback DROP TABLE IF EXISTS environment_variable;


