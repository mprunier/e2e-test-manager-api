-- liquibase formatted sql
-- changeset mprunier:1.0.0-040-create_test_configuration_table.sql

CREATE TABLE test_configuration
(
    id             UUID PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    status         VARCHAR(50)  NOT NULL DEFAULT 'NEW',
    suite_id       UUID,
    tags           VARCHAR[],
    variables      VARCHAR[],
    last_played_at TIMESTAMP WITH TIME ZONE,
    position       INTEGER,
    CONSTRAINT fk_suite_configuration
        FOREIGN KEY (suite_id)
            REFERENCES suite_configuration (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_test_configuration_suite ON test_configuration (suite_id);


-- rollback DROP TABLE IF EXISTS test_configuration;


