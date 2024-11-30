-- liquibase formatted sql
-- changeset mprunier:1.0.0-030-create_test_table.sql

CREATE TABLE test
(
    id                    BIGSERIAL PRIMARY KEY,
    configuration_test_id BIGINT                   NOT NULL,
    pipeline_id           VARCHAR(255)             NOT NULL,
    status                VARCHAR(255)             NOT NULL,
    reference             VARCHAR(255),
    error_url             VARCHAR(500),
    error_message         TEXT,
    error_stacktrace      TEXT,
    code                  TEXT,
    duration              INTEGER,
    video                 BYTEA,
    variables             VARCHAR(1000),
    created_by            VARCHAR(500)             NOT NULL,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk__test__configuration_test_id FOREIGN KEY (configuration_test_id) REFERENCES configuration_test (id) ON DELETE CASCADE
);


-- rollback DROP TABLE IF EXISTS testFilter;
